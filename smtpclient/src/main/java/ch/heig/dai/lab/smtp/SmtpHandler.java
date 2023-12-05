package ch.heig.dai.lab.smtp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import static java.nio.charset.StandardCharsets.*;

/**
 * Handle a connection to an SMTP server.
 * 
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class SmtpHandler implements Runnable {
   final Socket socket;
   final MailWorker worker;

   /**
    * Create a Handler.
    * 
    * @param socket socket to use to connect to server
    * @param worker worker to send the mail to the server, compute each process of
    *               the mail sending
    */
   public SmtpHandler(Socket socket, MailWorker worker) {
      this.socket = socket;
      this.worker = worker;
   }

   /**
    * Run the handler.
    */
   public void run() {
      try (socket;
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
            var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {
         String line;
         while ((line = in.readLine()) != null && worker.getCurrentCommand() != SmtpCommand.QUIT) {
            String response = worker.work(line);
            if (!response.isEmpty()) {
               out.write(response);
               out.flush();
            }
         }
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
         System.exit(1);
      }
   }
}
