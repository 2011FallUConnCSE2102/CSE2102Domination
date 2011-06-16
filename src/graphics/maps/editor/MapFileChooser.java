package graphics.maps.editor;

import java.io.File;
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
class MapFileChooser extends JFileChooser {

    private MapEditor parent;

    MapFileChooser(MapEditor parent) {
        this.parent = parent;
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(new MapFilter());
    }

    File openMapFile() {
        if (showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return getSelectedFile();
        }
        return null;
    }
}

class MapFilter extends FileFilter {

    public boolean accept(File pathname) {

        if (pathname.isDirectory()) {
            return true;
        } else if (getExtension(pathname) == null) {
            return false;
        } else if (getExtension(pathname).equals("dmap")) {
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
        return "Domination Map File";
    }
}
