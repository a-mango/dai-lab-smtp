package ch.heig.dai.lab.smtp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;

/**
 * Parse the content of the target lists and form groups from the data.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class GroupParser {
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
    GroupParser(String victimFile, String messageFile, int groupCount) throws FileNotFoundException {
        this.groups = new Mail[groupCount];

        Stack<String> victims = parseFile(victimFile);
        Stack<String> messages = parseFile(messageFile);

        // Validate the input files.
        if (victims.isEmpty())
            throw new IllegalArgumentException("The victim file is empty.");
        else if (messages.isEmpty())
            throw new IllegalArgumentException("The message file is empty.");
        else if (!victims.get(0).matches("^(.+)@(\\S+)$"))
            throw new IllegalArgumentException("The victim file contains invalid email addresses.");
        else if (victims.size() < groupCount * 6) // An upper bound of 6 emails per group is needed.
            throw new IllegalArgumentException("The victim file is not large enough.");
        else if (messages.size() < groupCount)
            throw new IllegalArgumentException("The message file is not large enough.");

        // Shuffle to have different order of messages each time the attack is run.
        Collections.shuffle(victims);
        Collections.shuffle(messages);

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
    private static Stack<String> parseFile(String path) throws FileNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (var in = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(classloader.getResourceAsStream(path)), StandardCharsets.UTF_8))) {
            Stack<String> tmp = new Stack<>();
            tmp.addAll(in.lines().toList());
            return tmp;
        } catch (Exception e) {
            throw new FileNotFoundException("File not found: " + path);
        }
    }

    /**
     * Select a message from a list.
     *
     * @param messages The message to select
     * @return The selected message.
     */
    private String selectMessage(Stack<String> messages) {
        return selectCandidate(messages);
    }

    /**
     * Randomly select a candidate from a list of candidates.
     *
     * @param candidates The list of candidates.
     * @return The selected candidate.
     */
    private String selectCandidate(Stack<String> candidates) {
        if (candidates.isEmpty())
            throw new IllegalArgumentException("Victim file not large enough.");

        return candidates.pop();
    }

    /**
     * Randomly selects a group of 2 to 5 receivers from a pool of candidates.
     *
     * @param candidates The pool of candidates.
     * @return An array of selected candidates.
     */
    private String[] selectReceivers(Stack<String> candidates) {
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
        if (this.groups == null)
            return null;
        return Arrays.copyOf(this.groups, this.groups.length);
    }
}
