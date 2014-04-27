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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static long mainID;
    private static List<CommandHolder> commands;
    public static boolean end = false;


    public static GHRepository OpenMC;
    public static List<String> pullIgnores = new ArrayList<String>();

    public static Thread looper;

    public static Process serverProcess;
    public static final String COMMAND = "java -Xmx1024M -Xms1024M -jar server/craftbukkit.jar";
    public static void main(String[] args) {
        mainID = Thread.currentThread().getId();

        log("Loading commands from " + Commands.class.getCanonicalName() + "..");
        commands = CommandHolder.lazyLoadCommands(Commands.class);

        log("Reading from passsword file..");
        try {
            String text = FileUtils.readAllLines("github.dat")[0];

            String username = text.split(":")[0];
            String password = text.split(":")[1];

            log("Logging into github..");
            GitHub github = GitHub.connectUsingPassword(username, password);
            log("Getting repo..");
            OpenMC = github.getRepository("GamezGalaxy2/OpenMC");

            log("Reading .pullignore file..");
            pullIgnores = FileUtils.readToList(".pullignore");

            log("Staritng server in separate process..");
            serverProcess = Runtime.getRuntime().exec(COMMAND);

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
            for (CommandHolder c : commands) {
                if (c.matches(line))
                    c.invoke();
            }
        }
    }
    
    public static void log(String text) {
        if (Thread.currentThread().getId() != mainID)
            System.err.println(text);
        else
            System.out.println(text);
    }

    public static void stopServer() {
        log("Sending stop command to server..");
        PrintStream pout = new PrintStream(serverProcess.getOutputStream());
        pout.println("/stop");
        try {
            log("Waiting for server to stop..");
            int value = serverProcess.waitFor();
            log("Server exited with exit code " + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pull the repo for new changes
     */
    public static void pullRepo() throws IOException, InterruptedException {
        Process gitProcess = Runtime.getRuntime().exec("git fetch && git reset --hard origin/master");
        BufferedReader input = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            log(line);
        }

        BufferedReader input2 = new BufferedReader(new InputStreamReader(gitProcess.getErrorStream()));
        String line2;
        while ((line2 = input2.readLine()) != null) {
            log(line2);
        }

        gitProcess.waitFor();
    }

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
                        serverProcess = Runtime.getRuntime().exec(COMMAND);
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
