package gameplay;

import graphics.DownloadProgressBar;
import graphics.GraphicsUtilities;
import graphics.GameInitWindow;
import graphics.PlayerScreen;
import graphics.maps.Map;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import networking.TCPCommunicator;
import networking.Message;
import networking.MessageReceiver;
import networking.Server;
import networking.ServerInfo;
import networking.UDPCommunicator;
import sound.AudioStreamer;
import sound.SoundSystem;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 * @author Joe Stein
 */
public class PlayerRunner implements MessageReceiver {

    public static final int SCREEN_HEIGHT = 480;
    public static final int SCREEN_WIDTH = 640;
    private AudioStreamer audioStreamer;
    private boolean connected = false;
    private boolean countdownActive = false;
    private int countdownNumber;
    private Grid grid;
    private Byte id = null;
    private PlayerScreen screen;
    private SoundSystem soundSystem;
    private TCPCommunicator tcpCommunicator = new TCPCommunicator();
    private UDPCommunicator udpCommunicator = new UDPCommunicator();
    private String username;
    private boolean retrievingMap = false;

    public boolean isRetrievingMap() {
        return retrievingMap;
    }

    public PlayerRunner(String username, ServerInfo info)
            throws IOException {
        this.username = username;
        soundSystem = new SoundSystem();
        tcpCommunicator.connect(username, this, info);
    }

    public int getCountdownNumber() {
        return countdownNumber;
    }

    public Grid getGrid() {
        return grid;
    }

    public byte getPlayerID() {
        return id;
    }

    public SoundSystem getSoundSystem() {
        return soundSystem;
    }

    public boolean isCountdownActive() {
        return countdownActive;
    }

