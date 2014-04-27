package com.gamezgalaxy.openmc;

import com.gamezgalaxy.openmc.system.FileUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static GHRepository OpenMC;
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


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
