package ch.heig.dai.lab.smtp;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

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
public class MailWorker {
    /**
     * The mail to be sent by the worker.
     */
    private final Mail mail;

    /**
     * The current step in the SMTP conversation.
     */
    private SmtpCommand currentCommand;

    /**
     * The current recipient index.
     */
    private int currentRecipientIndex = 0;

    /**
     * Constructor.
     *
     * @param mail The mail to be sent by the worker.
     */
    MailWorker(Mail mail) {
        this.mail = mail;
        this.currentCommand = SmtpCommand.WAIT;
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
            String response = handleResponse();
            System.out.print(message + "\n" + response);
            return response;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return String.format(SmtpCommand.QUIT.getValue());
        }
    }

    /**
     * Handle the response from the server based on the current step in the SMTP exchange and update the current step.
     *
     * @param request The message from the server.
     */
    private void handleRequest(String request) {
        currentCommand = switch (currentCommand) {
            case WAIT -> {
                if (!request.startsWith(SmtpStatus.SERVICE_READY.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                yield currentCommand.next();
            }
            case EHLO -> {
                if (!request.startsWith(SmtpStatus.OK.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                else if (request.startsWith(SmtpStatus.OK.code() + "-")) yield currentCommand.next();
                yield SmtpCommand.MAIL;
            }
            case EXT -> {
                if (!request.startsWith(SmtpStatus.OK.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                else if (request.startsWith(SmtpStatus.OK.code() + "-")) yield currentCommand;
                yield currentCommand.next();
            }
            case MAIL, MESSAGE -> {
                if (!request.startsWith(SmtpStatus.OK.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                yield currentCommand.next();
            }
            case RCPT -> {
                if (!request.startsWith(SmtpStatus.OK.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                else if (currentRecipientIndex == mail.receivers().length) yield currentCommand.next();
                yield currentCommand;
            }
            case DATA -> {
                if (!request.startsWith(SmtpStatus.START_MAIL_INPUT.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                yield currentCommand.next();
            }
            case QUIT -> {
                if (!request.startsWith(SmtpStatus.SERVICE_CLOSING.code()))
                    throw new IllegalStateException("Unexpected response: " + request);
                yield currentCommand.next();
            }
        };
    }

    /**
     * Generate the response to be sent to the server based on the current step in the SMTP exchange.
     *
     * @return The response to be sent to the server.
     */
    private String handleResponse() {
        return switch (currentCommand) {
            case WAIT -> "";
            case EHLO -> String.format(SmtpCommand.EHLO.getValue(), "localhost");
            case EXT -> String.format(SmtpCommand.EXT.getValue());
            case MAIL -> String.format(SmtpCommand.MAIL.getValue(), mail.sender());
            case RCPT -> String.format(SmtpCommand.RCPT.getValue(), String.format("<%s>", mail.receivers()[currentRecipientIndex++]));
            case DATA -> String.format(SmtpCommand.DATA.getValue(), mail.message());
            case MESSAGE -> {
                var date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").format(new Date());
                var sender = String.format("%s <%s>", mail.sender(), mail.sender());
                var subject = mail.message().substring(0, mail.message().indexOf('.'));
                var encodedSubject =  "=?utf-8?B?" + Base64.getEncoder().encodeToString(subject.getBytes()) + "?=";
                var receivers = String.join(", ", mail.receivers());
                var message = mail.message();
                yield String.format(SmtpCommand.MESSAGE.getValue(), date, sender, encodedSubject, receivers, message);
            }
            case QUIT -> String.format(SmtpCommand.QUIT.getValue());
        };
    }

    /**
     * Get the current command.
     *
     * @return The current command.
     */
    public SmtpCommand getCurrentCommand() {
        return currentCommand;
    }
}
