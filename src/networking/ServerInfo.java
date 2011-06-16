package networking;

import java.io.Serializable;

/**
 * The <code>ServerInfo</code> class provides a way to store server information.
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class ServerInfo implements Serializable {

    /**
     * The ip address of the server
     */
    private final String ipAddress;
    /**
     * The name of the server
     */
    private final String name;
    /**
     * The port number of the server
     */
    private final int port;

    /**
     * Constructs a new server info object containing the provided information
     * with its name set to null.
     * @param ipAddress the server's ip address
     * @param port the server's port number
     */
    public ServerInfo(String ipAddress, int port) {
        this(ipAddress, port, null);
    }

    /**
     * Constructs a new server info object containing the provided information.
     * @param ipAddress the server's ip address
     * @param port the server's port number
     * @param serverName the server's name
     */
    public ServerInfo(String ipAddress, int port, String serverName) {
        this.ipAddress = ipAddress;
        this.port = port;
        name = serverName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ServerInfo) {
            ServerInfo other = (ServerInfo) obj;
            return (ipAddress.equals(other.ipAddress) && port == other.port
                    && name.equals(other.name));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash
                + (this.ipAddress != null ? this.ipAddress.hashCode() : 0);
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + this.port;
        return hash;
    }

    /**
     * Returns the server's ip address.
     * @return the ip address of the server
     */
    public String getServerIPAddress() {
        return ipAddress;
    }

    /**
     * Returns the server's port number.
     * @return the port number of the server
     */
    public int getServerPort() {
        return port;
    }

    /**
     * Returns the server's name.
     * @return the name of the server
     */
    public String getServerName() {
        return name;
    }

    @Override
    public String toString() {
        return "ServerInfo {Server Name: " + getServerName() + ", IP Address: "
                + getServerIPAddress() + ", Port: " + getServerPort() + "}";
    }
}
