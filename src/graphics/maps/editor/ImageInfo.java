package graphics.maps.editor;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 *
 * @author Scott
 */
class ImageInfo {

   private final short id;
   private final BufferedImage image;
   private final Hashtable<Dimension, Image> scaledImages = new Hashtable<Dimension, Image>();

   ImageInfo(short id, BufferedImage image) {
      this.id = id;
      this.image = image;
   }

   short getID() {
      return id;
   }

   BufferedImage getImage() {
      return image;
   }

   Image getScaledImage(Dimension d) {
      if (scaledImages.containsKey(d)) {
         return scaledImages.get(d);
      } else {
         Image scaled = image.getScaledInstance(d.width, d.height, Image.SCALE_DEFAULT);
         scaledImages.put(d, scaled);
         return scaled;
      }
   }
}
