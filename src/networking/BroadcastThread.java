package networking;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * The <code>BroadcastThread</code> routinely broadcasts the server's
 * information across the local area network.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class BroadcastThread extends Thread {

    /**
     * The group multicast address to use for server information broadcasting
     */
    final static String GROUP_ADDRESS = "235.211.7.108";
    /**
     * One second in milliseconds
     */
    private final static int ONE_SECOND = 1000;
    /**
     * The port number to use for broadcasting
     */
    final static int PORT = 43672;
    /**
     * The datagram packet to broadcast
     */
    private final DatagramPacket packet;
    /**
     * A boolean flag indicating whether or not the broadcast thread should
     * run.  Used to stop the broadcasting thread.
     */
    private boolean run = true;
    /**
     * The multicast socket to broadcast through
     */
    private final MulticastSocket socket;

    /**
     * Constructs a new broadcast thread with the specified server name and
     * port (the ip address is inferred).
     * @param serverName the server's name
     * @param port the server's port number
     * @throws IOException if an I/O error occurs
     */
    BroadcastThread(String serverName, int port) throws IOException {
        super("Broadcast Thread");
        socket = new MulticastSocket(port);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(baos));
        oos.writeObject(new ServerInfo(
                InetAddress.getLocalHost().getHostAddress(), port,
                serverName));
        oos.flush();
        byte[] buf = baos.toByteArray();
        oos.close();
        baos.close();
        packet = new DatagramPacket(buf, buf.length,
                InetAddress.getByName(GROUP_ADDRESS), PORT);
    }

    /**
     * Kills the broadcast thread.
     */
    void kill() {
        run = false;
        socket.close();
        interrupt();
    }

    @Override
    public void run() {
        try {
            while (run) {
                socket.send(packet);
                sleep(ONE_SECOND);
            }
        } catch (Exception e) {
            if (run) {
                e.printStackTrace();
            }
        }
    }
}
