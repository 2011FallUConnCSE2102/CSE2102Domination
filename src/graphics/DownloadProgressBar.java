/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Joe Stein
 */
public class DownloadProgressBar {

    private JLabel label;
    private JProgressBar pb;
    private JButton button;
    private int fileSize;
    private JDialog dialog;
    private SwingSafe swingSafe;

    public DownloadProgressBar(JFrame owner, int fileSize) {
        this.fileSize = fileSize;
        swingSafe = new SwingSafe();
        dialog = new JDialog(owner, "Map Download Bar");
        dialog.setResizable(false);
        button = new JButton("Done");
        button.setEnabled(false);
        button.addActionListener(new ButtonListener());

        pb = new JProgressBar(0, fileSize);
        pb.setValue(0);
        pb.setStringPainted(true);

        label = new JLabel("Map Download");

        JPanel panel = new JPanel();
        panel.add(button);
        panel.add(pb);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(panel, BorderLayout.NORTH);
        panel1.add(label, BorderLayout.CENTER);
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(panel1);
        dialog.pack();
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    public void release() {
        swingSafe.setButtonEnabled(true);
    }

    public void updateBar(int bytesDownloaded) {
        swingSafe.updateProgressBar(bytesDownloaded);
        if (bytesDownloaded >= fileSize) {
            swingSafe.updateLabel("Download complete.");
            swingSafe.setButtonEnabled(true);
        }
    }

    /**
     * Utility class for accessing the Swing dialog.  Do not access the Swing
     * dialog directly, as Swing is not thread-safe.
     */
    private class SwingSafe {

        private void setButtonEnabled(final boolean enabled) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    button.setEnabled(enabled);
                }
            });
        }

        private void updateLabel(final String value) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    pb.setString(value);
                }
            });
        }

        private void updateProgressBar(final int value) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    pb.setValue(value);
                }
            });
        }
    }

    class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            dialog.dispose();
        }
    }
}
