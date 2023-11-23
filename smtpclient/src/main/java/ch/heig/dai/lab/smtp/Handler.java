package ch.heig.dai.lab.smtp;

import java.net.Socket;

/**
 * Handle a connection to an SMTP server.
 * 
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Handler {
   final Socket socket;
   final Worker worker;

   /**
    * Create a Handler.
    * 
    * @param socket socket to co
    * @param worker
    */
   Handler(Socket socket, Worker worker) {
      this.socket = socket;
      this.worker = worker;
   }

   /**
    * Run the handler.
    */
   public void run() {

   }
}
