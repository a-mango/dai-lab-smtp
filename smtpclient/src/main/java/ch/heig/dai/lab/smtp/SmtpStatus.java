package ch.heig.dai.lab.smtp;

/**
 * Represent the SMTP server codes, human-readable names and their associated
 * messages.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public enum SmtpStatus {
    /**
     * The requested action was not taken and the error condition is temporary
     */
    SERVICE_READY(220, "Service ready"),
    /**
     * The requested action was not taken and the error condition is permanent
     */
    SERVICE_CLOSING(221, "Service closing transmission channel"),
    /**
     * The server is ready to accept a message from the client
     */
    OK(250, "Requested mail action okay, completed"),
    /**
     * The server is unable to send the e-mail
     */
    START_MAIL_INPUT(354, "Start mail input; end with <CRLF>.<CRLF>");

    /**
     * The key of the enum
     */
    private final Integer key;

    /**
     * The value of the enum
     */
    private final String value;

    /**
     * Constructor
     *
     * @param key   The key
     * @param value The value
     */
    SmtpStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the key
     *
     * @return The key
     */
    public Integer getKey() {
        return key;
    }

    /**
     * Get the value
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }
}
