package networking;

import gameplay.Grid;
import gameplay.Player;
import gameplay.Team;
import graphics.Sprite;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 * @author Joe Stein
 */
public class Server implements MessageReceiver {

    public static final int AUDIO_RECEIVE_PORT = 9874;
    private static final String EXTERNAL_IP_RETRIEVAL_SITE = "http://whatismyip.com/automation/n09230945.asp";
    public static final int UDP_RECEIVE_PORT = 9875;
    public static final int UDP_SEND_PORT = 9876;
    private final UDPCommunicator audioCommunicator = new UDPCommunicator();
    private final DatagramSocket audioSocket = new DatagramSocket();
    private final AudioUpdaterThread audioUpdaterThread = new AudioUpdaterThread();
    private BroadcastThread broadcastThread;
    private final ClientListenerThread clientListenerThread;
    private final ArrayList<Client> clients = new ArrayList<Client>();
    private CountdownBroadcastThread countdownBroadcastThread;
    private static int currSendPort = UDP_SEND_PORT;
    private String externalIPAddress;
    private GameBroadcastThread gameBroadcastThread;
    private GameListenerThread gameListenerThread;
    private final Grid grid;
    private boolean isClosing = false;
    private String internalIPAddress;
    private int port;
    private ServerOptions options;
    private final MessageReceiver receiver;
    private final long serverID = new Random().nextLong();

