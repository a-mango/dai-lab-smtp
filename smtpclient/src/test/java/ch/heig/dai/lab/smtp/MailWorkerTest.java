package ch.heig.dai.lab.smtp;

import org.junit.jupiter.api.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the Worker class. Tests cases are based on RFC 5321 Appendix D.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5321#appendix-D">RFC 5321 Appendix D</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MailWorkerTest {
    /**
     * Reusable worker for the tests.
     */
    private static MailWorker worker;

    /**
     * Constructor
     */
    MailWorkerTest() {

    }

    /**
     * Set up the fixture to be shared among tests.
     */
    @BeforeAll
    void setUpFixture() {
        Mail mail = new Mail("s@test.com", new String[]{"a@test.com", "b@test.com"}, "Here is the email message. Sent from the smtp client \uD83D\uDCEC");
        worker = new MailWorker(mail);
    }

    /**
     * Test that the worker correctly handles a bad response from the server by sending a QUIT command.
     */
    @Test
    public void badRequestTest() {
        // Voluntary shadowing to avoid modifying the state of the shared worker.
        MailWorker worker = new MailWorker(new Mail("s@test.com", new String[]{"a@test.com", "b@test.com"}, "Here is the email message. Sent from the smtp client \uD83D\uDCEC"));
        assertEquals("QUIT\r\n", worker.work("500 Syntax error, command unrecognized\r\n"));
        assertEquals("QUIT\r\n", worker.work("501 Syntax error in parameters or arguments\r\n"));
    }

    /**
     * Test that the worker handles the EHLO step.
     */
    @Test
    @Order(1)
    public void ehloTest() {
        String response = worker.work("220 foo.com Simple Mail Transfer Service Ready");
        assertEquals("EHLO localhost\r\n", response);
    }

    /**
     * Test that the worker handles the MAIL step.
     */
    @Test
    @Order(2)
    public void mailTest() {
//        String response = worker.work("250-foo.com greets localhost\r\n250-8BITMIME\r\n250-SIZE\r\n250-DSN\r\n250- HELP");
        String response = worker.work("250 foo.com greets localhost\r\n");
        assertEquals("MAIL FROM: <s@test.com>\r\n", response);
    }

    /**
     * Test that the worker handles the RCPT step.
     */
    @Test
    @Order(3)
    public void rcptTest() {
        String response = worker.work("250 OK");
        assertEquals("RCPT TO: <a@test.com>\r\n", response);
        response = worker.work("250 OK");
        assertEquals("RCPT TO: <b@test.com>\r\n", response);
    }

    /**
     * Test that the worker handles the DATA step. The answer from the worker should contain a subject and a message.
     */
    @Test
    @Order(4)
    public void dataTest() {
        String response = worker.work("250 OK");
        assertEquals("DATA\r\n", response);
    }

    /**
     * Test that the worker handles the message step.
     */
    @Test
    @Order(5)
    public void messageTest() {
        String response = worker.work("354 Start mail input; end with <CRLF>.<CRLF>");
        var date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").format(new Date());
        String expected = """
                Date: %s\r
                From: s@test.com <s@test.com>\r
                Subject: =?utf-8?B?SGVyZSBpcyB0aGUgZW1haWwgbWVzc2FnZQ==?=\r
                To: a@test.com, b@test.com\r
                Content-Type: text/plain; charset=utf-8\r
                \r
                Here is the email message. Sent from the smtp client 📬\r
                .\r
                """;
        assertEquals(String.format(expected, date), response);
    }

    /**
     * Test that the worker handles the QUIT step.
     */
    @Test
    @Order(6)
    public void quitTest() {
        String response = worker.work("250 OK");
        assertEquals("QUIT\r\n", response);
    }
}
