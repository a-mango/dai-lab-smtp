package ch.heig.dai.lab.smtp;

/**
 * Handle the data exchange in an SMTP conversation. This object is stateful and should be used by a single thread for
 * a single session. The worker will keep track of the current step in the SMTP conversation and generate the
 * appropriate response based on the server's request. Use the {@link #work(String)} method to perform the work. The
 * work has been separated in two distinct steps for more clarity.
 * <p>
 * Upon reception of an unexpected response, the worker will throw an {@link IllegalStateException}, which will be
 * caught by the {@link #work(String)} method and will cause the worker to send a QUIT command to the server.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Worker {
    /**
     * The mail to be sent by the worker.
     */
    private final Mail mail;

    /**
     * The current step in the SMTP conversation.
     */
    private SmtpCommand currentCommand;

    /**
     * Constructor.
     *
     * @param mail The mail to be sent by the worker.
     */
    Worker(Mail mail) {
        this.mail = mail;
        this.currentCommand = SmtpCommand.HELO;
    }

    /**
     * Perform work based on the current step of the SMTP conversation.
     *
     * @param message The message from the server.
     * @return The message to send to the server.
     */
    public String work(String message) {
        try {
            handleRequest(message);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return String.format(SmtpCommand.QUIT.getValue());
        }

        String response = handleResponse();

        currentCommand = currentCommand.next();

        return response;
    }

    /**
     * Handle the response from the server based on the current step in the SMTP exchange.
     *
     * @param request The message from the server.
     */
    private void handleRequest(String request) {
        switch (currentCommand) {
            case HELO, RCPT, MAIL, MESSAGE -> {
                if (!request.startsWith(SmtpStatus.OK.getValue()))
                    throw new IllegalStateException("Unexpected response: " + request);
            }
            case DATA -> {
                if (!request.startsWith(SmtpStatus.START_MAIL_INPUT.getValue()))
                    throw new IllegalStateException("Unexpected response: " + request);
            }
            case QUIT -> {
                if (!request.startsWith("221")) throw new IllegalStateException("Unexpected response: " + request);
            }
            default -> throw new IllegalStateException("Unexpected response: " + currentCommand);
        }
    }

    /**
     * Generate the response to be sent to the server based on the current step in the SMTP exchange.
     *
     * @return The response to be sent to the server.
     */
    private String handleResponse() {
        return switch (currentCommand) {
            case HELO -> String.format(SmtpCommand.HELO.getValue(), "localhost");
            case MAIL -> String.format(SmtpCommand.MAIL.getValue(), mail.sender());
            case RCPT ->
                    String.format(SmtpCommand.MAIL.getValue(), String.format("<%s>", String.join("> <", mail.receivers())));
            case DATA -> String.format(SmtpCommand.DATA.getValue(), mail.message());
            case MESSAGE -> String.format(SmtpCommand.MESSAGE.getValue(), mail.message().substring(0, 20), mail.message().substring(20));
            case QUIT -> String.format(SmtpCommand.QUIT.getValue());
        };
    }
}
