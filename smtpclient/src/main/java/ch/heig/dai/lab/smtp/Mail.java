package ch.heig.dai.lab.smtp;

import java.util.Arrays;

/**
 * Encapsulate the data of an email.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Mail {
    /*
     * The recipients of the email. The first receiver is the sender.
     */
    private final String[] receivers;

    /**
     * The message of the email.
     */
    private final String message;

    /**
     * Create an email object.
     *
     * @param receivers The receivers of the email.
     * @param message   The message of the email.
     */
    Mail(String[] receivers, String message) {
        this.receivers = Arrays.copyOf(receivers, receivers.length);
        this.message = message;
    }

    /**
     * Get the sender of the email.
     *
     * @return
     */
    public String getSender() {
        return null;
    }

    /**
     * Get the receivers of an email.
     *
     * @return The receivers of the email.
     */
    public String[] getReceivers() {
        return null;
    }

    /**
     * Get the message of an email.
     *
     * @return The message of the email.
     */
    public String getMessage() {
        return null;
    }
}
