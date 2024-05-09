package org.example;

import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class TextAnalyzer {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args)); // Add this to see exactly what is passed to the program

        Options options = new Options();
        options.addOption("file", true, "File path");
        options.addOption("top", true, "Number of top phrases to display");
        options.addOption("phraseSize", true, "The size of phrases to analyze");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("file") && cmd.hasOption("top") && cmd.hasOption("phraseSize")) {
                String filePath = cmd.getOptionValue("file");
                int top = Integer.parseInt(cmd.getOptionValue("top"));
                int phraseSize = Integer.parseInt(cmd.getOptionValue("phraseSize"));

                processFile(filePath, top, phraseSize);
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("TextAnalyzer", options);
            }
        } catch (Exception e) {
            System.out.println("Error processing command line arguments: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void processFile(String filePath, int top, int phraseSize) {
        try {
            String content = Files.readString(Paths.get(filePath));
            List<String> sentences = Arrays.asList(content.split("[.!?]\\s*"));
            List<String> words = Arrays.asList(content.split("\\s+"));
            Map<String, Long> phraseCounts = new HashMap<>();

            // Generate phrases and count them
            for (int i = 0; i < words.size() - phraseSize + 1; i++) {
                List<String> subList = words.subList(i, i + phraseSize);
                String phrase = String.join(" ", subList);
                phraseCounts.put(phrase, phraseCounts.getOrDefault(phrase, 0L) + 1);
            }

            // Sort phrases by frequency
            List<Map.Entry<String, Long>> sortedPhrases = phraseCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(top)
                    .collect(Collectors.toList());

            // Output results
            System.out.println("Number of sentences: " + sentences.size());
            System.out.println("Number of words: " + words.size());
            System.out.println("Top " + top + " phrases of size " + phraseSize + ":");
            for (Map.Entry<String, Long> entry : sortedPhrases) {
                System.out.println("\"" + entry.getKey() + "\" appears " + entry.getValue() + " times");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
