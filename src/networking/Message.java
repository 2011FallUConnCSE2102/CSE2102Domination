package networking;

import java.io.Serializable;

/**
 * The <code>Message</code> class provides an easy way to package information
 * sent across the network.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 * @author Joe Stein
 */
public class Message implements Serializable {

    /**
     * Indicates the message contains audio
     */
    public static final byte AUDIO = -128;
    /**
     * Indicates a player is attempting to connect to the server.  The message
     * contents will contain information about the player.
     */
    public static final byte CONNECT_TO_SERVER = -127;
    /**
     * Indicates the message contains the information about the game
     */
    public static final byte DELIVER_INFO = -126;
    /**
     * Indicates the connection was closed between the client and the server.
     * The message contents will be empty.
     */
    public static final byte DISCONNECTED = -125;
    /**
     * Indicates the server has updated the grid.  The message contents will
     * contain information about the grid.
     */
    public static final byte GRID_UPDATE = -124;
    /**
     * Indicates a player connected to the server.  The message contents will
     * contain information about the player.
     */
    public static final byte PLAYER_CONNECTED = -123;
    /**
     * Indicates that players have either connected to or disconnected from the
     * server.
     */
    public static final byte PLAYERS_CHANGED = -122;
    /**
     * Indicates the username requested is already taken and a new one must be
     * chosen.  The message contents will be empty.
     */
    public static final byte USERNAME_TAKEN = -121;
    /**
     * Indicates a status update. Currently used for sending countdown updates.
     */
    public static final byte SCREEN_STAT_UPDATE = -121;
    /**
     * Indicates a map file message. If there is no content in this message, it
     * is a map file request. If there is content, it should be the map file.
     */
    public static final byte NEED_MAP = -120;
    /**
     * Indicates that the map transfer has been completed and that the client
     * which sent the message is ready to join the active game. Contents remain
     * empty.
     */
    public static final byte MAP_DONE = -119;
    /**
     * The contents of the message
     */
    private final Object contents;
    /**
     * The communicator that received this message
     */
    private TCPCommunicator receiver;
    /**
     * The type of message
     */
    private final byte type;

    /**
     * Constructs a new empty message of the specified type.
     * @param type the message type
     */
    public Message(byte type) {
        this(type, null);
    }

    /**
     * Constructs a new message of the specified type and with the specified
     * contents.
     * @param type the message type
     * @param contents the message contents
     */
    public Message(byte type, Object contents) {
        this.type = type;
        this.contents = contents;
    }

    /**
     * Returns the contents of the message.
     * @return the message contents
     */
    public Object getContents() {
        return contents;
    }

    /**
     * Returns the communicator that received the message.
     * @return the message receiver
     */
    public TCPCommunicator getReceiver() {
        return receiver;
    }

    /**
     * Returns the type of the message.
     * @return the message type
     */
    public byte getType() {
        return type;
    }

    /**
     * Sets the receiver to the specified communicator.
     * @param receiver the communicator that received the message
     */
    public void setReceiver(TCPCommunicator receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Message {Type: " + getType() + ", Contents: " + getContents()
                + "}";
    }
}
