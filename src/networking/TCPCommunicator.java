package networking;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The <code>TCPCommunicator</code> class facilitates communication between a
 * client and a server through TCP.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class TCPCommunicator {

   /**
    * A boolean flag indicating whether or not this communicator is currently
    * connected to a server
    */
   private boolean connected = false;
   /**
    * The communicator's object input stream
    */
   private ObjectInputStream inputStream;

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }
   /**
    * The communicator's object output stream
    */
   private ObjectOutputStream outputStream;
   /**
    * The communicator's message receiving thread
    */
   private MessageReceiverThread thread;

   /**
    * Connects to a server at the specified IP address and port number and
    * prepares for communication with the server.  All messages from the server
    * are sent to the specified message receiver.
    * In addition, the provided player information is sent to the server.
    * @param player the player
    * @param receiver the message receiver
    * @param info the server information object (containing IP address and port number)
    * @throws IOException if an I/O error occurs
    */
    public void connect(String username, MessageReceiver receiver, ServerInfo info) throws IOException {
        connect(new Socket(info.getServerIPAddress(), info.getServerPort()),receiver);
        sendMessage(new Message(Message.CONNECT_TO_SERVER, new Object[]{username}));
    }
   /**
    * Connects to a server at the specified IP address and port number. Does NOT
    * prepare for communication to the server; simply creates and binds a new
    * socket.
    * @param info the server information object (containing IP address and port number)
    * @param receiver the message receiver
    * @throws IOException if an I/O error occurs
    */
    public void connect(ServerInfo info, MessageReceiver receiver) throws IOException {
        connect(new Socket(info.getServerIPAddress(), info.getServerPort()),receiver);
    }
   /**
    * Prepares for communication through the given socket.  All messages
    * received are forwarded to the specified message receiver.
    * @param socket the socket
    * @param receiver the message receiver
    * @throws IOException if an I/O error occurs
    */
   void connect(Socket socket, MessageReceiver receiver) throws IOException {
      connected = true;
      outputStream = new ObjectOutputStream(socket.getOutputStream());
      inputStream = new ObjectInputStream(socket.getInputStream());
      thread = new MessageReceiverThread(this, receiver);
      thread.start();
   }

   /**
    * Closes the connection.
    * @throws IOException if an I/O error occurs
    */
   public void disconnect() throws IOException {
      if (connected) {
         connected = false;
         thread.kill();
      }
   }

   public boolean isConnected() {
       return connected;
   }

   /**
    * Sends a message to the connected client or server.
    * @param message the message
    * @throws IOException if an I/O error occurs
    */
   public void sendMessage(Message message) throws IOException {
      if (!connected) {
         throw new IllegalStateException("Not connected");
      }
      outputStream.writeObject(message);
   }
   /**
    * The <code>MessageReceiverThread</code> class waits for messages from the
    * connected client or server and then forwards the messages to a provided
    * message receiver.
    */
   private class MessageReceiverThread extends Thread {
      /**
       * The communicator this message receiver thread is running for
       */
      private TCPCommunicator communicator;
      /**
       * The message receiver to forward received messages to
       */
      private MessageReceiver receiver;
      /**
       * A boolean flag indicating whether or not the message receiver thread
       * should run.  Used to stop the message receiver thread.
       */
      private boolean run = true;

      /**
       * Constructs a new message receiver thread for the specified communicator
       * that will forward received messages to the specified message receiver.
       * @param communicator the communicator this message receiver thread will
       * run for
       * @param receiver the message receiver to forward received messages to
       */
      private MessageReceiverThread(TCPCommunicator communicator,
              MessageReceiver receiver) {
         super("TCP Message Receiver Thread");
         this.communicator = communicator;
         this.receiver = receiver;
      }

      /**
       * Kills the message receiver thread
       * @throws IOException if an I/O error occurs
       */
      private void kill() throws IOException {
         run = false;
         outputStream.close();
      }

      @Override
      public void run() {
         try {
            while (run) {
                //if (mode == MODE_MESSAGE) {
                   Message message = (Message) inputStream.readObject();
                   message.setReceiver(communicator);
                   receiver.receiveMessage(message);
                /*} else {
                    System.out.println("Downloading file...");
                    File downloadFile = File.createTempFile("dmap_", ".tmp");

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(downloadFile));
                    while(dataInputStream.available() > 0){
                        fos.writeByte(dataInputStream.readByte());
                    }
                    mapReceiver.receiveMap(downloadFile);
                    System.out.println("Done reading data");
                    mode = MODE_MESSAGE;
                }*/
            }
         } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
         } catch (IOException ioe) {
            //ioe.printStackTrace();
         }
         connected = false;
         Message message = new Message(Message.DISCONNECTED);
         message.setReceiver(communicator);
         receiver.receiveMessage(message);
      }
   }
}
