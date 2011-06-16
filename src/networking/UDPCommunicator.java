package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * The <code>UDPCommunicator</code> class facilitates communication between a
 * client and a server through UDP.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class UDPCommunicator {

    private boolean connected = false;
    private byte messageType;
    private DatagramSocket receiveSocket;
    private InetAddress sendAddress;
    private int sendPort;
    private DatagramSocket sendSocket;
    private MessageReceiverThread thread;

    public void connect(byte messageType, MessageReceiver receiver, int receivePort)
            throws IOException {
        connect(messageType, receiver, receivePort, 0);
    }

    public void connect(byte messageType, MessageReceiver receiver, int receivePort, int sendPort)
            throws IOException {
        if (connected) {
            throw new IllegalStateException("Already connected");
        }
        this.messageType = messageType;
        sendSocket = new DatagramSocket(null);
        receiveSocket = new DatagramSocket(null);
        receiveSocket.setReuseAddress(true);
        receiveSocket.bind(new InetSocketAddress(receivePort));
        this.sendPort = sendPort;
        thread = new MessageReceiverThread(receiver);
        thread.start();
        connected = true;
    }

    private void debug(String message) {
        //uncomment for debugging purposes
        //System.out.println("UDPCommunicator: " + message);
    }

    public void disconnect() throws IOException {
        thread.kill();
    }

    public void sendMessage(byte[] bytes) throws IOException {
        if (!connected) {
            throw new IllegalStateException("Not connected");
        }
        if (sendAddress == null) {
            throw new IllegalStateException("Send address unknown");
        }
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                sendAddress, sendPort);
        sendSocket.send(packet);
        debug("Sent message to " + sendAddress + " at port " + sendPort);
    }

    public void setSendAddress(InetAddress address) {
        sendAddress = address;
    }

    private class MessageReceiverThread extends Thread {

        private MessageReceiver receiver;
        private boolean run = true;

        private MessageReceiverThread(MessageReceiver receiver) throws IOException {
            super("UDP Message Receiver Thread");
            this.receiver = receiver;
        }

        private void kill() throws IOException {
            run = false;
            receiveSocket.close();
        }

        @Override
        public void run() {
            while (run) {
                try {
                    byte[] buf = new byte[65536];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    receiveSocket.receive(packet);
                    sendAddress = packet.getAddress();
                    byte[] temp = new byte[packet.getLength()];
                    System.arraycopy(buf, 0, temp, 0, temp.length);
                    receiver.receiveMessage(new Message(messageType, temp));
                } catch (IOException ioe) {
                    //ignore
                }
            }
            connected = false;
            receiver.receiveMessage(new Message(Message.DISCONNECTED));

        }
    }
}
