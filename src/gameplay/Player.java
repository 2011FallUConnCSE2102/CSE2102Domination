package gameplay;

import graphics.Sprite;
import graphics.maps.SpawnPoint;
import java.awt.Point;
import java.util.ArrayList;
import networking.NetUtils;

/**
 *
 * @author Scott
 */
public class Player extends DisplayableGridObject {

    private static final int BYTE_COUNT = 7;
    static final byte FULL_HEALTH = 100;
    private static final int HEALTH_REGENERATION_DELAY = 2500;
    private static final byte KILL_SCORE_INCREMENT = 5;
    private static final byte MOVE_PIXELS = 2;
    static final byte NO_HEALTH = 0;
    private static final int TIME_OUT = 5000;
    private byte alive;
    private byte deathCount = Byte.MIN_VALUE;
    private boolean down = false;
    private byte health = FULL_HEALTH;
    private boolean left = false;
    private byte killCount = Byte.MIN_VALUE;
    private long lastAlive = -1;
    private long lastHit;
    private byte playerID;
    private boolean right = false;
    private final byte[] score = NetUtils.shortToBytes((short) 0);
    private boolean up = false;
    private final String username;

    public Player(Grid grid, byte playerID) {
        super(grid);
        this.playerID = playerID;
        username = grid.getUsername(getPlayerID());
    }

    public Player(Grid grid, byte[] bytes, int startPosition) {
        super(grid);
        set(bytes, startPosition);
        username = grid.getUsername(getPlayerID());
    }

    boolean canMove(Point p) {
        Sprite sprite = getSprite();

        Point point = new Point(p);

        point.translate(-(PlayerRunner.SCREEN_WIDTH - sprite.getWidth()) / 2,
                -(PlayerRunner.SCREEN_HEIGHT - sprite.getHeight()) / 2);
        if (!getGrid().getMap().canMoveTo(sprite, point)) {
            return false;
        }
        ArrayList<Player> players = getGrid().getPlayers();
        for (Player player : players) {
            if (player != this && player.isAlive()) {
                if ((p.x < player.getPositionX() + player.getSprite().getWidth()
                        && p.x + sprite.getWidth() > player.getPositionX()
                        && p.y < player.getPositionY() + player.getSprite().getHeight()
                        && p.y + sprite.getHeight() > player.getPositionY())) {
                    return false;
                }
            }
        }
        return getHealthPackInPath((short) p.x, (short) p.y) == null;
    }

    public void checkAlive() {
        if (lastAlive != -1 && System.currentTimeMillis() - lastAlive >= TIME_OUT) {
            removeFromGrid();
        }
    }

    public int getDeathCount() {
        return deathCount + 128;
    }

    private HealthPack getHealthPackInPath(short positionX, short positionY) {
        ArrayList<DisplayableGridObject> objects = getGrid().getDisplayableGridObjects();
        for (DisplayableGridObject object : objects) {
            if (object instanceof HealthPack) {
                if ((positionX < object.getPositionX() + object.getSprite().getWidth()
                        && positionX + getSprite().getWidth() > object.getPositionX()
                        && positionY < object.getPositionY() + object.getSprite().getHeight()
                        && positionY + getSprite().getHeight() > object.getPositionY())) {
                    return (HealthPack) object;
                }
            }
        }
        return null;
    }

    public byte getHealth() {
        return health;
    }

    @Override
    int getInputSize() {
        return super.getInputSize() + BYTE_COUNT;
    }

    public int getKillCount() {
        return killCount + 128;
    }

    @Override
    int getOutputSize() {
        return super.getOutputSize() + BYTE_COUNT;
    }

    public byte getPlayerID() {
        return playerID;
    }

    public short getScore() {
        return NetUtils.bytesToShort(score);
    }

    public String getUsername() {
        return username;
    }

    void incrementHealth(int increment) {
        if (health < FULL_HEALTH && System.currentTimeMillis() - lastHit >= HEALTH_REGENERATION_DELAY) {
            health += increment;
            if (health > FULL_HEALTH) {
                health = FULL_HEALTH;
            }
        }
    }

    public void incrementKillCount() {
        killCount++;
        incrementScore(KILL_SCORE_INCREMENT);
        getGrid().getTeam(getSprite().getTeamID()).incrementScore(Team.INCREMENT_KILL);
    }

    public void incrementScore(byte increment) {
        NetUtils.shortToBytes((short) (NetUtils.bytesToShort(score) + increment), score, 0);
    }

    public boolean isAlive() {
        return NetUtils.byteToBooleans(alive)[0];
    }

    public void keepAlive() {
        lastAlive = System.currentTimeMillis();
    }