    public Server(boolean broadcast, String name, MessageReceiver receiver, ServerOptions options)
            throws IOException {
        this.options = options;
        grid = new Grid(serverID, true);
        grid.setOptions(this.options);
        this.receiver = receiver;
        clientListenerThread = new ClientListenerThread(0);
        clientListenerThread.start();
        if (broadcast) {
            broadcastThread = new BroadcastThread(name,
                    clientListenerThread.socket.getLocalPort());
            broadcastThread.start();
        }
        internalIPAddress = InetAddress.getLocalHost().getHostAddress();
        setExternalIPAddress();

        audioCommunicator.connect(Message.AUDIO, new MessageReceiver() {

            public void receiveMessage(Message message) {
                byte[] contents = (byte[]) message.getContents();
                if (contents != null) {
                    for (Client client : clients) {
                        try {
                            audioSocket.send(new DatagramPacket(contents, contents.length,
                                    client.getSocket().getInetAddress(), client.getAudioReceivePort()));
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            }
        }, AUDIO_RECEIVE_PORT);

        audioUpdaterThread.start();
    }

    public void close() throws IOException {
        isClosing = true;
        audioCommunicator.disconnect();
        stopBroadcasting();
        clientListenerThread.kill();
        if (countdownBroadcastThread != null) {
            countdownBroadcastThread.kill();
        }
        for (Client c : clients) {
            c.getCommunicator().disconnect();
        }
        grid.kill();
        if (gameBroadcastThread != null) {
            gameBroadcastThread.kill();
        }
        if (gameListenerThread != null) {
            gameListenerThread.kill();
        }
        audioUpdaterThread.kill();
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public String getExternalIPAddress() {
        return externalIPAddress;
    }

    public String getInternalIPAddress() {
        return internalIPAddress;
    }

    private synchronized byte getNextAvailablePlayerID() {
        byte id = -128;
        while (true) {
            boolean good = true;
            for (Client client : clients) {
                Player player = client.getPlayer();
                if (player != null && player.getPlayerID() == id) {
                    good = false;
                    break;
                }
            }
            if (good) {
                return id;
            }
            id++;
        }
    }

    public int getPort() {
        return port;
    }

    private ArrayList<Client> getReadyClients() {
        ArrayList<Client> readyClients = new ArrayList<Client>();
        for (int i = clients.size() - 1; i >= 0; i--) {
            if (clients.get(i).getPlayer() != null) {
                readyClients.add(clients.get(i));
            } else {
                clients.remove(i);
            }
        }
        return readyClients;
    }

    private Client getSender(Message message) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getCommunicator() == message.getReceiver()) {
                return clients.get(i);
            }
        }
        return null;
    }

    public synchronized void receiveMessage(Message message) {
        if (!isClosing) {
            if (message.getType() == Message.CONNECT_TO_SERVER) {
                System.out.println("New player has connected to server");
                Object[] contents = (Object[]) message.getContents();
                String username = (String) contents[0];
                Client sender = getSender(message);
                if (usernameInUse((username))) {
                    try {
                        sender.getCommunicator().sendMessage(
                                new Message(Message.USERNAME_TAKEN));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else {
                    byte playerID = getNextAvailablePlayerID();
                    grid.setUsername(playerID, username);
                    sender.setPlayer(new Player(grid, playerID));
                    try {
                        sender.setAudioReceivePort(currSendPort++);
                        sender.setReceivePort(currSendPort++);
                        sender.getCommunicator().sendMessage(new Message(
                                Message.PLAYER_CONNECTED, new Object[]{
                                    sender.getReceivePort(),
                                    sender.getAudioReceivePort(), serverID,
                                    playerID
                                }));
                        if (options.isAutoStarting()) {
                            runCountdownCheck();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    receiver.receiveMessage(new Message(Message.PLAYERS_CHANGED));
                }
            } else if (message.getType() == Message.DISCONNECTED && clientListenerThread.run) {
                clients.remove(getSender(message));
                receiver.receiveMessage(new Message(Message.PLAYERS_CHANGED));
            } else if (message.getType() == Message.NEED_MAP) {
                Client sender = getSender(message);
                sendMap(sender);
            } else if (message.getType() == Message.MAP_DONE) {
                getSender(message).setHasMap(true);
                if (grid.getPlayer(getSender(message).getPlayer().getPlayerID()) == null) {
                    grid.add(getSender(message).getPlayer());
                }
            } else {
                receiver.receiveMessage(message);
            }
        }
    }

    private void sendMap(Client sender) {
        System.out.println("Request for map received");
        File currentMap = new File("maps" + System.getProperty("file.separator") + options.getMap() + ".dmap");
        System.out.println("Looking for map file at: " + currentMap.getAbsolutePath());
        System.out.println("Map file exists: " + currentMap.exists());
        if (currentMap.exists()) {
            int bytesRead = 0;
            int bytesTotal = (int) currentMap.length();
            try {
                sender.getCommunicator().sendMessage(new Message(Message.NEED_MAP, new Object[]{currentMap.getName(), (int) currentMap.length()}));
                DataInputStream fis = new DataInputStream(new FileInputStream(currentMap));
                while (bytesRead < bytesTotal) {
                    byte[] buffer = new byte[10000];
                    int fisRead = fis.read(buffer, 0, buffer.length);
                    System.out.println("bytesRead:" + bytesRead + "/buffer.length:" + buffer.length);
                    Object[] fileStuff = new Object[]{buffer};
                    sender.getCommunicator().sendMessage(new Message(Message.NEED_MAP, fileStuff));
                    bytesRead += fisRead;
                    System.out.println(bytesRead + "/" + bytesTotal);
                }
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("AT TIME OF ERROR:");
                System.out.println("Bytes read: " + bytesRead + " Bytes total: " + bytesTotal);
            }
        }
    }

    private void runCountdownCheck() {
        //System.out.println("Countdown check has been called");
        ArrayList<Client> readyClients = getReadyClients();
        //System.out.println("Ready clients: " + readyClients.size());
        //System.out.println("Minimum players: " + options.getMinimumPlayers());
        if (readyClients.size() > options.getMinimumPlayers()) {
            initiateCountdown(20);
        }
    }

    private void initiateCountdown(int seconds) {
        countdownBroadcastThread = new CountdownBroadcastThread(seconds);
        countdownBroadcastThread.start();
    }

    private void setExternalIPAddress() {
        try {
            URL url = new URL(EXTERNAL_IP_RETRIEVAL_SITE);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            externalIPAddress = in.readLine();
        } catch (IOException ioe) {
            externalIPAddress = "Unavailable";
        }
    }

    /**
     * Assigns teams, starts game broadcast and listener threads, and generally
     * sets up for the game.
     * @throws IOException if an I/O error occurs
     */
    private void startGame() throws IOException {
        Object[] info = new Object[clients.size() + 1];
        info[0] = grid.getOptions().getMap();
        ArrayList<Client> clientList = getReadyClients();
        for (int i = 0; i < clientList.size(); i++) {
            info[i + 1] = new Object[]{clients.get(i).getPlayer().getPlayerID(),
                        clients.get(i).getPlayer().getUsername()};
        }
        byte team = Team.TEAM_GREEN;
        for (Client client : clientList) {
            client.getCommunicator().sendMessage(new Message(
                    Message.DELIVER_INFO, info));
            if (team == Team.TEAM_GREEN) {
                team = Team.TEAM_RED;
            } else {
                team = Team.TEAM_GREEN;
            }
            client.getPlayer().setSprite(Sprite.getSpriteForTeam(team));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        /* Below this comment, the code essentially starts the game by adding all players.
         * The code should be modified to only add the player to the grid once a message has
         * been received indicating that the client has received the map (if they downloaded
         * it) and is ready to start the game. This will probably involve waiting until all
         * players have sent the "ready" message before adding them to the grid.
         */
        clientListenerThread.kill();
        for (Client client : clientList) {
            if (client.hasMap()) {
                grid.add(client.getPlayer());
            }
        }
        grid.start();
        gameBroadcastThread = new GameBroadcastThread();
        gameBroadcastThread.start();
        gameListenerThread = new GameListenerThread();
        gameListenerThread.start();
    }

    /** 
     * Starts game with a specified countdown time. Does not directly initiate game
     * setup.
     * @param seconds The number of seconds to count down. If this value is equal
     * to or less than 0, the game will start immediately as if <code>startGame()</code>
     * (with no parameters) had been called. Otherwise, the game will start in the specified
     * number of seconds.
     * @see startGame()
     */
    public synchronized void startGame(int seconds) {
        stopBroadcasting();
        if (seconds < 1) {
            try {
                startGame();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            initiateCountdown(seconds);
        }
    }

    private void stopBroadcasting() {
        if (broadcastThread != null) {
            broadcastThread.kill();
            broadcastThread = null;
        }
    }

    private boolean usernameInUse(String username) {
        for (Client client : clients) {
            if (client.getPlayer() != null && client.getPlayer().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private class CountdownBroadcastThread extends Thread {

        private boolean run = true;
        private final static int COUNTDOWN_MS_INTERVAL = 1000;
        private int countdownNumber = 20;

        CountdownBroadcastThread(int seconds) {
            super("Countdown Broadcast Thread");
            countdownNumber = seconds;
            setDaemon(true);
            System.out.println("Countdown broadcast thread started");
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            try {
                while (run) {
                    if (countdownNumber < 1) {
                        run = false;
                        startGame();
                        this.kill();
                    } else {
                        Message countdownUpdateMessage = new Message(Message.SCREEN_STAT_UPDATE, countdownNumber);
                        for (Client client : clients) {
                            System.out.println("Countdown message sent");
                            client.getCommunicator().sendMessage(countdownUpdateMessage);
                        }
                        System.out.println("Countdown: " + countdownNumber);
                        countdownNumber--;
                        sleep(COUNTDOWN_MS_INTERVAL);
                    }
                }
            } catch (Exception e) {
                if (run) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AudioUpdaterThread extends Thread {

        private int DELAY = 500;
        private boolean run = true;

        AudioUpdaterThread() {
            super("Audio Updater Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            while (run) {
                for (Client client : clients) {
                    try {
                        audioSocket.send(new DatagramPacket(new byte[]{}, 0,
                                client.getSocket().getInetAddress(), client.getAudioReceivePort()));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException ie) {
                    //ignore
                }
            }
        }
    }

    private class ClientListenerThread extends Thread {

        private boolean run = true;
        private ServerSocket socket;

        private ClientListenerThread(int port) throws IOException {
            super("Client Listener Thread");
            socket = new ServerSocket(port);
            Server.this.port = socket.getLocalPort();
        }

        private void kill() throws IOException {
            run = false;
            socket.close();
        }

        @Override
        public void run() {
            while (run) {
                try {
                    clients.add(new Client(socket.accept(), Server.this));
                } catch (IOException ioe) {
                    //ioe.printStackTrace();
                }
            }
        }
    }

    private class GameBroadcastThread extends Thread {

        private final static int DELAY = 20;
        private boolean run = true;
        private final DatagramSocket socket;

        GameBroadcastThread() throws IOException {
            super("Game Broadcast Thread");
            setDaemon(true);
            socket = new DatagramSocket();
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            try {
                while (run) {
                    byte[] bytes = grid.toByteArray();
                    for (Client client : clients) {
                        socket.send(new DatagramPacket(bytes, bytes.length,
                                client.getSocket().getInetAddress(), client.getReceivePort()));
                    }
                    sleep(DELAY);
                }
            } catch (Exception e) {
                if (run) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GameListenerThread extends Thread {

        private boolean run = true;
        private final DatagramSocket socket;

        private GameListenerThread() throws IOException {
            super("Game Listener Thread");
            setDaemon(true);
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(UDP_RECEIVE_PORT));
            System.out.println("Server$GameListenerThread: Game listener thread created");
        }

        private void kill() {
            run = false;
            socket.close();
            System.out.println("Server$GameListenerThread: Game listener thread killed");
        }

        @Override
        public void run() {
            while (run) {
                try {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    grid.performChange(packet.getData());
                } catch (Exception e) {
                    if (run) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
