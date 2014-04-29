package com.gamezgalaxy.openmc.system.commands;

import com.gamezgalaxy.openmc.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Commands {

    @Command(
            command = 'h',
            description = "The help menu"
    )
    public static void help() {
        for (CommandHolder holder : Main.commands) {
            System.out.println(holder.getCommand().command() + "    -    " + holder.getCommand().description());
        }
    }

    @Command(
            command = 'p',
            description = "Force a server update."
    )
    public static void forcePull() {
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
    public static void restart() {
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
    public static void serverView() {
        sview = !sview;
        if (sview) {
            printThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Main.isServerProcessAlive()) {
                        Main.log("The server is not online! Try using \'r\' to restart it..");
                    }
                    BufferedReader input2 = new BufferedReader(new InputStreamReader(Main.serverProcess.getInputStream()));
                    String line2;
                    try {
                        while ((line2 = input2.readLine()) != null) {
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
                printThread.interrupt();
                try {
                    printThread.join(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Main.log("Server output closed..");
            }
        }
    }



    @Command(
            command = 't',
            description = "Terminate the bot"
    )
    public static void terminate() {
        Main.log("Closing..");
        Main.end = true;

        Main.stopServer();

        Main.looper.interrupt();

        System.exit(0);
    }
}
