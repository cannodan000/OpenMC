package com.gamezgalaxy.openmc;

import com.gamezgalaxy.openmc.system.FileUtils;
import com.gamezgalaxy.openmc.system.commands.CommandHolder;
import com.gamezgalaxy.openmc.system.commands.Commands;
import org.joda.time.DateTime;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static long mainID;
    public static List<CommandHolder> commands;
    public static boolean end = false;


    public static GHRepository OpenMC;
    public static String username, password;
    public static List<String> pullIgnores = new ArrayList<String>();

    public static Thread looper;

    public static Process serverProcess;
    public static BufferedReader serverReader;
    public static PrintWriter serverWriter;
    public static final String COMMAND = "java -Xmx1024M -Xms1024M -jar craftbukkit.jar";
    public static void main(String[] args) {
        mainID = Thread.currentThread().getId();

        log("Loading commands from " + Commands.class.getCanonicalName() + "..");
        commands = CommandHolder.lazyLoadCommands(Commands.class);

        log("Reading from passsword file..");
        try {
            String text = FileUtils.readAllLines("data/github.dat")[0];

            username = text.split(":")[0];
            password = text.split(":")[1];

            log("Loading cache data..");
            loadAnnounced();

            log("Logging into github..");
            GitHub github = GitHub.connectUsingPassword(username, password);
            log("Getting repo..");
            OpenMC = github.getRepository("GamezGalaxy2/OpenMC");

            log("Reading .pullignore file..");
            pullIgnores = FileUtils.readToList("data/.pullignore");

            log("Staritng server in separate process..");
            startServer();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        log("Starting main loop!");
        looper = new Thread(MAIN_LOOP);
        looper.start();
        final Scanner scan = new Scanner(System.in);
        while (!end) {
            String line = scan.nextLine();
            char cmd = line.toCharArray()[0];
            line = line.substring(1).trim();
            for (CommandHolder c : commands) {
                if (c.matches(cmd)) {
                    c.invoke(line);
                }
            }
        }
    }

    public static void startServer() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(COMMAND.split(" "));
        pb.directory(new File("server/"));
        serverProcess = pb.start();
        serverReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
        serverWriter = new PrintWriter(serverProcess.getOutputStream());
    }

    public static void log(String text) {
        if (Thread.currentThread().getId() != mainID)
            System.err.println(text);
        else
            System.out.println(text);
    }

    public static void stopServer() {
        log("Sending stop command to server..");
        serverWriter.println("stop");
        serverWriter.flush();
        try {
            log("Waiting for server to stop..");
            int value = serverProcess.waitFor();
            log("Server exited with exit code " + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverWriter.close();
        try {
            serverReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isServerProcessAlive() {
        try {
            serverProcess.exitValue();
        } catch (IllegalThreadStateException e) {
            return true;
        }
        return false;
    }

    /**
     * Pull the repo for new changes
     */
    public static void pullRepo() throws IOException, InterruptedException {
        log("Commiting any changes..");
        runCommand("git add .");
        runCommand("git commit -m \"Updating server files\"");
        runCommand("git pull"); //TODO Maybe avoid conflicts..?
        runCommand("git push", username, password);
    }

    private static void saveAnnounced() throws IOException {
        String[] lines = new String[announced_pulls.size()];
        for (int i = 0; i < announced_pulls.size(); i++) {
            lines[i] = "" + announced_pulls.get(i);
        }

        FileUtils.writeLines("data/pulls.cache", lines);
    }

    private static void loadAnnounced() throws IOException {
        List<String> list = FileUtils.readToList("data/pulls.cache");

        for (String s : list) {
            try {
                announced_pulls.add(Integer.parseInt(s));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    //git fetch && git reset --hard origin/master
    private static void runCommand(String cmd, String... toSend) throws IOException, InterruptedException {
        Process gitProcess = Runtime.getRuntime().exec(cmd);
        if (toSend.length > 0) {
            PrintStream pout = new PrintStream(gitProcess.getOutputStream());
            for (String s : toSend) {
                pout.println(s);
            }
            pout.flush();
            pout.close();
        }
        BufferedReader input = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            log(line);
        }

        input.close();

        BufferedReader input2 = new BufferedReader(new InputStreamReader(gitProcess.getErrorStream()));
        String line2;
        while ((line2 = input2.readLine()) != null) {
            log(line2);
        }

        input2.close();

        gitProcess.waitFor();
    }

    private static List<Integer> announced_pulls = new ArrayList<>();
    private static final Runnable MAIN_LOOP = new Runnable() {
        @Override
        public void run() {
            DateTime lastPull = DateTime.now();
            while (!end) {
                try {
                    log("Checking for pull requests..");
                    List<GHPullRequest> pulls = OpenMC.getPullRequests(GHIssueState.OPEN);
                    Iterator<GHPullRequest> iterator = pulls.iterator();
                    while (iterator.hasNext()) {
                        GHPullRequest request = iterator.next();
                        if (pullIgnores.contains(request.getUser().getLogin())) {
                            request.comment("You are banned from making pull requests. This pull request shall be closed.");
                            request.close();
                            log("Closed banned pull request: " + request.getTitle());
                            iterator.remove();
                        }
                        if (!announced_pulls.contains(request.getNumber())) {
                            Commands.sendCmd("say §l§aNew Pull Request by §r§o" + request.getUser().getLogin() + "§r! | §n" + request.getTitle());

                            announced_pulls.add(request.getNumber());
                            saveAnnounced();
                        }
                    }
                    log("There are " + pulls.size() + " open pull requests!");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DateTime time = DateTime.now();
                if (time.getHourOfDay() == 0 && lastPull.getDayOfMonth() != time.getDayOfMonth()) { //It's midnight!
                    //TODO Announce to server to shutdown
                    stopServer();

                    try {
                        log("Pulling from repo..");
                        pullRepo();

                        log("Starting server again..");
                        startServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lastPull = time;
                }

                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