    public void kill(boolean confirmExit) {
        int confirm = JOptionPane.YES_OPTION;
        if (confirmExit && (grid == null || !grid.isGameOver())) {
            confirm = JOptionPane.showConfirmDialog(screen,
                    "Are you sure you want to exit?",
                    "Leaving Game", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        }
        if (confirm == JOptionPane.YES_OPTION) {
            audioStreamer.kill();
            if (grid != null) {
                grid.kill();
            }
            soundSystem.stopBackgroundMusic();
            screen.kill();
            screen.dispose();
            try {
                tcpCommunicator.disconnect();
                udpCommunicator.disconnect();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            new GameInitWindow().setVisible(true);
        }
    }
    private int tmpFileSize;
    private int receivedBytes;
    private String tmpFileName;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private DownloadProgressBar dpb;

    private void getMapFromServer(Message message) {
        try {
            // Send map request (OBSOLETE)
            // tcpCommunicator.sendMessage(new Message(Message.NEED_MAP));

            // Wait for send
            // Message message = (Message) tcpCommunicator.getInputStream().readObject();
            Object[] content = (Object[]) message.getContents();
            if (content[0] instanceof String) {
                // FILE TRANSFER INITIATED, RECEIVE INFO
                retrievingMap = true;
                tmpFileName = (String) content[0];
                tmpFileSize = (Integer) content[1];
                String separator = System.getProperty("file.separator");
                // Create and initiate file
                File f = new File("maps" + separator + tmpFileName);
                File mapsDir = new File("maps");
                if (!mapsDir.exists() || !mapsDir.isDirectory()) {
                    mapsDir.mkdir();
                }
                f.createNewFile();
                f.setWritable(true);
                f.setReadable(true);
                fos = new FileOutputStream("maps" + separator + tmpFileName);
                bos = new BufferedOutputStream(fos);

                dpb = new DownloadProgressBar(screen, tmpFileSize);

            } else if (content[0] instanceof byte[]) {
                Object[] fileInfo = (Object[]) message.getContents();
                byte[] fileByteArray = (byte[]) fileInfo[0];
                bos.write(fileByteArray, 0, fileByteArray.length);
                receivedBytes += fileByteArray.length;
                dpb.updateBar(receivedBytes);
                bos.flush();
            } else {
                bos.close();
                fos.flush();
                fos.close();
                dpb.release();
                retrievingMap = false;
                enterGame();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    private Message deliver_info_message;

    private void enterGame() {
        try {
            tcpCommunicator.sendMessage(new Message(Message.MAP_DONE));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Object[] content = (Object[]) deliver_info_message.getContents();
        String mapName = (String) content[0];
        try {
            grid.setMap(new Map(mapName));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        for (int i = 1; i < content.length; i++) {
            Object obj = content[i];
            if (obj != null) {
                Object[] obj2 = (Object[]) obj;
                grid.setUsername((Byte) obj2[0], (String) obj2[1]);
            }
        }
        try {
            tcpCommunicator.disconnect();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public synchronized void receiveMessage(Message message) {
        if (message.getType() == Message.DELIVER_INFO) {
            countdownActive = false;
            Object[] content = (Object[]) message.getContents();
            String mapName = (String) content[0];
            deliver_info_message = message;
            if (!GraphicsUtilities.getAvailableMaps().contains(mapName)) {
                int choice = JOptionPane.showConfirmDialog(null, "Would you like to download \"" + mapName + "\"?", "Download Map", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        tcpCommunicator.sendMessage(new Message(Message.NEED_MAP));
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerRunner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    /*if (getMapFromServer()) {
                    System.out.println("Map retrieved.");
                    } else {
                    JOptionPane.showMessageDialog(null,"Map download failed.","Error",JOptionPane.ERROR_MESSAGE);
                    kill(false);
                    }*/
                } else {
                    JOptionPane.showMessageDialog(null, "Your version of Domination does "
                            + "not include \"" + mapName + "\", the map being used by the server."
                            + "\nIn order to play, you must either update your version of Domination, or"
                            + " put the map\nfile being used by the server into a folder"
                            + " named \"maps\" in the same directory as Domination.jar.",
                            "Could not find map file", JOptionPane.ERROR_MESSAGE);
                    kill(false);
                }
            } else {
                enterGame();
            }

        } else if (message.getType() == Message.NEED_MAP) {
            getMapFromServer(message);
        } else if (message.getType() == Message.GRID_UPDATE) {
            if (grid.set((byte[]) message.getContents()) && !connected) {
                connected = true;
                soundSystem.playBackgroundMusic(0.5);
            }
        } else if (message.getType() == Message.PLAYER_CONNECTED) {
            Object[] content = (Object[]) message.getContents();
            int receivePort = (Integer) content[0];
            int audioReceivePort = (Integer) content[1];
            long serverID = (Long) content[2];
            byte playerID = (Byte) content[3];
            grid = new Grid(serverID, false);
            id = playerID;
            udpCommunicator = new UDPCommunicator();
            audioStreamer = new AudioStreamer(id);
            audioStreamer.start(audioReceivePort, Server.AUDIO_RECEIVE_PORT);
            try {
                udpCommunicator.connect(Message.GRID_UPDATE, this, receivePort, Server.UDP_RECEIVE_PORT);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            screen = new PlayerScreen(this, id);
            screen.setVisible(true);
        } else if (message.getType() == Message.SCREEN_STAT_UPDATE) {
            countdownNumber = (Integer) message.getContents();
            countdownActive = true;
            System.out.println("Countdown message received");
            if (countdownNumber == 0) {
                countdownActive = false;
            }
        } else if (message.getType() == Message.USERNAME_TAKEN) {
            boolean keepRequesting = true;
            while (keepRequesting) {
                String input = JOptionPane.showInputDialog(screen,
                        "The username you entered is already in use."
                        + "\nPlease choose" + " another one (" + GameInitWindow.USERNAME_LENGTH_LIMIT + " characters max):", "Username Taken",
                        JOptionPane.PLAIN_MESSAGE);
                if (input == null) {
                    keepRequesting = false;
                    kill(false);
                    new GameInitWindow().setVisible(true);
                } else {
                    if (input.length() == 0 || input.length() > GameInitWindow.USERNAME_LENGTH_LIMIT) {
                        JOptionPane.showMessageDialog(screen, "The username you entered" + " was invalid.", "Invalid Username",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        keepRequesting = false;
                        username = input;
                        try {
                            tcpCommunicator.sendMessage(new Message(
                                    Message.CONNECT_TO_SERVER, new Object[]{
                                        username}));
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            }
        } else if (message.getType() == Message.DISCONNECTED) {
            System.out.println("Disconnect message received.");
            //JOptionPane.showMessageDialog(null, "You have been disconnected from the server.", "Disconnected", JOptionPane.ERROR_MESSAGE);
        } else {
            System.out.println(message);
        }
    }

    public void sendMessage(byte[] bytes) {
        try {
            udpCommunicator.sendMessage(bytes);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void setCountdownNumber(int countdownNumber) {
        this.countdownNumber = countdownNumber;
    }
}
