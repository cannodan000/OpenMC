package com.gamezgalaxy.openmc.system.commands;

import com.gamezgalaxy.openmc.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Commands {

    @Command(
            command = 'h',
            description = "The help menu"
    )
    public static void help(String line) {
        for (CommandHolder holder : Main.commands) {
            System.out.println(holder.getCommand().command() + "    -    " + holder.getCommand().description());
        }
    }

    @Command(
            command = 'p',
            description = "Force a server update."
    )
    public static void forcePull(String line) {
        Main.log("==FORCE UPDATE STARTED==");
        Main.stopServer();

        try {
            Main.log("Pulling from repo..");
            Main.pullRepo();

            Main.log("Starting server again..");
            Main.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Main.log("==FORCE UPDATE COMPLETE==");
    }

    @Command(
            command = 'r',
            description = "Restart the server."
    )
    public static void restart(String line) {
        Main.stopServer();
        Main.log("Starting server again..");
        try {
            Main.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static boolean sview = false;
    static Thread printThread;
    @Command(
            command = 's',
            description = "Toggle view of server output"
    )
    public static void serverView(String line) {
        sview = !sview;
        if (sview) {
            printThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Main.isServerProcessAlive()) {
                        Main.log("The server is not online! Try using \'r\' to restart it..");
                    }
                    String line2;
                    try {
                        while ((line2 = Main.serverReader.readLine()) != null) {
                            if (Thread.currentThread().isInterrupted())
                                break;
                            Main.log(line2);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sview = false;
                    Main.log("Server output closed..");
                }
            });
            printThread.start();
            Main.log("Now viewing server output!");
        } else {
            if (printThread != null && printThread.isAlive()) {
                Main.log("Please wait while the server output is closed...");
                printThread.interrupt();
                try {
                    printThread.join(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Main.log("Server output closed!");
            }
        }
    }



    @Command(
            command = 't',
            description = "Terminate the bot"
    )
    public static void terminate(String line) {
        Main.log("Closing..");
        Main.end = true;

        Main.stopServer();

        Main.looper.interrupt();

        System.exit(0);
    }

    @Command(
            command = '/',
            description = "/<cmd>, Send command to the server. Example \"/say Hi\""
    )
    public static void sendCmd(String line) {
        if (Main.serverProcess != null && Main.isServerProcessAlive()) {
            Main.serverWriter.println(line);
            Main.serverWriter.flush();
        }
    }
}
