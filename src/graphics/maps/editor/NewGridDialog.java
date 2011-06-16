package graphics.maps.editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class NewGridDialog extends JDialog implements ActionListener {

   private JComboBox cmbLayer;
   private byte layerCount;
   private MapEditor parent;
   private JTextField txtColumns;
   private JTextField txtRows;
   private JTextField txtTileHeight;
   private JTextField txtTileWidth;

   NewGridDialog(MapEditor parent, byte layerCount) {
      super(parent);
      this.layerCount = layerCount;
      this.parent = parent;
      initComponents();
      setSize(150, 150);
      setLocationRelativeTo(null);
      setTitle("New Grid");
   }

   public void actionPerformed(ActionEvent evt) {
      try {
         short rows = Short.parseShort(txtRows.getText());
         short columns = Short.parseShort(txtColumns.getText());
         short tileHeight = Short.parseShort(txtTileHeight.getText());
         short tileWidth = Short.parseShort(txtTileWidth.getText());
         byte layer = (byte)cmbLayer.getSelectedIndex();
         if (rows > 0 && columns > 0 && tileHeight > 0 && tileWidth > 0) {
            parent.createGrid(rows, columns, tileHeight, tileWidth, layer);
            dispose();
            return;
         }
      } catch (NumberFormatException nfe) {
      }
      JOptionPane.showMessageDialog(this, "Invalid input.");
   }

   private void initComponents() {
      setLayout(new GridLayout(0, 1));

      JPanel panel = new JPanel(new GridLayout(0, 2));
      panel.add(new JLabel("Rows: "));
      txtRows = new JTextField();
      panel.add(txtRows);
      add(panel);

      panel = new JPanel(new GridLayout(0, 2));
      panel.add(new JLabel("Columns: "));
      txtColumns = new JTextField();
      panel.add(txtColumns);
      add(panel);

      panel = new JPanel(new GridLayout(0, 2));
      panel.add(new JLabel("Tile Height: "));
      txtTileHeight = new JTextField();
      panel.add(txtTileHeight);
      add(panel);

      panel = new JPanel(new GridLayout(0, 2));
      panel.add(new JLabel("Tile Width: "));
      txtTileWidth = new JTextField();
      panel.add(txtTileWidth);
      add(panel);

      panel = new JPanel(new GridLayout(0, 2));
      panel.add(new JLabel("Layer"));
      String[] choices = new String[layerCount + 1];
      for (int i = 0; i < choices.length; i++) {
         choices[i] = String.valueOf(i + 1);
      }
      cmbLayer = new JComboBox(choices);
      panel.add(cmbLayer);
      add(panel);

      JButton btnCreate = new JButton("Create");
      btnCreate.addActionListener(this);
      add(btnCreate);

      getRootPane().setDefaultButton(btnCreate);
   }
}
