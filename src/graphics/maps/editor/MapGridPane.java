package graphics.maps.editor;

import graphics.maps.Tile;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import networking.NetUtils;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class MapGridPane extends JScrollPane {

   private final short cols;
   private final ImageListPane imagePane;
   private MapPanel[][] mapPanels;
   private final short rows;
   private final short tileHeight;
   private final short tileWidth;

   MapGridPane(ImageListPane imagePane, short r, short c,
           short tileHeight, short tileWidth) {
      this.imagePane = imagePane;
      cols = c;
      rows = r;
      this.tileHeight = tileHeight;
      this.tileWidth = tileWidth;
      initComponents();
      getHorizontalScrollBar().setUnitIncrement(10);
      getVerticalScrollBar().setUnitIncrement(10);
      setAutoscrolls(true);
   }

   ArrayList<Byte> getMapData(short[] imageIDs) {
      ArrayList<Byte> mapData = new ArrayList<Byte>();
      byte[] temp = NetUtils.shortToBytes(rows);
      mapData.add(temp[0]);
      mapData.add(temp[1]);
      temp = NetUtils.shortToBytes(cols);
      mapData.add(temp[0]);
      mapData.add(temp[1]);
      temp = NetUtils.shortToBytes(tileHeight);
      mapData.add(temp[0]);
      mapData.add(temp[1]);
      temp = NetUtils.shortToBytes(tileWidth);
      mapData.add(temp[0]);
      mapData.add(temp[1]);
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            Tile tile = mapPanels[i][j].getTile();
            mapData.addAll(tile.getAttributes().toBytes());
            short simplifiedID = Tile.NO_IMAGE;
            short id = tile.getImageID();
            for (short k = 0; k < imageIDs.length; k++) {
               if (imageIDs[k] == id) {
                  simplifiedID = (short) (Short.MIN_VALUE + k + 1);
                  break;
               }
            }
            temp = NetUtils.shortToBytes(simplifiedID);
            mapData.add(temp[0]);
            mapData.add(temp[1]);
         }
      }
      return mapData;
   }

   MapPanel[] getSelectedPanels() {
      ArrayList<MapPanel> selectedList = new ArrayList<MapPanel>();
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            if (mapPanels[i][j].isSelected()) {
               selectedList.add(mapPanels[i][j]);
            }
         }
      }
      MapPanel[] selectedPanels = new MapPanel[selectedList.size()];
      selectedList.toArray(selectedPanels);
      return selectedPanels;
   }

   private void initComponents() {
      JPanel panel = new JPanel(new GridLayout(rows, cols));
      mapPanels = new MapPanel[rows][cols];
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            mapPanels[i][j] = new MapPanel(imagePane, this);
            mapPanels[i][j].setPreferredSize(
                    new Dimension(tileHeight, tileWidth));
            panel.add(mapPanels[i][j]);
         }
      }
      setViewportView(panel);
   }

   void removeImage(int imageID) {
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            if (mapPanels[i][j].getTile().getImageID() == imageID) {
               mapPanels[i][j].getTile().setImageID(Tile.NO_IMAGE);
               mapPanels[i][j].repaint();
            }
         }
      }
   }

   void setTiles(Tile[][] tiles) {
      for (int i = 0; i < tiles.length; i++) {
         for (int j = 0; j < tiles[i].length; j++) {
            mapPanels[i][j].setTile(tiles[i][j]);
         }
      }
      revalidate();
   }

   void setSelected(boolean selected) {
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            if (mapPanels[i][j].isSelected() != selected) {
               mapPanels[i][j].setSelected(selected, false);
            }
         }
      }
   }
}
