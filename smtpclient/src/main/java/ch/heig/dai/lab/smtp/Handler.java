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
public class Handler implements Runnable {
   final Socket socket;
   final Worker worker;

   /**
    * Create a Handler.
    * 
    * @param socket socket to use to connect to server
    * @param worker worker to send the mail to the server, compute each process of
    *               the mail sending
    */
   Handler(Socket socket, Worker worker) {
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
            out.write(worker.work(line));
            out.flush();
         }
      } catch (Exception e) {
         System.out.println("Error: " + e);
         System.exit(1);
      }
   }
}
