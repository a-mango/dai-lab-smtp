package ch.heig.dai.lab.smtp;

/**
 * Encapsulate the data of an email.
 *
 * @param sender    The sender of the mail.
 * @param receivers The receivers of the mail.
 * @param message   The message body of the mail.
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public record Mail(String sender, String[] receivers, String message) {
}