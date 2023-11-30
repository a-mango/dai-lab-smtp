package ch.heig.dai.lab.smtp;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Send emails to an SMTP server.
 * 
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Client {
   /**
    * The mails to send to the server.
    */
   private Mail mails[];

   /**
    * Starting point of the program.
    *
    * @param args Arguments for the attack, 1st argument is the list of victims,
    *             2sd argument is the message list and the last argument is a int
    *             for the number of group on wich the e-mail prank is played.
    */
   public static void main(String[] args) {
      try {
         int nbGrp = Integer.parseInt(args[2]);
         Client client = new Client(args[0], args[1], nbGrp);

         client.execute();

      } catch (Exception e) {

      }

   }

   /**
    * Create a client.
    *
    * @param victimFile  File containing list of victims to attack 😈.
    * @param messageFile Message to send to the victims.
    * @param groupCount  Number of groups.
    */
   Client(String victimFile, String messageFile, int groupCount) {

      Parser parser = new Parser(victimFile, messageFile, groupCount);
      mails = parser.getGroups();
   }

   /**
    * Run the client.
    */
   public void execute() {
      final int SERVER_SOCKET = 8025;
      final String SERVER_ADRESS = "localhost";

      try (var socket = new Socket(SERVER_ADRESS, SERVER_SOCKET);
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

         for (Mail mail : mails) {
            var handler = new Handler(socket, new Worker(mail));
            executor.execute(handler);
         }

      } catch (Exception e) {
         System.out.println("Error : " + e);
      }

   }

}
