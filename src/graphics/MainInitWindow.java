package graphics;

import graphics.maps.editor.MapEditor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Scott
 */
public class MainInitWindow extends JFrame implements ActionListener {

    private String INSTRUCTIONS_SITE = "http://ahsdomination.sourceforge.net/?page_id=74";
    private JButton btnExit;
    private JButton btnMapEditor;
    private JButton btnPlay;
    private JButton btnShowInstructions;

    public MainInitWindow() {
        GraphicsUtilities.setUIManager();
        initComponents();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(350, 250);
        setTitle("Domination");
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(btnExit.getActionCommand())) {
            dispose();
        } else if (evt.getActionCommand().equals(btnMapEditor.getActionCommand())) {
            new MapEditor().setVisible(true);
            dispose();
        } else if (evt.getActionCommand().equals(btnPlay.getActionCommand())) {
            new GameInitWindow().setVisible(true);
            dispose();
        } else if (evt.getActionCommand().equals(btnShowInstructions.getActionCommand())) {
            try {
                Desktop.getDesktop().browse(new URI(INSTRUCTIONS_SITE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponents() {
        setLayout(new GridLayout(6, 1));

        btnPlay = new JButton("Play");
        btnPlay.addActionListener(this);

        btnMapEditor = new JButton("Map Editor");
        btnMapEditor.addActionListener(this);

        btnShowInstructions = new JButton("Instructions");
        btnShowInstructions.addActionListener(this);

        btnExit = new JButton("Exit");
        btnExit.addActionListener(this);

        JLabel label = new JLabel("Domination");
        label.setFont(label.getFont().deriveFont(36f).deriveFont(Font.BOLD));

        add(label);
        add(new JPanel());
        add(btnPlay);
        add(btnMapEditor);
        add(btnShowInstructions);
        add(btnExit);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new MainInitWindow().setVisible(true);
            }
        });
    }
}
