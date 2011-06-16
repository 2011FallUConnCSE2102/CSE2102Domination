package graphics;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class GraphicsUtilities {

    public static ArrayList<String> getAvailableMaps() {
        ArrayList<String> maps = new ArrayList<String>();
        JarFile jar = null;
        File jarFolder = new File(".");
        while (jar == null) {
            if (jarFolder.exists()) {
                for (String fileName : jarFolder.list()) {
                    if (fileName.contains("Domination") && fileName.endsWith(".jar")) {
                        try {
                            jar = new JarFile(jarFolder.getPath()
                                    + System.getProperty("file.separator") + fileName);
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            }
            if (jar == null && jarFolder.getPath().equals("dist")) {
                JOptionPane.showMessageDialog(null, "The JAR you are currently using has an invalid name.\n"
                        + "This program will now exit.", "Invalid JAR Name",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } else {
                jarFolder = new File("dist");
            }
        }
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            String jarEntry = entries.nextElement().getName();
            if (jarEntry.startsWith("graphics/maps/") && jarEntry.endsWith(".dmap")) {
                maps.add(jarEntry.substring(14, jarEntry.length() - 5));
            }
        }
        File mapsFolder = new File("maps");
        if (mapsFolder.exists()) {
            for (String fileName : mapsFolder.list()) {
                if (fileName.endsWith(".dmap")) {
                    maps.add(fileName.substring(0, fileName.length() - 5));
                }
            }
        }
        return maps;
    }

    public static GraphicsConfiguration getGraphicsConfiguration() {
        GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphDevice = graphEnv.getDefaultScreenDevice();
        return graphDevice.getDefaultConfiguration();
    }

    public static InputStream openMap(String name) {
        InputStream input = GraphicsUtilities.class.getResourceAsStream("maps/" + name + ".dmap");
        if (input == null) {
            try {
                input = new FileInputStream(new File("maps" + System.getProperty("file.separator") + name + ".dmap"));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        }
        return input;
    }

    /**
     * Sets the UIManager's Look & Feel to the local system's default Look & Feel.
     */
    public static void setUIManager() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
