package ch.heig.dai.lab.smtp;

/**
 * Handle the data used in an SMTP conversation.
 * 
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 */
public class Worker {
   /**
    * The steps of an SMTP exchange.
    */
   private enum SmtpProcedureStep {
      OPENING, INIT_CLIENT, INIT_SERVER, MAIL_HEADER, MAIL_DATA
   };

   /**
    * The status codes of an SMTP server.
    */
   private enum SmtpServerCode {
      _220, _221, _250, _354
   };

   /**
    * Instanciate a worker.
    * 
    * @param mail The mail to be sent by the worker.
    */
   Worker(Mail mail) {

   }

   /**
    * Perform work based on the current step of the SMTP conversation.
    * 
    * @param message The message from the server.
    * @return
    */
   public String work(String message) {
      return null;
   }

   /**
    * Perform the indentity declaration step of an SMTP exchange.
    * 
    * @return
    */
   private String identity() {
      return null;
   }

   /**
    * Get the header of the mail.
    * 
    * @return The header of the mail to be sent.
    */
   private String mailHeader() {
      return null;
   }

   /**
    * Get the body of the email.
    * 
    * @return The body of the mail to be sent.
    */
   private String mailBody() {
      return null;
   }
}
