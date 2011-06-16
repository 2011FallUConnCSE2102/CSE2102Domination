package gameplay;

import graphics.Sprite;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Scott
 */
class Bullet extends DisplayableGridObject {

    static final int HEALTH_DECREMENT = 5;
    private static final int MOVE_DISTANCE = 2;
    private static final int TOTAL_DISTANCE = 500;
    private int distance = 0;
    private Player player;
    private Point startPos;
    private byte teamID;
    private double xInc;
    private double yInc;

    Bullet(Grid grid, byte[] bytes, int startPosition) {
        super(grid);
        set(bytes, startPosition);
    }

    Bullet(Grid grid, Player player, byte[] dir, byte[] posX, byte[] posY) {
        super(grid, Sprite.BULLET, dir, posX, posY);
        this.player = player;
        teamID = player.getSprite().getTeamID();
        startPos = new Point((short) Math.round(getPositionX() + xInc * distance),
                (short) Math.round(getPositionY() + yInc * distance));
        setPosition(startPos);
        double adjustedDir = getDirection() - Math.PI / 2;
        xInc = MOVE_DISTANCE * Math.cos(adjustedDir);
        yInc = MOVE_DISTANCE * Math.sin(adjustedDir);
    }

    private boolean canMove(short positionX, short positionY) {
        if (distance > TOTAL_DISTANCE) {
            return false;
        }
        Point point = new Point(positionX, positionY);
        point.translate(-(PlayerRunner.SCREEN_WIDTH - getSprite().getWidth()) / 2,
                -(PlayerRunner.SCREEN_HEIGHT - getSprite().getHeight()) / 2);
        return getGrid().getMap().canMoveTo(getSprite(), point);
    }

    private Player getPlayerInPath() {
        ArrayList<Player> players = getGrid().getPlayers();
        for (Player p : players) {
            if (p.isAlive() && p != player) {
                if ((getPositionX() < p.getPositionX() + p.getSprite().getWidth()
                        && getPositionX() + getSprite().getWidth() > p.getPositionX()
                        && getPositionY() < p.getPositionY() + p.getSprite().getHeight()
                        && getPositionY() + getSprite().getHeight() > p.getPositionY())) {
                    return p;
                }
            }
        }
        return null;
    }

    void move() {
        short posX = (short) Math.round(startPos.x + xInc * distance);
        short posY = (short) Math.round(startPos.y + yInc * distance);
        Player playerInPath = getPlayerInPath();
        if (canMove(posX, posY) && playerInPath == null) {
            setPosition(new Point(posX, posY));
            distance += MOVE_DISTANCE;
        } else {
            if (playerInPath != null && playerInPath.getSprite().getTeamID() != teamID) {
                if (playerInPath.shoot(HEALTH_DECREMENT + (int) (6 * Math.random()))) {
                    player.incrementKillCount();
                }
            }
            removeFromGrid();
        }
    }
}
