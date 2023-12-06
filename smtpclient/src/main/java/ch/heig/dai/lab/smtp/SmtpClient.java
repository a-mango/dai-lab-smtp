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

        // Create and start the client.
        SmtpClient client = new SmtpClient(victimFile, messageFile, groupCount);
        client.execute();
    }

    /**
     * Execute the client. Create a new thread for each mail and send it to the server.
     */
    public void execute() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Mail mail : mails) {
                executor.execute(new SmtpHandler(new Socket(SERVER_ADDRESS, SERVER_SOCKET), new MailWorker(mail)));
            }
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
    }
}
