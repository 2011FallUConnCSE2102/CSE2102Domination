package graphics.maps;

import java.awt.Point;

/**
 *
 * @author Scott
 */
public class SpawnPoint {

   public static final int SPAWN_ONE = TileAttributes.SPAWN_ONE;
   public static final int SPAWN_TWO = TileAttributes.SPAWN_TWO;
   public static final int SPAWN_THREE = TileAttributes.SPAWN_THREE;
   public static final int SPAWN_OTHER = TileAttributes.SPAWN_OTHER;
   private final Point point;
   private final int spawnType;

   SpawnPoint(Point p, int type) {
      point = p;
      spawnType = type;
   }

   public Point getPoint() {
      return point;
   }

   public int getType() {
      return spawnType;
   }
}
