package networking;

import gameplay.Player;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class Client {

    private int audioReceivePort;
    private final TCPCommunicator communicator;
    private Player player;
    private int receivePort;
    private final Socket socket;
    private boolean hasMap = false;

    public boolean hasMap() {
        return hasMap;
    }

    public void setHasMap(boolean hasMap) {
        this.hasMap = hasMap;
    }

    Client(Socket socket, MessageReceiver receiver) throws IOException {
        this.socket = socket;
        communicator = new TCPCommunicator();
        communicator.connect(socket, receiver);
    }

    public int getAudioReceivePort() {
        return audioReceivePort;
    }

    public TCPCommunicator getCommunicator() {
        return communicator;
    }

    public Player getPlayer() {
        return player;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public Socket getSocket() {
        return socket;
    }

    void setAudioReceivePort(int port) {
        audioReceivePort = port;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    void setReceivePort(int port) {
        receivePort = port;
    }
}
