package graphics.maps.editor;

import graphics.maps.Tile;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class LayersPane extends JTabbedPane {

    private final ImageListPane imagePane;
    private ArrayList<MapGridPane> layers = new ArrayList<MapGridPane>();

    LayersPane(ImageListPane imagePane) {
        this.imagePane = imagePane;
        addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt) {
                for (MapGridPane layer : layers) {
                    layer.setSelected(false);
                }
            }
        });
    }

    void addLayer(short rows, short columns, short tileHeight, short tileWidth, byte index) {
        layers.add(index, new MapGridPane(imagePane, rows, columns, tileHeight, tileWidth));
        insertTab(null, null, layers.get(index), "", index);
        setTabComponentAt(index, new ButtonTabComponent());
    }

    void clear() {
        layers.clear();
        removeAll();
    }

    byte getLayerCount() {
        return (byte) layers.size();
    }

    byte[] getMapData(short[] imageIDs) {
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        bytes.add(getLayerCount());
        for (MapGridPane layer : layers) {
            bytes.addAll(layer.getMapData(imageIDs));
        }
        byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArr[i] = bytes.get(i);
        }
        return byteArr;
    }

    void removeImage(int id) {
        for (MapGridPane layer : layers) {
            layer.removeImage(id);
        }
    }

    void selectAll() {
        int index = getSelectedIndex();
        if (index != -1) {
            layers.get(index).setSelected(true);
        }
    }

    void setTiles(int layerIndex, Tile[][] tiles) {
        layers.get(layerIndex).setTiles(tiles);
    }

    private class ButtonTabComponent extends JPanel {

        private ButtonTabComponent() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);

            JLabel label = new JLabel() {

                @Override
                public String getText() {
                    int i = LayersPane.this.indexOfTabComponent(ButtonTabComponent.this);
                    if (i != -1) {
                        return "Layer " + (i + 1);
                    }
                    return null;
                }
            };
            add(label);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

            JButton button = new TabButton();
            add(button);
            setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        }

        private class TabButton extends JButton implements ActionListener {

            public TabButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("Remove Layer");

                setUI(new BasicButtonUI());

                setContentAreaFilled(false);
                setBorder(BorderFactory.createEtchedBorder());

                setBorderPainted(false);

                addActionListener(this);
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        Component component = e.getComponent();
                        if (component instanceof AbstractButton) {
                            AbstractButton button = (AbstractButton) component;
                            button.setBorderPainted(true);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        Component component = e.getComponent();
                        if (component instanceof AbstractButton) {
                            AbstractButton button = (AbstractButton) component;
                            button.setBorderPainted(false);
                        }
                    }
                });

                setRolloverEnabled(true);
            }

            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(LayersPane.this,
                        "Are you sure you want to remove this layer?") == JOptionPane.YES_OPTION) {
                    int i = LayersPane.this.indexOfTabComponent(ButtonTabComponent.this);
                    if (i != -1) {
                        layers.remove(i);
                        LayersPane.this.remove(i);
                    }
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.MAGENTA);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }

            @Override
            public void updateUI() {
            }
        }
    }
}
