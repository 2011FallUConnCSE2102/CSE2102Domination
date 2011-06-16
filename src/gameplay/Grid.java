package gameplay;

import graphics.Sprite;
import graphics.maps.Map;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import networking.NetUtils;
import networking.ServerOptions;

/**
 * Supports maps up to 65536 x 65536 pixels
 * @author Scott
 */
public class Grid {

    public static final byte ACTIVATE_KONAMI_CODE = -128;
    public static final byte CHANGE_DIRECTION = -127;
    public static final byte FIRE_BULLET = -126;
    public static final byte KEEP_ALIVE = -125;
    public static final byte MOVE = -124;
    private boolean gameOver;
    private GridUpdaterThread gridUpdaterThread;
    private long lastUpdate;
    private boolean gameStarted = false;
    private Map map;
    private final HashMap<Integer, GridObject> objects = new HashMap<Integer, GridObject>();
    private short secondsLeft;
    private final boolean serverGrid;
    private final byte[] serverID;
    private ServerOptions serverOptions = new ServerOptions();
    private UpdateCheckerThread updateCheckerThread;
    private final HashMap<Byte, String> usernames = new HashMap<Byte, String>();

    public Grid(long serverID, boolean serverGrid) {
        this.serverID = NetUtils.longToBytes(serverID);
        this.serverGrid = serverGrid;
        debug("New grid created (Server ID: " + serverID + ", Server Grid: " + serverGrid + ")");
    }

    public synchronized void add(GridObject obj) {
        objects.put(obj.getObjectID(), obj);
        if ((obj instanceof Player) && gameStarted) {
            ((Player) obj).spawn();
        }
        debug("New grid object added (ID: " + obj.getObjectID() + ", " + obj + ")");
    }

    public void changeDirection(int objectID, float direction) {
        ((DisplayableGridObject) objects.get(objectID)).setDirection(direction);
        debug("Changed direction of grid object (ID: " + objectID + ", " + objects.get(objectID) + ", Direction: " + direction + ")");
    }

    private void debug(String message) {
        //uncomment for debugging purposes
        //System.out.println("Grid: " + message);
    }

    public synchronized ArrayList<Base> getBases() {
        ArrayList<Base> bases = new ArrayList<Base>();
        bases.add(null);
        bases.add(null);
        bases.add(null);
        for (GridObject object : objects.values()) {
            if (object instanceof Base) {
                Base temp = (Base) object;
                bases.set(temp.getBaseType() - Base.BASE_ONE, temp);
            }
        }
        while (bases.contains(null)) {
            bases.remove(null);
        }
        return bases;
    }

    synchronized ArrayList<Bullet> getBullets() {
        ArrayList<Bullet> bullets = new ArrayList<Bullet>();
        for (GridObject object : objects.values()) {
            if (object instanceof Bullet) {
                bullets.add((Bullet) object);
            }
        }
        return bullets;
    }

    public byte[] getBytesForChange(byte changeType, byte playerID, Object[] value) {
        byte[] bytes;
        if (changeType == Grid.ACTIVATE_KONAMI_CODE) {
            bytes = new byte[10];
        } else if (changeType == Grid.CHANGE_DIRECTION) {
            bytes = new byte[14];
            NetUtils.floatToBytes((Float) value[0], bytes, 10);
        } else if (changeType == Grid.FIRE_BULLET) {
            bytes = new byte[18];
            NetUtils.floatToBytes((Float) value[0], bytes, 10);
            NetUtils.shortToBytes((Short) value[1], bytes, 14);
            NetUtils.shortToBytes((Short) value[2], bytes, 16);
        } else if (changeType == Grid.KEEP_ALIVE) {
            bytes = new byte[10];
        } else if (changeType == Grid.MOVE) {
            bytes = new byte[11];
            NetUtils.booleansToBytes((Boolean[]) value, bytes, 10);
        } else {
            return null;
        }
        System.arraycopy(serverID, 0, bytes, 0, 8);
        bytes[8] = changeType;
        bytes[9] = playerID;
        return bytes;
    }

    public synchronized ArrayList<DisplayableGridObject> getDisplayableGridObjects() {
        ArrayList<DisplayableGridObject> displayableGridObjects = new ArrayList<DisplayableGridObject>();
        for (GridObject object : objects.values()) {
            if (object instanceof DisplayableGridObject) {
                displayableGridObjects.add((DisplayableGridObject) object);
            }
        }
        return displayableGridObjects;
    }

