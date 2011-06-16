package graphics;

import gameplay.PlayerRunner;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class PlayerScreen extends JFrame {

    private static final int DELAY = 20;
    private final PlayerCanvas canvas;
    private final ScreenUpdaterThread updaterThread;

    public PlayerScreen(final PlayerRunner parent, byte playerID) {
        super(GraphicsUtilities.getGraphicsConfiguration());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                parent.kill(true);
            }
        });

        canvas = new PlayerCanvas(parent, this, playerID);
        add(canvas);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);
        setIgnoreRepaint(true);
        setResizable(false);
        setTitle("Domination");
        pack();
        setLocationRelativeTo(null);
        canvas.initGraphics();
        updaterThread = new ScreenUpdaterThread();
        updaterThread.start();
    }

    public void kill() {
        updaterThread.kill();
        canvas.kill();
    }

    private class ScreenUpdaterThread extends Thread {

        private boolean run = true;

        private ScreenUpdaterThread() {
            super("Screen Updater Thread");
            setDaemon(true);
        }

        private void kill() {
            run = false;
        }

        @Override
        public void run() {
            while (run) {
                try {
                    try {
                        canvas.renderImage();
                    } catch (IllegalStateException ise) {
                        //ignore
                    }
                    sleep(DELAY);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
