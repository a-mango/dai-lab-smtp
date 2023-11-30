package ch.heig.dai.lab.smtp;

/**
 * Represent the steps of the SMTP mail-sending procedure.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public enum SmtpCommand {
    /**
     * The HELO step.
     */
    HELO("HELO %s\r\n"),
    /**
     * The MAIL step.
     */
    MAIL("MAIL FROM: <%s>\r\n"),
    /**
     * The RCPT step.
     */
    RCPT("RCPT TO: %s\r\n"),
    /**
     * The DATA step.
     */
    DATA("DATA\r\n"),
    /**
     * The QUIT step.
     */
    QUIT("QUIT\r\n");

    /**
     * The value of the enum
     */
    private final String value;

    /**
     * Constructor
     *
     * @param value The value
     */
    SmtpCommand(String value) {
        this.value = value;
    }

    /**
     * Get the value
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }

    public SmtpCommand next() {
        SmtpCommand[] command = SmtpCommand.values();
        int i = 0;
        while (command[i] != this) i++;
        i++;
        i %= command.length;
        return command[i];
    }
}
