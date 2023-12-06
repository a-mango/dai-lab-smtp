package ch.heig.dai.lab.smtp;

/**
 * Represent the SMTP server codes.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public enum SmtpStatus {
    /**
     * The requested action was not taken and the error condition is temporary
     */
    SERVICE_READY("220"),
    /**
     * The requested action was not taken and the error condition is permanent
     */
    SERVICE_CLOSING("221"),
    /**
     * The server is ready to accept a message from the client
     */
    OK("250"),
    /**
     * The server is unable to send the e-mail
     */
    START_MAIL_INPUT("354");

    /**
     * The code of the enum
     */
    private final String code;

    /**
     * Constructor
     *
     * @param code   The status code
     */
    SmtpStatus(String code) {
        this.code = code;
    }

    /**
     * Get the code
     *
     * @return The key
     */
    public String code() {
        return code;
    }
}
