package com.gamezgalaxy.openmc;

import com.gamezgalaxy.openmc.system.FileUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static GHRepository OpenMC;
    public static List<String> pullIgnores = new ArrayList<String>();
    public static void main(String[] args) {
        System.out.println("Reading from passsword file..");
        try {
            String text = FileUtils.readAllLines("github.dat")[0];

            String username = text.split(":")[0];
            String password = text.split(":")[1];

            System.out.println("Logging into github..");
            GitHub github = GitHub.connectUsingPassword(username, password);
            System.out.println("Getting repo..");
            OpenMC = github.getRepository("GamezGalaxy2/OpenMC");

            System.out.println("Reading .pullignore file..");
            pullIgnores = FileUtils.readToList(".pullignore");

            System.out.println("Pull latest changes..");
            pullRepo();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Entering main loop!");
        while (true) {

        }
    }

    /**
     * Pull the repo for new changes
     */
    public static void pullRepo() throws IOException {
        Process gitProcess = Runtime.getRuntime().exec("git fetch && git reset --hard origin/master");
        BufferedReader input = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        BufferedReader input2 = new BufferedReader(new InputStreamReader(gitProcess.getErrorStream()));
        String line2;
        while ((line2 = input2.readLine()) != null) {
            System.out.println(line2);
        }
    }
}
