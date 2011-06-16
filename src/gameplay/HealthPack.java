package gameplay;

import graphics.Sprite;

/**
 *
 * @author Scott
 */
public class HealthPack extends DropItem {

    private static int count;

    HealthPack(Grid grid, byte[] bytes, int startPosition) {
        super(grid, bytes, startPosition);
    }

    HealthPack(Grid grid) {
        super(grid, Sprite.HEALTH_KIT);
        count++;
    }

    static int getCount() {
        return count;
    }

    void rewardPlayer(Player player) {
        player.setHealth(Player.FULL_HEALTH);
        removeFromGrid();
        count--;
    }
}
