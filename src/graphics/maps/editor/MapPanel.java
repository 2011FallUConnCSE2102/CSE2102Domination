package graphics.maps.editor;

import graphics.maps.Tile;
import graphics.maps.TileAttributes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
class MapPanel extends JPanel implements ActionListener {

    private static final String COMMAND_BASE = "base";
    private static final String COMMAND_REMOVE = "remove";
    private static final String COMMAND_ROTATE = "rotate";
    private static final String COMMAND_SEMI_SOLID = "semi-solid";
    private static final String COMMAND_SOLID = "solid";
    private static final String COMMAND_SPAWN = "spawn";
    private static final int BORDER_WIDTH = 2;
    static DataFlavor DATA_FLAVOR;
    private final ImageListPane imagePane;
    private final MapGridPane mapGridPane;
    private boolean selected = false;
    private Tile tile = new Tile();

    static {
        try {
            DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
                    + ";class=" + Integer.class.getName());
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public MapPanel(ImageListPane imagePane, MapGridPane gridPane) {
        this.imagePane = imagePane;
        mapGridPane = gridPane;
        MapPanelMouseListener mouseListener = new MapPanelMouseListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_WIDTH));
        setTransferHandler(new MapPanelTransferHandler());
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(COMMAND_BASE)) {
            tile.getAttributes().set(MapPanelPopupMenu.base, true);
        } else if (evt.getActionCommand().equals(COMMAND_REMOVE)) {
            tile.setImageID(Tile.NO_IMAGE);
            repaint();
        } else if (evt.getActionCommand().equals(COMMAND_ROTATE)) {
            tile.getAttributes().setRotationAngle(
                    (short) (MapPanelPopupMenu.rotationAngle
                    + tile.getAttributes().getRotationAngle()));
            repaint();
            return;
        } else if (evt.getActionCommand().equals(COMMAND_SEMI_SOLID)
                || evt.getActionCommand().equals(COMMAND_SOLID)) {
            tile.getAttributes().set(MapPanelPopupMenu.solidity, true);
        } else if (evt.getActionCommand().equals(COMMAND_SPAWN)) {
            tile.getAttributes().set(MapPanelPopupMenu.spawnPoint, true);
        }
        setBorder(BorderFactory.createLineBorder(
                tile.getAttributes().getColorRepresentation(), BORDER_WIDTH));
    }

    public Tile getTile() {
        return tile;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Insets insets = getInsets();
        if (tile.getImageID() != -1) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(tile.getAttributes().getRotationAngle()),
                    getWidth() / 2, getHeight() / 2);
            g2d.drawImage(imagePane.getScaledImage(tile.getImageID(), getWidth(),
                    getHeight()), insets.left, insets.top,
                    getWidth() - insets.left - insets.right,
                    getHeight() - insets.top - insets.bottom, this);
        }
        float fontSize = Math.min(getWidth(), getHeight()) / 6;
        if (!tile.getAttributes().get(TileAttributes.BASE_NONE)) {
            g.setColor(Color.BLUE);
            g.setFont(g.getFont().deriveFont(fontSize));
            if (tile.getAttributes().get(TileAttributes.BASE_ONE)) {
                g.drawString("1", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - fontSize / 4));
            } else if (tile.getAttributes().get(TileAttributes.BASE_TWO)) {
                g.drawString("2", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - fontSize / 4));
            } else if (tile.getAttributes().get(TileAttributes.BASE_THREE)) {
                g.drawString("3", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - fontSize / 4));
            }
        }
        if (!tile.getAttributes().get(TileAttributes.SPAWN_NONE)) {
            g.setColor(Color.GREEN);
            g.setFont(g.getFont().deriveFont(fontSize));
            if (tile.getAttributes().get(TileAttributes.SPAWN_ONE)) {
                g.drawString("1", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - 5 * fontSize / 4));
            } else if (tile.getAttributes().get(TileAttributes.SPAWN_TWO)) {
                g.drawString("2", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - 5 * fontSize / 4));
            } else if (tile.getAttributes().get(TileAttributes.SPAWN_THREE)) {
                g.drawString("3", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - 5 * fontSize / 4));
            } else if (tile.getAttributes().get(TileAttributes.SPAWN_OTHER)) {
                g.drawString("O", (int) (fontSize / 4 + insets.left), (int) (getHeight() - insets.bottom - 5 * fontSize / 4));
            }
        }
        if (selected) {
            g.setColor(new Color(0, 102, 255, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    void setSelected(
            boolean s, boolean unselectOthers) {
        if (unselectOthers) {
            mapGridPane.setSelected(false);
        }
        selected = s;
        repaint();
    }

    void setTile(Tile tile) {
        this.tile = tile;
        TileAttributes attributes = tile.getAttributes();
        setBorder(BorderFactory.createLineBorder(attributes.getColorRepresentation(), BORDER_WIDTH));
    }

    private class MapPanelMouseListener extends MouseAdapter {

        private boolean inPanel = false;

        @Override
        public void mouseEntered(MouseEvent evt) {
            inPanel = true;
        }

        @Override
        public void mouseExited(MouseEvent evt) {
            inPanel = false;
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            inPanel = true;
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            if (inPanel) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    setSelected(!isSelected(), !evt.isControlDown());
                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    setSelected(true, !evt.isControlDown() && !isSelected());
                    MapPanelPopupMenu.show(MapPanel.this,
                            mapGridPane.getSelectedPanels(), evt.getX(), evt.getY());
                }
            }
        }
    }

    private static class MapPanelPopupMenu {

        private static byte base;
        private static final JMenuItem baseMenuItem;
        private static MapPanel[] mapPanels;
        private static final JPopupMenu popupMenu = new JPopupMenu();
        private static final JMenuItem removeMenuItem;
        private static final JMenuItem rotateMenuItem;
        private static short rotationAngle;
        private static final JCheckBoxMenuItem semiSolidMenuItem;
        private static byte solidity;
        private static final JCheckBoxMenuItem solidMenuItem;
        private static final JMenuItem spawnMenuItem;
        private static byte spawnPoint;

        static {

            ActionListener listener = new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (evt.getActionCommand().equals(COMMAND_BASE)) {
                        base = (byte) JOptionPane.showOptionDialog(null,
                                "Please select a base type:", "Base Selection",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null,
                                new String[]{"None", "Base One", "Base Two",
                                    "Base Three"}, "None");
                        if (base == -1) {
                            return;
                        }
                        base += TileAttributes.BASE_NONE;
                    } else if (evt.getActionCommand().equals(COMMAND_ROTATE)) {
                        String angle = JOptionPane.showInputDialog(
                                "Please enter the rotation angle (degrees):");
                        try {
                            rotationAngle = Short.parseShort(angle);
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(null,
                                    "The rotation angle you entered was invalid.",
                                    "Invalid Rotation Angle", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (evt.getActionCommand().equals(COMMAND_SEMI_SOLID)) {
                        solidity = TileAttributes.NOT_SOLID;
                        for (MapPanel panel : mapPanels) {
                            if (!panel.getTile().getAttributes().get(TileAttributes.SEMI_SOLID)) {
                                solidity = TileAttributes.SEMI_SOLID;
                                break;
                            }
                        }
                    } else if (evt.getActionCommand().equals(COMMAND_SOLID)) {
                        solidity = TileAttributes.NOT_SOLID;
                        for (MapPanel panel : mapPanels) {
                            if (!panel.getTile().getAttributes().get(TileAttributes.SOLID)) {
                                solidity = TileAttributes.SOLID;
                                break;
                            }
                        }
                    } else if (evt.getActionCommand().equals(COMMAND_SPAWN)) {
                        spawnPoint = (byte) JOptionPane.showOptionDialog(null,
                                "Please select a spawn point type:", "Spawn Point Selection",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null,
                                new String[]{"None", "Spawn One", "Spawn Two",
                                    "Spawn Three", "Spawn Other"}, "None");
                        if (spawnPoint == -1) {
                            return;
                        }
                        spawnPoint += TileAttributes.SPAWN_NONE;
                    }
                    for (MapPanel panel : mapPanels) {
                        panel.actionPerformed(evt);
                    }
                }
            };

            baseMenuItem = new JMenuItem("Base...");
            baseMenuItem.setActionCommand(COMMAND_BASE);
            baseMenuItem.addActionListener(listener);
            popupMenu.add(baseMenuItem);

            semiSolidMenuItem = new JCheckBoxMenuItem("Semi-Solid");
            semiSolidMenuItem.setActionCommand(COMMAND_SEMI_SOLID);
            semiSolidMenuItem.addActionListener(listener);
            popupMenu.add(semiSolidMenuItem);

            solidMenuItem = new JCheckBoxMenuItem("Solid");
            solidMenuItem.setActionCommand(COMMAND_SOLID);
            solidMenuItem.addActionListener(listener);
            popupMenu.add(solidMenuItem);

            spawnMenuItem = new JMenuItem("Spawn Point...");
            spawnMenuItem.setActionCommand(COMMAND_SPAWN);
            spawnMenuItem.addActionListener(listener);
            popupMenu.add(spawnMenuItem);

            rotateMenuItem = new JMenuItem("Rotate...");
            rotateMenuItem.setActionCommand(COMMAND_ROTATE);
            rotateMenuItem.addActionListener(listener);
            popupMenu.add(rotateMenuItem);

            removeMenuItem = new JMenuItem("Remove");
            removeMenuItem.setActionCommand(COMMAND_REMOVE);
            removeMenuItem.addActionListener(listener);
            popupMenu.add(removeMenuItem);
        }

        private static void show(MapPanel parent, MapPanel[] selectedPanels, int x, int y) {
            mapPanels = selectedPanels;
            semiSolidMenuItem.setSelected(true);
            solidMenuItem.setSelected(true);
            spawnMenuItem.setSelected(true);
            removeMenuItem.setEnabled(false);
            rotateMenuItem.setEnabled(true);
            for (MapPanel panel : selectedPanels) {
                TileAttributes attributes = panel.getTile().getAttributes();
                if (!attributes.get(TileAttributes.SEMI_SOLID)) {
                    semiSolidMenuItem.setSelected(false);
                }
                if (!attributes.get(TileAttributes.SOLID)) {
                    solidMenuItem.setSelected(false);
                }
                if (panel.getTile().getImageID() == -1) {
                    rotateMenuItem.setEnabled(false);
                } else {
                    removeMenuItem.setEnabled(true);
                }
            }
            popupMenu.show(parent, x, y);
        }
    }

    private class MapPanelTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DATA_FLAVOR);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            try {
                tile.setImageID((Short) support.getTransferable().getTransferData(
                        DATA_FLAVOR));
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
