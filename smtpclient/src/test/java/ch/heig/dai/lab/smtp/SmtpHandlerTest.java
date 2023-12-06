package ch.heig.dai.lab.smtp;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the Handler class.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class SmtpHandlerTest {
    /**
     * Test that handler and worker successfully answer a meaningful message over a network socket.
     */
    @Test
    public void runTest() {
        MockSocket socket = new MockSocket("1 foocom Simple Simple Transfer Smtp Ready\n");
        Mail mail = new Mail("sender", new String[]{"receiver"}, "message");
        MailWorker worker = new MailWorker(mail);

        SmtpHandler handler = new SmtpHandler(socket, worker);

        try {
            var thread = new Thread(handler);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
        assertTrue(socket.getOutput().startsWith("QUIT"));
    }

    /**
     * Basic mock socket to test the handler.
     */
    private static class MockSocket extends Socket {
        private final ByteArrayOutputStream outputStream;
        private final ByteArrayInputStream inputStream;

        public MockSocket(String input) {
            this.inputStream = new ByteArrayInputStream(input.getBytes());
            this.outputStream = new ByteArrayOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return this.inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            return this.outputStream;
        }

        public String getOutput() {
            return this.outputStream.toString();
        }
    }
}