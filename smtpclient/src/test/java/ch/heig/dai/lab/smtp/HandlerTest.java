package ch.heig.dai.lab.smtp;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the Handler class.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class HandlerTest {
    /**
     * Test that handler successfully answers over a network socket.
     */
    @Test
    public void runTest() {
        try(Socket socket = new Socket();
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            var mail = new Mail("sender", new String[]{"receiver"}, "message");
            var worker = new Worker(mail);
            var handler = new Handler(socket, worker); // Create a handler. Any thrown error will make this test fail.

            new Thread(handler::run).start(); // Run the runnable in a separate thread.

            out.write("1 foocom Simple Simple Transfer Smtp Ready\r\n");
            out.flush();

            assertTrue(in.readLine().startsWith("QUIT"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}