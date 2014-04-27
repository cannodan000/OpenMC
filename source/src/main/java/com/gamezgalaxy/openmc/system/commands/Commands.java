package com.gamezgalaxy.openmc.system.commands;

import com.gamezgalaxy.openmc.Main;

public class Commands {

    @Command(
            command = 'h',
            description = "The help menu"
    )
    public static void help() {

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
