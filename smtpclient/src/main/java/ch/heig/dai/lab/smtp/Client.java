package ch.heig.dai.lab.smtp;

import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Send emails to an SMTP server.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Client {
    /**
     * The port of the server.
     */
    final static int SERVER_SOCKET = 1025;

    /**
     * The address of the server.
     */
    final static String SERVER_ADDRESS = "localhost";

    /**
     * The mails to send to the server.
     */
    private final Mail[] mails;

    /**
     * Create a client.
     *
     * @param victimFile  File containing the potential victims to attack.
     * @param messageFile File containing the message to send to the victims.
     * @param groupCount  Number of groups.
     */
    Client(String victimFile, String messageFile, int groupCount) {
        Mail[] mails;
        try {
            var parser = new Parser(victimFile, messageFile, groupCount);
            mails = parser.getGroups();
        } catch (FileNotFoundException e) {
            mails = null;
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        this.mails = mails;
    }

    /**
     * Starting point of the program.
     *
     * @param args 1st argument is the list of victims
     *             2nd argument is the message list
     *             3rd argument is the number of groups
     */
    public static void main(String[] args) {
        final String usage = "Usage: java -jar smtpclient.jar <victimFile> <messageFile> <groupCount>";
        int argLength = 3;

        if (args.length != argLength) {
            System.err.println("Error: " + argLength + " arguments are required.");
            System.err.println(usage);
            System.exit(1);
        }

        // Parse the arguments.
        String victimFile = args[0];
        String messageFile = args[1];
        int groupCount = 0;
        try {
            groupCount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        // Create and start the client.
        Client client = new Client(victimFile, messageFile, groupCount);
        client.execute();
    }

    /**
     * Execute the client. Create a new thread for each mail and send it to the server.
     */
    public void execute() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Mail mail : mails) {
                executor.execute(new Handler(new Socket(SERVER_ADDRESS, SERVER_SOCKET), new Worker(mail)));
            }
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
    }
}
