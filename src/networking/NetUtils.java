package networking;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * The <code>NetworkUtilities</code> class provides miscellaneous networking
 * methods to supplement the other networking classes.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class NetUtils {

    private static final int DOUBLE_LENGTH = 8;
    private static final int FLOAT_LENGTH = 4;
    private static final int INT_LENGTH = 4;
    private static final int LONG_LENGTH = 8;
    private static final int SHORT_LENGTH = 2;
    private static BroadcastReceiverThread broadcastReceiverThread;
    private static ServerListUpdaterThread serverListUpdaterThread;
    /**
     * A hashtable containing the available local servers that are broadcasting
     */
    private final static Hashtable<ServerInfo, Long> servers = new Hashtable<ServerInfo, Long>();

    public static byte[] booleansToBytes(Boolean[] booleans) {
        int byteCount = booleans.length / 8;
        if (booleans.length % 8 != 0) {
            byteCount++;
        }
        byte[] bytes = new byte[byteCount];
        booleansToBytes(booleans, bytes, 0);
        return bytes;
    }

    public static void booleansToBytes(Boolean[] booleans, byte[] bytes, int offset) {
        for (int i = 0; i < booleans.length; i++) {
            if (booleans[i]) {
                bytes[i / 8 + offset] |= 1 << (7 - i % 8);
            }
        }
    }

    public static boolean[] byteToBooleans(byte b) {
        return bytesToBooleans(new byte[]{b}, 0, 1);
    }

    public static boolean[] bytesToBooleans(byte[] bytes, int offset, int length) {
        boolean[] booleans = new boolean[length * 8];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = (bytes[i / 8 + offset] & (1 << (7 - i % 8))) == 1 << (7 - i % 8);
        }
        return booleans;
    }

    public static double bytesToDouble(byte[] bytes) {
        return bytesToDouble(bytes, 0);
    }

    public static double bytesToDouble(byte[] bytes, int offset) {
        return Double.longBitsToDouble(bytesToLong(bytes, offset));
    }

    public static float bytesToFloat(byte[] bytes) {
        return bytesToFloat(bytes, 0);
    }

    public static float bytesToFloat(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytesToInt(bytes, offset));
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes, 0);
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        int num = 0;
        for (int i = 0; i < INT_LENGTH; i++) {
            num |= (0xFF & bytes[i + offset]) << (8 * (INT_LENGTH - i - 1));
        }
        return num;
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, 0);
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        long num = 0;
        for (int i = 0; i < LONG_LENGTH; i++) {
            num |= (0xFF & bytes[i + offset]) << (8 * (LONG_LENGTH - i - 1));
        }
        return num;
    }

    public static short bytesToShort(byte[] bytes) {
        return bytesToShort(bytes, 0);
    }

    public static short bytesToShort(byte[] bytes, int offset) {
        short num = 0;
        for (int i = 0; i < SHORT_LENGTH; i++) {
            num |= (0xFF & bytes[i + offset]) << (8 * (SHORT_LENGTH - i - 1));
        }
        return num;
    }

    public static byte[] doubleToBytes(double num) {
        byte[] bytes = new byte[DOUBLE_LENGTH];
        doubleToBytes(num, bytes, 0);
        return bytes;
    }

    public static void doubleToBytes(double num, byte[] bytes, int offset) {
        longToBytes(Double.doubleToLongBits(num), bytes, offset);
    }

    public static byte[] floatToBytes(float num) {
        byte[] bytes = new byte[FLOAT_LENGTH];
        floatToBytes(num, bytes, 0);
        return bytes;
    }

    public static void floatToBytes(float num, byte[] bytes, int offset) {
        intToBytes(Float.floatToIntBits(num), bytes, offset);
    }

    /**
     * Returns an array of server info objects describing each local available
     * server that is broadcasting.  You must call the
     * <code>startListening</code> method a few seconds before calling this
     * method or it will not behave as expected.
     * @return an array of information about the local available servers that are
     * broadcasting
     */
    public static ServerInfo[] getAvailableServers() {
        ServerInfo[] availableServers;
        synchronized (servers) {
            Set<ServerInfo> keySet = servers.keySet();
            availableServers = new ServerInfo[keySet.size()];
            keySet.toArray(availableServers);
        }
        return availableServers;
    }

    public static byte[] intToBytes(int num) {
        byte[] bytes = new byte[INT_LENGTH];
        intToBytes(num, bytes, 0);
        return bytes;
    }

    public static void intToBytes(int num, byte[] bytes, int offset) {
        for (int i = 0; i < INT_LENGTH; i++) {
            bytes[i + offset] = (byte) (num >>> (8 * (INT_LENGTH - i - 1)));
        }
    }

    /**
     * Returns the specified port number or the next available port number for
     * a local server if the specified port number is currently in use.
     * @param port the port number
     * @return the port number if it is available or the next available port
     * number
     */
    static int getNextAvailablePort(int port) {
        try {
            ServerSocket s = new ServerSocket(port);
            s.close();
            return s.getLocalPort();
        } catch (IOException ioe) {
            return getNextAvailablePort(++port);
        }
    }

    public static byte[] longToBytes(long num) {
        byte[] bytes = new byte[LONG_LENGTH];
        longToBytes(num, bytes, 0);
        return bytes;
    }

    public static void longToBytes(long num, byte[] bytes, int offset) {
        for (int i = 0; i < LONG_LENGTH; i++) {
            bytes[i + offset] = (byte) (num >>> (8 * (LONG_LENGTH - i - 1)));
        }
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytes = new byte[SHORT_LENGTH];
        shortToBytes(num, bytes, 0);
        return bytes;
    }

    public static void shortToBytes(short num, byte[] bytes, int offset) {
        for (int i = 0; i < SHORT_LENGTH; i++) {
            bytes[i + offset] = (byte) (num >>> (8 * (SHORT_LENGTH - i - 1)));
        }
    }

    /**
     * Starts listening for server broadcasts and building a list of available
     * servers.
     */
    public static void startListening() {
        try {
            broadcastReceiverThread = new BroadcastReceiverThread();
            broadcastReceiverThread.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        serverListUpdaterThread = new ServerListUpdaterThread();
        serverListUpdaterThread.start();
    }

    /**
     * Stops listening for server broadcasts
     */
    public static void stopListening() {
        if (broadcastReceiverThread != null) {
            broadcastReceiverThread.kill();
            serverListUpdaterThread.kill();
            servers.clear();
        }
    }

    /**
     * The <code>BroadcastReceiverThread</code> listens for server broadcasts and
     * adds broadcasting servers to the server hashtable.
     */
    private static class BroadcastReceiverThread extends Thread {

        private boolean run = true;
        /**
         * The multicast socket to broadcast through
         */
        private final MulticastSocket socket;

        /**
         * Constructs a new daemon broadcast receiver thread.
         * @throws IOException if an I/O error occurs
         */
        private BroadcastReceiverThread() throws IOException {
            super("Broadcast Receiver Thread");
            setDaemon(true);
            socket = new MulticastSocket(BroadcastThread.PORT);
            socket.joinGroup(InetAddress.getByName(BroadcastThread.GROUP_ADDRESS));
        }

        private void kill() {
            run = false;
        }

        @Override
        public void run() {
            try {
                while (run) {
                    byte[] buf = new byte[65536];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(packet.getData());
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(byteStream));
                    ServerInfo serverInfo = (ServerInfo) ois.readObject();
                    servers.put(serverInfo, System.currentTimeMillis());
                    ois.close();
                }
            } catch (SocketException se) {
                JOptionPane.showMessageDialog(null, "You have been disconnected from the server.", "Disconnected", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The <code>ServerListUpdaterThread</code> routinely removes expired server
     * information from the server hashtable in order to keep the list accurate.
     */
    private static class ServerListUpdaterThread extends Thread {

        /**
         * One second in milliseconds
         */
        private static final int ONE_SECOND = 1000;
        /**
         * Three seconds in milliseconds
         */
        private static final int THREE_SECONDS = 3000;
        private boolean run = true;

        /**
         * Contructs a new daemon server list updater thread.
         */
        private ServerListUpdaterThread() {
            super("Server List Updater Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            while (run) {
                long time = System.currentTimeMillis();
                synchronized (servers) {
                    Iterator<ServerInfo> iterator = servers.keySet().iterator();
                    while (iterator.hasNext()) {
                        if (time - servers.get(iterator.next()) > THREE_SECONDS) {
                            iterator.remove();
                        }
                    }
                }
                try {
                    sleep(ONE_SECOND);
                } catch (InterruptedException ie) {
                    if (run) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }
}
