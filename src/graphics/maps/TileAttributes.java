package graphics.maps;

import java.awt.Color;
import java.util.ArrayList;
import networking.NetUtils;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class TileAttributes {

   public static final byte BASE_NONE = -128;
   public static final byte BASE_ONE = -127;
   public static final byte BASE_TWO = -126;
   public static final byte BASE_THREE = -125;
   public static final byte NOT_SOLID = -124;
   public static final int SEMI_SOLID = -123;
   public static final int SOLID = -122;
   public static final byte SPAWN_NONE = -121;
   public static final byte SPAWN_ONE = -120;
   public static final byte SPAWN_TWO = -119;
   public static final byte SPAWN_THREE = -118;
   public static final byte SPAWN_OTHER = -117;
   private byte base = BASE_NONE;
   private byte spawnPoint = SPAWN_NONE;
   private short rotation;
   private byte solidity = NOT_SOLID;

   public boolean get(int attribute) {
      if (attribute == BASE_NONE || attribute == BASE_ONE
              || attribute == BASE_TWO || attribute == BASE_THREE) {
         return base == attribute;
      } else if (attribute == NOT_SOLID || attribute == SEMI_SOLID
              || attribute == SOLID) {
         return solidity == attribute;
      } else if (attribute == SPAWN_NONE || attribute == SPAWN_ONE
              || attribute == SPAWN_TWO || attribute == SPAWN_THREE
              || attribute == SPAWN_OTHER) {
         return spawnPoint == attribute;
      } else {
         throw new IllegalArgumentException("Invalid attribute");
      }
   }

   public Color getColorRepresentation() {
      int r = 0;
      int g = 0;
      int b = 0;
      if (base != BASE_NONE) {
         b = 255;
      }
      if (solidity == SEMI_SOLID) {
         r = 192;
      } else if (solidity == SOLID) {
         r = 255;
      }
      if (spawnPoint != SPAWN_NONE) {
         g = 255;
      }
      float[] hsb = Color.RGBtoHSB(r, g, b, null);
      return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
   }

   public short getRotationAngle() {
      return rotation;
   }

   public void set(byte attribute, boolean value) {
      if (attribute == BASE_NONE || attribute == BASE_ONE
              || attribute == BASE_TWO || attribute == BASE_THREE) {
         if (value) {
            base = attribute;
         }
      } else if (attribute == NOT_SOLID || attribute == SEMI_SOLID
              || attribute == SOLID) {
         solidity = attribute;
      } else if (attribute == SPAWN_NONE || attribute == SPAWN_ONE
              || attribute == SPAWN_TWO || attribute == SPAWN_THREE
              || attribute == SPAWN_OTHER) {
         spawnPoint = attribute;
      } else {
         throw new IllegalArgumentException("Invalid attribute");
      }
   }

   public void setRotationAngle(short rotationAngle) {
      rotation = (short) (rotationAngle % 360);
   }

   public ArrayList<Byte> toBytes() {
      ArrayList<Byte> bytes = new ArrayList<Byte>();
      bytes.add(base);
      bytes.add(solidity);
      bytes.add(spawnPoint);
      byte[] temp = NetUtils.shortToBytes(rotation);
      bytes.add(temp[0]);
      bytes.add(temp[1]);
      return bytes;
   }

   public static TileAttributes toTileAttributes(byte[] bytes) {
      TileAttributes attributes = new TileAttributes();
      if (bytes.length != 5) {
         throw new IllegalArgumentException("Invalid attribute string");
      }
      for (int i = 0; i < 3; i++) {
         attributes.set(bytes[i], true);
      }
      attributes.setRotationAngle(NetUtils.bytesToShort(new byte[]{bytes[3], bytes[4]}));
      return attributes;
   }
}
