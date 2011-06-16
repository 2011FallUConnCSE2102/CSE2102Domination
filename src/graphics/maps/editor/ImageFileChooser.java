package graphics.maps.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class ImageFileChooser extends JFileChooser {

   private MapEditor parent;

   ImageFileChooser(MapEditor parent) {
      this.parent = parent;
      addChoosableFileFilter(new ImageFilter());
      setAcceptAllFileFilterUsed(false);
      setAccessory(new ImagePreview(this));
      setFileView(new ImageFileView());
      setMultiSelectionEnabled(true);
   }

   BufferedImage[] openImageFiles() {
      if (showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
         File[] files = getSelectedFiles();
         BufferedImage[] images = new BufferedImage[files.length];
         for (int i = 0; i < images.length; i++) {
            try {
               images[i] = ImageIO.read(files[i]);
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
         }
         return images;
      }
      return null;
   }

   private class ImageFileView extends FileView {

      @Override
      public String getTypeDescription(File f) {
         String extension = ImageFilter.getExtension(f);
         String type = null;

         if (extension != null) {
            if (extension.equals("jpeg") || extension.equals("jpg")) {
               type = "JPEG Image";
            } else if (extension.equals("gif")) {
               type = "GIF Image";
            } else if (extension.equals("tiff") || extension.equals("tif")) {
               type = "TIFF Image";
            } else if (extension.equals("png")) {
               type = "PNG Image";
            }
         }
         return type;
      }
   }

   private class ImagePreview extends JComponent implements PropertyChangeListener {

      private File file = null;
      private int prefWidth = 75;
      private int prefHeight = 75;
      private ImageIcon thumb = null;

      ImagePreview(JFileChooser fc) {
         setPreferredSize(new Dimension(prefWidth + 50, prefHeight + 50));
         fc.addPropertyChangeListener(this);
      }

      private void loadImage() {
         if (file == null) {
            thumb = null;
            return;
         }

         ImageIcon tmpIcon = new ImageIcon(file.getPath());
         if (tmpIcon != null) {
            thumb = new ImageIcon(
                    tmpIcon.getImage().getScaledInstance(
                    prefWidth, prefHeight, Image.SCALE_DEFAULT));
         }
      }

      public void propertyChange(PropertyChangeEvent e) {
         boolean update = false;
         String prop = e.getPropertyName();

         if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

         } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
         }

         if (update) {
            thumb = null;
            if (isShowing()) {
               loadImage();
               repaint();
            }
         }
      }

      @Override
      protected void paintComponent(Graphics g) {
         if (thumb == null) {
            loadImage();
         }
         if (thumb != null) {
            int x = getWidth() / 2 - thumb.getIconWidth() / 2;
            int y = getHeight() / 2 - thumb.getIconHeight() / 2;

            if (y < 0) {
               y = 0;
            }

            if (x < 5) {
               x = 5;
            }
            thumb.paintIcon(this, g, x, y);
         }
      }
   }
}

class ImageFilter extends FileFilter {

   private ArrayList<String> acceptableExtensions = new ArrayList<String>();

   ImageFilter() {
      String[] exts = {"jpg", "png", "gif", "jpeg", "tiff", "tif"};
      for (int i = 0; i < exts.length; i++) {
         acceptableExtensions.add(exts[i]);
      }
   }

   public boolean accept(File pathname) {

      if (pathname.isDirectory()) {
         return true;
      } else if (getExtension(pathname) == null) {
         return false;
      } else if (acceptableExtensions.contains(getExtension(pathname))) {
         return true;
      } else {
         return false;
      }
   }

   static String getExtension(File file) {
      String filePath = file.getAbsolutePath();
      if (filePath.lastIndexOf(".") < 0) {
         return null;
      } else {
         String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
         return ext;
      }
   }

   public String getDescription() {
      return "Image Files";
   }
}
