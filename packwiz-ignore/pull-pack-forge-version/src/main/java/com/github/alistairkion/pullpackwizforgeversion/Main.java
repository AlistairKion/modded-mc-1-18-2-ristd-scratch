package com.github.alistairkion.pullpackwizforgeversion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Scanner;

public class Main {
    /**
     * Reads the pack.toml at the given url, and writes two files, one containing the
     * minecraft version and the other containing the forge version.
     *
     * @param args Expected args: -h, or http link to packwiz pack.toml, filename
     *             to save forge version, and filename to save minecraft version
     */
    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                printHelpMessage();
                return;
            }
        }
        if (args.length < 3) {
            System.out.println("You must pass in a link to the packwiz pack.toml and output filenames");
            printHelpMessage();
            return;
        }

        String url = args[0];
        String forgeFilename = args[1];
        String minecraftFilename = args[2];

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
        } else if (forgeVersion == null) {
            System.out.printf(
                "pack.toml url read successfully, and found minecraft version %s, but no forge version was found\n",
                minecraftVersion
            );
        } else if (minecraftVersion == null) {
            System.out.printf(
                "pack.toml url read successfully, and found forge version %s, but no minecraft version was found\n",
                forgeVersion
            );
        } else {
            System.out.println("pack.toml url read successfully");
        }

        // write to file
        try (
            Writer forgeWriter = new FileWriter(forgeFilename, false);
            Writer minecraftWriter = new FileWriter(minecraftFilename, false)
        ) {
            forgeWriter.append(forgeVersion);
            minecraftWriter.append(minecraftVersion);
        }

        System.out.println("Finished writing to files");
    }

    private static void printHelpMessage() {
        System.out.println("Usage: 'java -jar <filename>.jar <packwiz pack.toml url> <forge-version-output-file> <minecraft-version-output-file>'");
        System.out.println("Reads the pack.toml at the given url, and writes two files, one containing the minecraft version and the other containing the forge version.");
    }
}
