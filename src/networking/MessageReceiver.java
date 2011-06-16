package networking;

/**
 * The <code>MessageReceiver</code> interface provides a way to receive messages
 * from a communicator as they arrive.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public interface MessageReceiver {

   /**
    * Performs the appropriate actions based on the type and contents of the
    * specified message.
    * @param message the message
    */
   public void receiveMessage(Message message);
}
