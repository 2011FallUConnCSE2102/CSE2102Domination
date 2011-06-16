package graphics.maps;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class Tile {

   public static final short NO_IMAGE = Short.MIN_VALUE;
   private final TileAttributes attributes;
   private short imageID = NO_IMAGE;

   public Tile() {
      attributes = new TileAttributes();
   }

   public Tile(TileAttributes attributes, short imageID) {
      this.attributes = attributes;
      this.imageID = imageID;
   }

   public TileAttributes getAttributes() {
      return attributes;
   }

   public short getImageID() {
      return imageID;
   }

   public void setImageID(short imageID) {
      attributes.setRotationAngle((short)0);
      this.imageID = imageID;
   }
}