    void move() {
        if (isAlive()) {
            Point location = getPosition();
            if (down) {
                location.y += MOVE_PIXELS;
            }
            if (left) {
                location.x -= MOVE_PIXELS;
            }
            if (right) {
                location.x += MOVE_PIXELS;
            }
            if (up) {
                location.y -= MOVE_PIXELS;
            }
            if (!location.equals(getPosition())) {
                move((short) location.x, (short) location.y);
            }
        }
    }

    public void move(short positionX, short positionY) {
        HealthPack healthPack = getHealthPackInPath(positionX, positionY);
        if (healthPack != null) {
            healthPack.rewardPlayer(this);
        }
        if (canMove(new Point(positionX, getPositionY()))) {
            setPositionX(positionX);
        }
        if (canMove(new Point(getPositionX(), positionY))) {
            setPositionY(positionY);
        }
    }

    void setKeys(boolean down, boolean left, boolean right, boolean up) {
        this.down = down;
        this.left = left;
        this.right = right;
        this.up = up;
    }

    boolean shoot(int healthDecrement) {
        lastHit = System.currentTimeMillis();
        health -= healthDecrement;
        if (health <= NO_HEALTH) {
            deathCount++;
            setAlive(false);
            Thread respawnThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        Thread.sleep(getGrid().getOptions().getRespawnDelay() * 1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    health = FULL_HEALTH;
                    spawn();
                }
            });
            respawnThread.setDaemon(true);
            respawnThread.setName("Respawn Thread for " + username);
            respawnThread.start();
            return true;
        }
        return false;
    }

    @Override
    void set(byte[] bytes, int startPosition) {
        super.set(bytes, startPosition);
        startPosition += super.getInputSize();
        alive = bytes[startPosition++];
        deathCount = bytes[startPosition++];
        health = bytes[startPosition++];
        killCount = bytes[startPosition++];
        playerID = bytes[startPosition++];
        System.arraycopy(bytes, startPosition, score, 0, score.length);
    }

    private void setAlive(boolean isAlive) {
        alive = NetUtils.booleansToBytes(new Boolean[]{isAlive})[0];
    }

    void setHealth(byte health) {
        this.health = health;
    }

    public void spawn() {
        ArrayList<SpawnPoint> spawnPoints = getGrid().getMap().getSpawnPoints();

        ArrayList<SpawnPoint> preferredSpawnPoints = getGrid().getMap().getSpawnPoints();

        for (Base base : getGrid().getBases()) {
            if (base.getHolder() == getSprite().getTeamID()) {
                for (SpawnPoint point : spawnPoints) {
                    if ((base.getBaseType() == Base.BASE_ONE && point.getType() == SpawnPoint.SPAWN_ONE)
                            || (base.getBaseType() == Base.BASE_TWO && point.getType() == SpawnPoint.SPAWN_TWO)
                            || (base.getBaseType() == Base.BASE_THREE && point.getType() == SpawnPoint.SPAWN_THREE)) {
                        preferredSpawnPoints.add(point);
                    }
                }
            }
        }

        while (!preferredSpawnPoints.isEmpty()) {
            SpawnPoint point = preferredSpawnPoints.remove(
                    (int) (Math.random() * preferredSpawnPoints.size()));
            if (canMove(point.getPoint())) {
                setPosition(point.getPoint());
                setAlive(true);
                return;
            }
        }

        ArrayList<SpawnPoint> otherSpawnPoints = new ArrayList<SpawnPoint>();
        for (SpawnPoint point : spawnPoints) {
            if (point.getType() == SpawnPoint.SPAWN_OTHER) {
                otherSpawnPoints.add(point);
            }
        }

        while (!otherSpawnPoints.isEmpty()) {
            SpawnPoint point = otherSpawnPoints.remove(
                    (int) (Math.random() * otherSpawnPoints.size()));
            if (canMove(point.getPoint())) {
                setPosition(point.getPoint());
                setAlive(true);
                return;
            }
        }

        SpawnPoint spawnPoint = null;
        while (spawnPoint == null || !canMove(spawnPoint.getPoint())) {
            spawnPoint = spawnPoints.remove((int) (Math.random() * spawnPoints.size()));
        }
        setPosition(spawnPoint.getPoint());
        setAlive(true);
    }

    @Override
    void toByteArray(byte[] bytes, int offset) {
        super.toByteArray(bytes, offset);
        offset += super.getOutputSize();
        bytes[offset++] = alive;
        bytes[offset++] = deathCount;
        bytes[offset++] = health;
        bytes[offset++] = killCount;
        bytes[offset++] = playerID;
        System.arraycopy(score, 0, bytes, offset, score.length);
    }
}
