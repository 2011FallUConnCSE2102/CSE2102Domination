package graphics.maps.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class ImageListPane extends JScrollPane implements ActionListener {

   static final int SCALED_SIZE = 150;
   private short imageID = Short.MIN_VALUE + 1;
   private JList list = new JList();
   private DefaultListModel model = new DefaultListModel();
   private MapEditor parent;
   private JPopupMenu popupMenu;

   ImageListPane(MapEditor parent) {
      this.parent = parent;
      initComponents();
      setPreferredSize(new Dimension(171, 0));
      setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
   }

   public void actionPerformed(ActionEvent evt) {
      ImageInfo info = (ImageInfo) model.remove(list.getSelectedIndex());
      parent.removeImage(info.getID());
   }

   void addImage(BufferedImage image) {
      model.addElement(new ImageInfo(imageID++, image));
   }

   void clearImages() {
      imageID = Short.MIN_VALUE + 1;
      model.clear();
   }

   ImageInfo[] getImages() {
      ArrayList<ImageInfo> imageList = new ArrayList<ImageInfo>();
      for (int i = 0; i < model.size(); i++) {
         ImageInfo image = (ImageInfo) model.get(i);
         imageList.add(image);
      }
      ImageInfo[] images = new ImageInfo[imageList.size()];
      imageList.toArray(images);
      return images;
   }

   Image getScaledImage(int imageID, int width, int height) {
      for (int i = 0; i < model.size(); i++) {
         ImageInfo image = (ImageInfo) model.get(i);
         if (image.getID() == imageID) {
            return image.getScaledImage(new Dimension(width, height));
         }
      }
      return null;
   }

   private void initComponents() {
      list.addMouseListener(new ImageListPaneMouseListener());
      list.setCellRenderer(new ImageListCellRenderer());
      list.setDragEnabled(true);
      list.setModel(model);
      list.setTransferHandler(new ImageListTransferHandler());

      popupMenu = new JPopupMenu();
      JMenuItem menuItem = new JMenuItem("Remove");
      menuItem.addActionListener(this);
      popupMenu.add(menuItem);

      this.setViewportView(list);
   }

   private class ImageListCellRenderer extends DefaultListCellRenderer {

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
              int index, boolean isSelected, boolean cellHasFocus) {
         JLabel component = new JLabel(new ImageIcon(
                 getScaledImage(((ImageInfo) value).getID(), SCALED_SIZE,
                 SCALED_SIZE)));
         component.setBorder(BorderFactory.createLineBorder(Color.BLACK));
         return component;
      }
   }

   private class ImageListPaneMouseListener extends MouseAdapter {

      @Override
      public void mouseReleased(MouseEvent evt) {
         if (SwingUtilities.isRightMouseButton(evt)) {
            int index = list.locationToIndex(evt.getPoint());
            if (index != -1) {
               list.setSelectedIndex(index);
               popupMenu.show(list, evt.getX(), evt.getY());
            }
         }
      }
   }

   private class ImageListTransferHandler extends TransferHandler {

      @Override
      public Transferable createTransferable(JComponent c) {
         return new ImageListTransferable(c);
      }

      @Override
      public int getSourceActions(JComponent c) {
         return COPY_OR_MOVE;
      }
   }

   private class ImageListTransferable implements Transferable {

      private JComponent component;

      private ImageListTransferable(JComponent c) {
         component = c;
      }

      public Object getTransferData(DataFlavor flavor)
              throws UnsupportedFlavorException {
         if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
         }
         JList list = (JList) component;
         return ((ImageInfo) list.getModel().getElementAt(list.getSelectedIndex())).getID();
      }

      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[]{MapPanel.DATA_FLAVOR};
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         return flavor.equals(MapPanel.DATA_FLAVOR);
      }
   }
}
