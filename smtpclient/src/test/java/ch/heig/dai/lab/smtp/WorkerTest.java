package ch.heig.dai.lab.smtp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the Worker class. Tests cases are based on RFC 5321 Appendix D.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5321#appendix-D">RFC 5321 Appendix D</a>
 */
public class WorkerTest {
    /**
     * Reusable worker for the tests.
     */
    private final Worker worker;

    /**
     * Constructor
     */
    WorkerTest() {
        Mail mail = new Mail("s@test.com", new String[]{"a@test.com", "b@test.com"}, "Here is the message");
        worker = new Worker(mail);
    }

    /**
     * Test that the worker correctly handles a bad response from the server by sending a QUIT command.
     */
    @Test
    public void badRequestTest() {
        // Voluntary shadowing to avoid modifying the state of the shared worker.
        Worker worker = new Worker(new Mail("s@test.com", new String[]{"a@test.com", "b@test.com"}, "Here is the message"));
        assertEquals("QUIT\r\n", worker.work("500 Syntax error, command unrecognized\r\n"));
        assertEquals("QUIT\r\n", worker.work("501 Syntax error in parameters or arguments\r\n"));
    }

    /**
     * Test that the worker handles the EHLO step.
     */
    @Test
    public void ehloTest() {
        String response = worker.work("220 foo.com Simple Mail Transfer Service Ready\r\n");
        assertEquals("EHLO localhost\r\n", response);
    }

    /**
     * Test that the worker handles the MAIL step.
     */
    @Test
    public void mailTest() {
        String response = worker.work("250-foo.com greets localhost\r\n250-8BITMIME\r\n250-SIZE\r\n250-DSN\r\n250- HELP\r\n");
        assertEquals("MAIL FROM: <s@test.com>\r\n", response);
    }

    /**
     * Test that the worker handles the RCPT step.
     */
    @Test
    public void rcptTest() {
        String response = worker.work("250 OK\r\n");
        assertEquals("RCPT TO: <a@test.com> <b@test.com>\r\n", response);
    }

    /**
     * Test that the worker handles the DATA step. The answer from the worker should contain a subject and a message.
     */
    @Test
    public void dataTest() {
        String response = worker.work("250 OK\r\n");
        assertEquals("DATA\r\n", response);
    }

    /**
     * Test that the worker handles the message step.
     */
    @Test
    public void messageTest() {
        String response = worker.work("354 Start mail input; end with <CRLF>.<CRLF>\r\n");
        assertEquals("Subject: Here is the message\r\n\r\nHere is the message\r\n.\r\n", response);
    }

    /**
     * Test that the worker handles the QUIT step.
     */
    @Test
    public void quitTest() {
        String response = worker.work("250 OK\r\n");
        assertEquals("QUIT\r\n", response);
    }
}
