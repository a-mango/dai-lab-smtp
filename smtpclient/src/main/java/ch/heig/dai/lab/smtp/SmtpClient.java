package ch.heig.dai.lab.smtp;

import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Send emails to an SMTP server.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class SmtpClient {
    /**
     * Program banner
     */
    final static String BANNER = """
                                  dP                     dP oo                     dP  \s
                                  88                     88                        88  \s
            .d8888b. 88d8b.d8b. d8888P 88d888b. .d8888b. 88 dP .d8888b. 88d888b. d8888P\s
            Y8ooooo. 88'`88'`88   88   88'  `88 88'  `"" 88 88 88ooood8 88'  `88   88  \s
                  88 88  88  88   88   88.  .88 88.  ... 88 88 88.  ... 88    88   88  \s
            `88888P' dP  dP  dP   dP   88Y888P' `88888P' dP dP `88888P' dP    dP   dP  \s
                                       88                                              \s
                                       dP                         by Balkghar & a-mango\s
            """;

    final static String USAGE = "Usage: java -jar smtpclient.jar <victimFile> <messageFile> <groupCount> [uri]";

    /**
     * The port of the server.
     */
    static int SERVER_PORT = 1025;

    /**
     * The address of the server.
     */
    static String SERVER_ADDRESS = "localhost";

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
    public SmtpClient(String victimFile, String messageFile, int groupCount) {
        Mail[] mails;
        try {
            var parser = new GroupParser(victimFile, messageFile, groupCount);
            mails = parser.getGroups();
        } catch (FileNotFoundException e) {
            mails = null;
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        this.mails = mails;
    }

    public SmtpClient(String victimFile, String messageFile, int groupCount, String uri) {
        this(victimFile, messageFile, groupCount);
        try {
            SERVER_ADDRESS = uri.split(":")[0];
            SERVER_PORT = Integer.parseInt(uri.split(":")[1]);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Starting point of the program.
     *
     * @param args 1st argument is the list of victims
     *             2nd argument is the message list
     *             3rd argument is the number of groups
     */
    public static void main(String[] args) {
        int argLength = 3;

        if (args.length < argLength || args.length > 4) {
            System.err.println("Error: " + argLength + " arguments are required.");
            System.out.println(USAGE);
            System.exit(1);
        }

        // Print the banner.
        System.out.println(BANNER);

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

        // Check that the groupCount is within acceptable bounds.
        if (!(groupCount > 0 && groupCount <= 10)) {
            System.err.println("Error: groupCount must be in [1, 10] range.");
            System.exit(1);
        }

        // Recap the options for the user to see.
        System.out.println("> Victim file: " + victimFile);
        System.out.println("> Message file: " + messageFile);
        System.out.println("> Group count: " + groupCount);
        if (args.length == 4) {
            System.out.println("> Server: " + args[3]);
        } else {
            System.out.println("> Server: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        }

        // Ask for confirmation.
        System.out.print("> Do you want to continue? [y/N] ");
        Scanner keyboard = new Scanner(System.in);
        String input;
        while (!(input = keyboard.nextLine()).equals("\n") && !input.equals("y") && !input.equals("N")) {
            System.out.print("> Do you want to continue? [y/N] ");
        }

        if (input.equals("N")) {
            System.out.println("Exiting...");
            System.exit(0);
        }

        // Create and start the client with all parameters if provided, and only 3 otherwise.
        if (args.length == 4) {
            String uri = args[3];
            new SmtpClient(victimFile, messageFile, groupCount, uri).execute();
        } else {
            new SmtpClient(victimFile, messageFile, groupCount).execute();
        }
    }

    /**
     * Execute the client. Create a new thread for each mail and send it to the server.
     */
    public void execute() {
        System.out.println("> Starting virtual threads...");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Futures list
            ArrayList<Future<?>> futures = new ArrayList<>();
            for (Mail mail : mails) {
                System.out.println("> Sending mail from " + mail.sender() + " to " + mail.receivers().length + " recipients...");
                futures.add(executor.submit(new SmtpHandler(new Socket(SERVER_ADDRESS, SERVER_PORT), new MailWorker(mail))));
            }

            // Await for all futures to complete.
            for (Future<?> future : futures) {
                future.get();
            }

            // Confirm that all mails have been sent.
            System.out.println("> All mails sent.");
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
