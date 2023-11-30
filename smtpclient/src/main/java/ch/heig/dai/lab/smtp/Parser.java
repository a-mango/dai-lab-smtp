package ch.heig.dai.lab.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Parse the content of the target lists and form groups from the data.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Parser {
    /**
     * The groups formed by the parser.
     */
    private final Mail[] groups;

    /**
     * Class constructor.
     *
     * @param victimFile  The list of potential victims.
     * @param messageFile The list of potential messages.
     * @param groupCount  The number of groups to create.
     */
    Parser(String victimFile, String messageFile, int groupCount) {
        this.groups = new Mail[groupCount];

        String[] victims = parseFile(victimFile);
        String[] messages = parseFile(messageFile);

        for (int i = 0; i < groupCount; i++) {
            String sender = selectCandidate(victims);
            String[] receivers = selectReceivers(victims);
            String message = selectMessage(messages);
            Mail mail = new Mail(sender, receivers, message);
            this.groups[i] = mail;
        }
    }

    /**
     * Parse the contents of a text file situated in the resources folder.
     *
     * @param path The path of the file to parse.
     * @return The parsed content of the file.
     */
    private static String[] parseFile(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (var in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classloader.getResourceAsStream(path)), StandardCharsets.UTF_8))) {
            return in.lines().toArray(String[]::new);
        } catch (IOException e) {
            System.out.println("Error while parsing file: " + path);
            System.exit(1);
            return null;
        }
    }

    /**
     * Select a message from a list.
     *
     * @param messages The message to select
     * @return The selected message.
     */
    private String selectMessage(String[] messages) {
        return selectCandidate(messages);
    }

    /**
     * Randomly select a candidate from a list of candidates.
     *
     * @param candidates The list of candidates.
     * @return The selected candidate.
     */
    private String selectCandidate(String[] candidates) {
        Random rand = new Random();
        return candidates[rand.nextInt(candidates.length)];
    }

    /**
     * Randomly selects a group of 2 to 5 receivers from a pool of candidates.
     *
     * @param candidates The pool of candidates.
     * @return An array of selected candidates.
     */
    private String[] selectReceivers(String[] candidates) {
        Random rand = new Random();
        int size = rand.nextInt(4) + 2;

        String[] receivers = new String[size];
        for (int i = 0; i < size; i++)
            receivers[i] = selectCandidate(candidates);

        return receivers;
    }

    /**
     * Form random groups of sender, victims and a message.
     *
     * @return The list of emails containing the victims and message aggregations.
     */
    public Mail[] getGroups() {
        if (this.groups == null) return null;
        return Arrays.copyOf(this.groups, this.groups.length);
    }
}
