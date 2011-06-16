package networking;

/**
 *
 * @author Scott
 * @author Joe Stein
 */
public class ServerOptions {

    private boolean autoStart = false;
    private int healthRegenerationIncrement = 2;
    private String mapName = "Python";
    private int maxPlayers = 8;
    private int minPlayers = 2;
    private int respawnDelay = 5;
    private int scoreLimit = 500;
    private int timeLimit = -1;

    public int getHealthRegenerationIncrement() {
        return healthRegenerationIncrement;
    }

    public String getMap() {
        return mapName;
    }

    public int getMaximumPlayers() {
        return maxPlayers;
    }

    public int getMinimumPlayers() {
        return minPlayers;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public int getScoreLimit() {
        return scoreLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean isAutoStarting() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public void setHealthRegenerationIncrement(int increment) {
        healthRegenerationIncrement = increment;
    }

    public void setMap(String map) {
        mapName = map;
    }

    public void setMaximumPlayers(int players) {
        maxPlayers = players;
    }

    public void setMinimumPlayers(int players) {
        minPlayers = players;
    }

    public void setRespawnDelay(int delay) {
        respawnDelay = delay;
    }

    public void setScoreLimit(int limit) {
        scoreLimit = limit;
    }

    public void setTimeLimit(int limit) {
        timeLimit = limit;
    }
}
