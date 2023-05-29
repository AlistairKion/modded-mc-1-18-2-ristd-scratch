package com.github.alistairkion.pullpackwizforgeversion;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Scanner;

public class Main {

    private static final String FORGE_VERSION_PLACEHOLDER = "(FORGE_VERSION)";
    private static final String MINECRAFT_VERSION_PLACEHOLDER = "(MINECRAFT_VERSION)";

    /**
     * Reads the pack.toml at the given url, pulls out the forge and minecraft
     * version information, and then runs the passed in shell command, optionally
     * inserting the version information.
     *
     * @param args Expected args: -h, or http link to packwiz pack.toml and command to run
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                printHelpMessage();
                return;
            }
        }
        if (args.length < 2) {
            System.out.println("You must pass in a link to the packwiz pack.toml and the shell command to run");
            printHelpMessage();
            return;
        }

        String url = args[0];
        String rawShellCommand = args[1];
        boolean needsForgeVersion = rawShellCommand.contains(FORGE_VERSION_PLACEHOLDER);
        boolean needsMinecraftVersion = rawShellCommand.contains(MINECRAFT_VERSION_PLACEHOLDER);

        // read from url
        String forgeVersion = null;
        String minecraftVersion = null;
        try (InputStream stream = new URL(url).openStream()) {
            try (Scanner scan = new Scanner(stream)) {
                String line;
                String forgeCheck = "forge = ";
                String minecraftCheck = "minecraft = ";
                while (scan.hasNext()) {
                    line = scan.nextLine();

                    if (line.contains(forgeCheck)) {
                        forgeVersion = line
                            .replace(forgeCheck, "")
                            .replace("\"", "")
                            .trim();
                    }
                    if (line.contains(minecraftCheck)) {
                        minecraftVersion = line
                            .replace(minecraftCheck, "")
                            .replace("\"", "")
                            .trim();
                    }
                }
            }
        }

        if (forgeVersion == null && minecraftVersion == null) {
            System.out.println("pack.toml url read successfully, but no forge or minecraft version was found");
            if (needsForgeVersion || needsMinecraftVersion) {
                System.exit(1);
            }
        } else if (forgeVersion == null) {
            System.out.printf(
                "pack.toml url read successfully, and found minecraft version %s, but no forge version was found\n",
                minecraftVersion
            );
            if (needsForgeVersion) {
                System.exit(1);
            }
        } else if (minecraftVersion == null) {
            System.out.printf(
                "pack.toml url read successfully, and found forge version %s, but no minecraft version was found\n",
                forgeVersion
            );
            if (needsMinecraftVersion) {
                System.exit(1);
            }
        } else {
            System.out.printf(
                "pack.toml url read successfully, and found forge version %s, and minecraft version %s\n",
                forgeVersion,
                minecraftVersion
            );
        }

        if (forgeVersion == null) {
            forgeVersion = "";
        }
        if (minecraftVersion == null) {
            minecraftVersion = "";
        }

        // execute shell command
        String realShellCommand = rawShellCommand
            .replace(FORGE_VERSION_PLACEHOLDER, forgeVersion)
            .replace(MINECRAFT_VERSION_PLACEHOLDER, minecraftVersion);

        Process proc = Runtime.getRuntime().exec(realShellCommand);
        new Thread("Shell Cmd Standard Out") {
            @Override
            public void run() {
                try {
                    proc.getInputStream().transferTo(System.out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        new Thread("Shell Cmd Standard Err") {
            @Override
            public void run() {
                try {
                    proc.getErrorStream().transferTo(System.err);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        proc.waitFor();
    }

    private static void printHelpMessage() {
        System.out.println("Usage: 'java -jar <filename>.jar <packwiz pack.toml url> <shell command>'");
        System.out.println("Reads the minecraft and forge version information in the pack.toml" +
            " at the given url, and passes it into the given shell command. Put \""
            + FORGE_VERSION_PLACEHOLDER + "\" or \"" + MINECRAFT_VERSION_PLACEHOLDER + "\" into the" +
            " given shell command wherever you want them inserted");
    }
}