    public synchronized GridObject[] getGridObjects() {
        GridObject[] objectArray = new GridObject[objects.size()];
        int index = 0;
        for (GridObject object : objects.values()) {
            objectArray[index++] = object;
        }
        return objectArray;
    }

    public Map getMap() {
        return map;
    }

    public ServerOptions getOptions() {
        return serverOptions;
    }

    public synchronized Player getPlayer(byte playerID) {
        for (GridObject object : objects.values()) {
            if (object instanceof Player && ((Player) object).getPlayerID() == playerID) {
                return (Player) objects.get(object.getObjectID());
            }
        }
        return null;
    }

    public synchronized ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (GridObject object : objects.values()) {
            if (object instanceof Player) {
                players.add((Player) object);
            }
        }
        return players;
    }

    public short getSecondsLeft() {
        return secondsLeft;
    }

    public synchronized Team getTeam(byte teamID) {
        for (GridObject object : objects.values()) {
            if (object instanceof Team && ((Team) object).getTeamID() == teamID) {
                return (Team) object;
            }
        }
        return null;
    }

    String getUsername(byte playerID) {
        return usernames.get(playerID);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void kill() {
        if (gridUpdaterThread != null) {
            gridUpdaterThread.kill();
        }
        if (updateCheckerThread != null) {
            updateCheckerThread.kill();
        }
        map = null;
        debug("Killed grid");
    }

    public void performChange(byte[] bytes) {
        for (int i = 0; i < 8; i++) {
            if (bytes[i] != serverID[i]) {
                debug("Change not performed because server ID is invalid");
                return;
            }
        }
        Player player = ((Player) getPlayer(bytes[9]));
        if (player == null) {
            debug("Change not performed because player is null");
            return;
        }
        if (bytes[8] == Grid.ACTIVATE_KONAMI_CODE) {
            if (player.getSprite() == Sprite.PLAYER) {
                player.setSprite(Sprite.TANK);
            } else if (player.getSprite() == Sprite.PLAYER_RED) {
                player.setSprite(Sprite.TANK_RED);
            } else if (player.getSprite() == Sprite.TANK) {
                player.setSprite(Sprite.PLAYER);
            } else if (player.getSprite() == Sprite.TANK_RED) {
                player.setSprite(Sprite.PLAYER_RED);
            }
            debug("Performed change (ACTIVATE_KONAMI_CODE)");
        } else if (bytes[8] == Grid.CHANGE_DIRECTION) {
            if (player.isAlive()) {
                player.setDirection(new byte[]{bytes[10], bytes[11], bytes[12],
                            bytes[13]});
            }
            debug("Performed change (CHANGE_DIRECTION)");
        } else if (bytes[8] == Grid.FIRE_BULLET) {
            if (player.isAlive()) {
                add(new Bullet(this, player,
                        new byte[]{bytes[10], bytes[11], bytes[12], bytes[13]},
                        new byte[]{bytes[14], bytes[15]},
                        new byte[]{bytes[16], bytes[17]}));
            }
            debug("Performed change (FIRE_BULLET)");
        } else if (bytes[8] == Grid.KEEP_ALIVE) {
            player.keepAlive();
            debug("Performed change (KEEP_ALIVE)");
        } else if (bytes[8] == Grid.MOVE) {
            if (player.isAlive()) {
                boolean[] booleans = NetUtils.byteToBooleans(bytes[10]);
                player.setKeys(booleans[0], booleans[1], booleans[2], booleans[3]);
            }
            debug("Performed change (MOVE)");
        }
    }

    public synchronized void remove(int objectID) {
        objects.remove(objectID);
        debug("Removed object (ID: " + objectID + ")");
    }

    public synchronized boolean set(byte[] bytes) {
        try {
            if (!serverGrid) {
                lastUpdate = System.currentTimeMillis();
                if (updateCheckerThread == null) {
                    updateCheckerThread = new UpdateCheckerThread();
                    updateCheckerThread.start();
                }
            }
            for (int i = 0; i < 8; i++) {
                if (bytes[i] != serverID[i]) {
                    debug("Not setting grid because of invalid server ID");
                    return false;
                }
            }
            objects.clear();
            int size = bytes[8] + 128;
            gameOver = NetUtils.byteToBooleans(bytes[9])[0];
            secondsLeft = NetUtils.bytesToShort(bytes, 10);
            int srcPos = 12;
            for (int i = 0; i < size; i++) {
                GridObject object;
                if (bytes[srcPos] == GridObject.TYPE_BASE) {
                    object = new Base(this, bytes, ++srcPos);
                } else if (bytes[srcPos] == GridObject.TYPE_BULLET) {
                    object = new Bullet(this, bytes, ++srcPos);
                } else if (bytes[srcPos] == GridObject.TYPE_HEALTH_PACK) {
                    object = new HealthPack(this, bytes, ++srcPos);
                } else if (bytes[srcPos] == GridObject.TYPE_PLAYER) {
                    object = new Player(this, bytes, ++srcPos);
                } else if (bytes[srcPos] == GridObject.TYPE_TEAM) {
                    object = new Team(this, bytes, ++srcPos);
                } else {
                    debug("Not setting grid because of invalid byte array");
                    throw new IllegalArgumentException("Invalid byte array");
                }
                srcPos += object.getInputSize();
                object.putInGrid();
            }
            debug("Set grid successfully");
            return true;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            aioobe.printStackTrace();
        }
        debug("Not setting grid because of ArrayIndexOutOfBoundsException");
        return false;
    }

    void setGameOver(boolean over) {
        gameOver = over;
        debug("Set game over (" + over + ")");
    }

    public void setMap(Map m) {
        map = m;
        debug("Set map (" + m + ")");
    }

    public void setOptions(ServerOptions options) {
        serverOptions = options;
        debug("Set options (" + options + ")");
    }

    public void setUsername(byte playerID, String username) {
        usernames.put(playerID, username);
        debug("Set username (Player ID: " + playerID + ", Username: " + username + ")");
    }

    public void start() {
        if (serverGrid) {
            try {
                map = new Map(serverOptions.getMap());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            add(new Team(this, Team.TEAM_GREEN));
            add(new Team(this, Team.TEAM_RED));
            for (Base base : map.getBases()) {
                add(base);
            }
            for (Player player : getPlayers()) {
                player.spawn();
            }
            gridUpdaterThread = new GridUpdaterThread();
            gridUpdaterThread.start();
            gameStarted = true;
        }
        debug("Started grid");
    }

    public synchronized byte[] toByteArray() {
        int byteCount = 0;
        for (GridObject object : objects.values()) {
            byteCount += object.getOutputSize();
        }
        byte[] bytes = new byte[byteCount + 12];
        System.arraycopy(serverID, 0, bytes, 0, 8);
        bytes[8] = (byte) (objects.size() - 128);
        NetUtils.booleansToBytes(new Boolean[]{gameOver}, bytes, 9);
        NetUtils.shortToBytes(secondsLeft, bytes, 10);
        int destPos = 12;
        for (GridObject object : objects.values()) {
            object.toByteArray(bytes, destPos);
            destPos += object.getOutputSize();
        }
        return bytes;
    }

    private class GridUpdaterThread extends Thread {

        private final static int BASE_UPDATE_FREQUENCY = 20;
        private final static int BULLET_UPDATE_FREQUENCY = 1;
        private final static int HEALTH_PACK_FREQUENCY = 6000;
        private final static int MAX_HEALTH_PACKS = 2;
        private final static int PLAYER_ALIVENESS_UPDATE_FREQUENCY = 200;
        private final static int PLAYER_HEALTH_UPDATE_FREQUENCY = 20;
        private final static int PLAYER_POSITION_UPDATE_FREQUENCY = 2;
        private final static int SCORE_UPDATE_FREQUENCY = 1000;
        private final static int SECONDS_LEFT_SET_FREQUENCY = 50;
        private final static int DELAY = 5;
        private boolean run = true;
        private long startTime = System.currentTimeMillis();
        private int updateNum = 0;

        public GridUpdaterThread() {
            super("Grid Updater Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
        }

        @Override
        public void run() {
            while (run && !gameOver) {
                if (updateNum % BASE_UPDATE_FREQUENCY == 0) {
                    updateBases();
                }
                if (updateNum % BULLET_UPDATE_FREQUENCY == 0) {
                    updateBullets();
                }
                if (updateNum % HEALTH_PACK_FREQUENCY == 0) {
                    if (HealthPack.getCount() < MAX_HEALTH_PACKS) {
                        add(new HealthPack(Grid.this));
                    }
                }
                if (updateNum % PLAYER_ALIVENESS_UPDATE_FREQUENCY == 0) {
                    updatePlayerAliveness();
                }
                if (updateNum % PLAYER_HEALTH_UPDATE_FREQUENCY == 0) {
                    updatePlayerHealth();
                }
                if (updateNum % PLAYER_POSITION_UPDATE_FREQUENCY == 0) {
                    updatePlayerPositions();
                }
                if (updateNum % SCORE_UPDATE_FREQUENCY == 0) {
                    updateScores();
                }
                if (updateNum % SECONDS_LEFT_SET_FREQUENCY == 0) {
                    setSecondsLeft();
                }
                try {
                    sleep(DELAY);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                updateNum++;
            }
        }

        public void setSecondsLeft() {
            if (serverOptions.getTimeLimit() == -1) {
                secondsLeft = -1;
            } else {
                short seconds = (short) (serverOptions.getTimeLimit() * 60
                        - (System.currentTimeMillis() - startTime) / 1000);
                if (seconds < 0) {
                    seconds = 0;
                }
                secondsLeft = seconds;
            }
            if (secondsLeft == 0) {
                gameOver = true;
            }
        }

        private void updateBases() {
            ArrayList<Base> bases = getBases();
            ArrayList<Player> players = getPlayers();
            for (Base base : bases) {
                int teamGreens = 0;
                int teamReds = 0;
                for (Player player : players) {
                    Point endPoint = player.getPosition();
                    endPoint.translate(player.getSprite().getWidth(),
                            player.getSprite().getHeight());
                    if (player.isAlive()) {
                        if (map.isInTile(base.getBaseType(), player.getPosition(), endPoint)) {
                            if (player.getSprite().getTeamID() == Team.TEAM_GREEN) {
                                teamGreens++;
                            } else if (player.getSprite().getTeamID() == Team.TEAM_RED) {
                                teamReds++;
                            }
                        }
                    }
                }
                if (teamGreens > teamReds) {
                    base.updateProgress(Team.TEAM_GREEN, teamGreens - teamReds);
                } else if (teamReds > teamGreens) {
                    base.updateProgress(Team.TEAM_RED, teamReds - teamGreens);
                }
            }
        }

        private void updateBullets() {
            for (Bullet bullet : getBullets()) {
                bullet.move();
            }
        }

        private void updatePlayerAliveness() {
            for (Player player : getPlayers()) {
                player.checkAlive();
            }
        }

        private void updatePlayerHealth() {
            for (Player player : getPlayers()) {
                player.incrementHealth(serverOptions.getHealthRegenerationIncrement());
            }
        }

        private void updatePlayerPositions() {
            for (Player player : getPlayers()) {
                player.move();
            }
        }

        private void updateScores() {
            int redTeamPoints = 0;
            int greenTeamPoints = 0;
            for (Base base : getBases()) {
                if (base.getHolder() == Team.TEAM_RED) {
                    redTeamPoints++;
                } else if (base.getHolder() == Team.TEAM_GREEN) {
                    greenTeamPoints++;
                }
            }

            if (greenTeamPoints != 0) {
                Team team = getTeam(Team.TEAM_GREEN);
                team.incrementScore(greenTeamPoints * Team.INCREMENT_BASE);
            }

            if (redTeamPoints != 0) {
                Team team = getTeam(Team.TEAM_RED);
                team.incrementScore(redTeamPoints * Team.INCREMENT_BASE);
            }
        }
    }

    private class UpdateCheckerThread extends Thread {

        private final static int DELAY = 1000;
        private final static int TIME_OUT = 5000;
        private boolean run = true;

        public UpdateCheckerThread() {
            super("Update Checker Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
            interrupt();
        }

        @Override
        public void run() {
            while (run && !gameOver) {
                if (System.currentTimeMillis() - lastUpdate >= TIME_OUT) {
                    gameOver = true;
                }
                try {
                    sleep(DELAY);
                } catch (InterruptedException ie) {
                    //ignore
                }
            }
        }
    }
}
