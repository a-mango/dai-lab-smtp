package ch.heig.dai.lab.smtp;

/**
 * Handle the data exchange in an SMTP conversation.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Worker {
    private final Mail mail;
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
            handleMessage(message);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return String.format(SmtpCommand.QUIT.getValue());
        }

        return handleResponse();
    }


    private void handleMessage(String message) {
        switch (currentCommand) {
            case HELO, RCPT, MAIL -> {
                if (!message.startsWith(SmtpStatus.OK.getValue()))
                    throw new IllegalStateException("Unexpected response: " + message);
            }
            case DATA -> {
                if (!message.startsWith(SmtpStatus.START_MAIL_INPUT.getValue()))
                    throw new IllegalStateException("Unexpected response: " + message);
            }
            case QUIT -> {
                if (!message.startsWith("221")) throw new IllegalStateException("Unexpected response: " + message);
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
        String response = switch (currentCommand) {
            case HELO -> String.format(SmtpCommand.HELO.getValue(), "localhost");
            case MAIL -> String.format(SmtpCommand.MAIL.getValue(), mail.sender());
            case RCPT -> String.format(SmtpCommand.MAIL.getValue(), String.format("<%s>", String.join("> <", mail.receivers())));
            case DATA -> String.format(SmtpCommand.DATA.getValue(), mail.message());
            case QUIT -> String.format(SmtpCommand.QUIT.getValue());
        };

        currentCommand = currentCommand.next();

        return response;
    }
}
