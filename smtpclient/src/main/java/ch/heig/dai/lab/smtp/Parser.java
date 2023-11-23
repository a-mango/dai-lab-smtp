package ch.heig.dai.lab.smtp;

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
        this.groups = null;
    }

    /**
     * Select a message from a list.
     *
     * @param messages The message to select
     * @return The selected message.
     */
    private String selectMessage(String messages) {
        return null;
    }

    /**
     * Randomly select a candidate from a list of candidates.
     *
     * @param candidates The list of candidates.
     * @return The selected candidate.
     */
    private String selectCandidate(String candidates) {
        return null;
    }

    /**
     * Randomly selects a group of receivers from a pool of candidates.
     *
     * @param candidates The pool of candidates.
     * @return An array of selected candidates.
     */
    private String[] selectReceivers(String candidates) {
        return null;
    }

    /**
     * Form random groups of sender, victims and a message.
     *
     * @return The list of emails containing the victims and message aggregations.
     */
    public Mail[] getGroups() {
        return null;
    }

    /**
     * Parse the contents of a text file
     *
     * @param path The path of the file to parse.
     * @return The parsed content of the file.
     */
    private static String[] parseFile(String path) {
        return null;
    }
}
