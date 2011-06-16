package gameplay;

import graphics.Sprite;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Scott
 */
abstract class DropItem extends DisplayableGridObject {

    DropItem(Grid grid, byte[] bytes, int startPosition) {
        super(grid);
        set(bytes, startPosition);
    }

    DropItem(Grid grid, Sprite sprite) {
        super(grid, sprite);
        Point p = null;
        while (p == null || !canMove(p)) {
            p = getGrid().getMap().getRandomPoint(getSprite());
            p.translate((PlayerRunner.SCREEN_WIDTH - getSprite().getWidth()) / 2,
                    (PlayerRunner.SCREEN_HEIGHT - getSprite().getHeight()) / 2);
        }
        setPosition(p);
    }

    private boolean canMove(Point p) {
        ArrayList<DisplayableGridObject> objects = getGrid().getDisplayableGridObjects();
        for (DisplayableGridObject object : objects) {
            if (!(object instanceof Bullet) && !(object instanceof Player && !((Player) object).isAlive())) {
                if ((p.x < object.getPositionX() + object.getSprite().getWidth()
                        && p.x + getSprite().getWidth() > object.getPositionX()
                        && p.y < object.getPositionY() + object.getSprite().getHeight()
                        && p.y + getSprite().getHeight() > object.getPositionY())) {
                    return false;
                }
            }
        }
        return true;
    }

    abstract void rewardPlayer(Player player);
}
