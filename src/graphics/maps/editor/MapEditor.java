package graphics.maps.editor;

import graphics.GraphicsUtilities;
import graphics.MainInitWindow;
import graphics.maps.Tile;
import graphics.maps.TileAttributes;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
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
public class MapEditor extends JFrame implements ActionListener {

    private static final String NEW_GRID_COMMAND = "new grid";
    private static final String OPEN_IMAGE_COMMAND = "open image";
    private static final String OPEN_MAP_COMMAND = "open map";
    private static final String SAVE_COMMAND = "save";
    private static final String SELECT_ALL_COMMAND = "select all";
    private static final String EXIT_COMMAND = "exit";
    private ImageFileChooser imageChooser;
    private ImageListPane imagePane;
    private LayersPane layersPane;
    private MapFileChooser mapChooser;
    private JMenuItem saveMenuItem;

    public MapEditor() {
        GraphicsUtilities.setUIManager();
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                confirmExit();
            }
        });
        imageChooser = new ImageFileChooser(this);
        mapChooser = new MapFileChooser(this);
        setLayout(new BorderLayout());
        initComponents();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setTitle("Map Editor");
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(EXIT_COMMAND)) {
            confirmExit();
        } else if (evt.getActionCommand().equals(NEW_GRID_COMMAND)) {
            new NewGridDialog(this, layersPane.getLayerCount()).setVisible(true);
        } else if (evt.getActionCommand().equals(OPEN_IMAGE_COMMAND)) {
            BufferedImage[] images = imageChooser.openImageFiles();
            if (images != null) {
                for (BufferedImage image : images) {
                    imagePane.addImage(image);
                }
            }
        } else if (evt.getActionCommand().equals(OPEN_MAP_COMMAND)) {
            File file = mapChooser.openMapFile();
            if (file != null) {
                openMapFile(file);
            }
        } else if (evt.getActionCommand().equals(SAVE_COMMAND)) {
            showSaveMapPrompt();
        } else if (evt.getActionCommand().equals(SELECT_ALL_COMMAND)) {
            layersPane.selectAll();
        }
    }

    void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Leaving Map Editor", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            new MainInitWindow().setVisible(true);
            dispose();
        }
    }

    void createGrid(short rows, short columns, short tileHeight, short tileWidth, byte layer) {
        layersPane.addLayer(rows, columns, tileHeight, tileWidth, layer);
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("New Grid...", KeyEvent.VK_N);
        menuItem.addActionListener(this);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK));
        menuItem.setActionCommand(NEW_GRID_COMMAND);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open Image File...", KeyEvent.VK_O);
        menuItem.addActionListener(this);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItem.setActionCommand(OPEN_IMAGE_COMMAND);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open Map...", KeyEvent.VK_M);
        menuItem.addActionListener(this);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.setActionCommand(OPEN_MAP_COMMAND);
        menu.add(menuItem);

        saveMenuItem = new JMenuItem("Save As...", KeyEvent.VK_S);
        saveMenuItem.addActionListener(this);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        saveMenuItem.setActionCommand(SAVE_COMMAND);
        menu.add(saveMenuItem);

        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(EXIT_COMMAND);
        menu.add(menuItem);

        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);

        menuItem = new JMenuItem("Select All", KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(SELECT_ALL_COMMAND);
        menu.add(menuItem);

        setJMenuBar(menuBar);

        imagePane = new ImageListPane(this);
        add(imagePane, BorderLayout.EAST);

        layersPane = new LayersPane(imagePane);
        add(layersPane, BorderLayout.CENTER);
    }

    void removeImage(int id) {
        layersPane.removeImage(id);
    }

    public void saveToFile(String name) throws FileNotFoundException {
        try {
            String sep = System.getProperty("file.separator");
            String dir = "maps" + sep;
            new File(dir).mkdir();
            FileOutputStream output = new FileOutputStream(new File(dir + name + ".dmap"));
            ImageInfo[] info = imagePane.getImages();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
            writer.setOutput(ImageIO.createImageOutputStream(baos));
            output.write(NetUtils.shortToBytes((short) info.length));
            for (int i = 0; i < info.length; i++) {
                writer.write(info[i].getImage());
                output.write(NetUtils.intToBytes(baos.size()));
                baos.writeTo(output);
                baos.reset();
            }
            short[] imageIDs = new short[info.length];
            for (int i = 0; i < imageIDs.length; i++) {
                imageIDs[i] = info[i].getID();
            }
            output.write(layersPane.getMapData(imageIDs));
            output.close();
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void openMapFile(File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            byte[] temp = new byte[2];
            input.read(temp);
            short imageCount = NetUtils.bytesToShort(temp);
            ImageReader reader = ImageIO.getImageReadersByFormatName("PNG").next();
            imagePane.clearImages();
            for (short i = 0; i < imageCount; i++) {
                temp = new byte[4];
                input.read(temp);
                byte[] bytes = new byte[NetUtils.bytesToInt(temp)];
                input.read(bytes);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                reader.setInput(ImageIO.createImageInputStream(bais));
                imagePane.addImage(reader.read(0));
            }
            layersPane.clear();
            byte layers = (byte) input.read();
            for (byte i = 0; i < layers; i++) {
                temp = new byte[2];
                input.read(temp);
                short rows = NetUtils.bytesToShort(temp);
                temp = new byte[2];
                input.read(temp);
                short columns = NetUtils.bytesToShort(temp);
                temp = new byte[2];
                input.read(temp);
                short tileHeight = NetUtils.bytesToShort(temp);
                temp = new byte[2];
                input.read(temp);
                short tileWidth = NetUtils.bytesToShort(temp);
                Tile[][] tiles = new Tile[rows][columns];
                layersPane.addLayer(rows, columns, tileHeight, tileWidth, i);
                for (int j = 0; j < tiles.length; j++) {
                    for (int k = 0; k < tiles[j].length; k++) {
                        temp = new byte[5];
                        input.read(temp);
                        TileAttributes attributes = TileAttributes.toTileAttributes(temp);
                        temp = new byte[2];
                        input.read(temp);
                        tiles[j][k] = new Tile(attributes, NetUtils.bytesToShort(temp));
                    }
                }
                layersPane.setTiles(i, tiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "The specified map could not be" + " read.", "Error Reading Map File", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSaveMapPrompt() {
        boolean prompt = true;
        while (prompt) {
            String name = JOptionPane.showInputDialog(this, "Map Name:",
                    "Save As", JOptionPane.PLAIN_MESSAGE);
            boolean valid = true;
            if (name == null) {
                prompt = false;
            } else if (name.equals("")) {
                valid = false;
            } else {
                try {
                    saveToFile(name);
                    prompt = false;
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                    valid = false;
                }
            }
            if (!valid) {
                JOptionPane.showMessageDialog(this, "The map file you tried to "
                        + "open is corrupt.  Please try opening a different map"
                        + " file.", "Corrupt Map File",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
