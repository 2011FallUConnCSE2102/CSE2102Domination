package graphics;

import gameplay.PlayerRunner;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import networking.ServerInfo;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class ManualConnectWindow extends JFrame implements ActionListener {

    private JTextField txtIPAdress;
    private JTextField txtPort;
    private JButton btnConnect;
    private JButton btnBack;

    public ManualConnectWindow() {
        GraphicsUtilities.setUIManager();
        initComponents();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(200, 150);
        setTitle("Domination");
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        // the connect button
        if (evt.getActionCommand().equals("Connect")) {
            if (txtIPAdress.getText().equals("") && txtPort.getText().equals("")) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "Please fill in all text fields",
                        "A plain message",
                        JOptionPane.PLAIN_MESSAGE);
                return;
            }

            try {
                String username = JOptionPane.showInputDialog(this,
                        "Please enter your desired username:");
                if (username != null) {
                    if (username.equals("")) {
                        JOptionPane.showMessageDialog(this, "The username you entered" + " was invalid.", "Invalid Username",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {
                            new PlayerRunner(username,
                                    new ServerInfo(txtIPAdress.getText(), Integer.valueOf(txtPort.getText())));
                        } catch (NumberFormatException nfe) {
                        }
                        dispose();
                    }
                }
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "An error occurred while trying"
                        + " to connect to the server you selected."
                        + "\nPlease try again" + " or choose a different server.",
                        "Error Connecting To Server", JOptionPane.ERROR_MESSAGE);
                ioe.printStackTrace();
            }

            // the back button
        } else if (evt.getActionCommand().equals("Back")) {
            new GameInitWindow().setVisible(true);
            dispose();
        }
    }

    private void initComponents() {
        txtIPAdress = new JTextField();
        txtPort = new JTextField();

        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(this);

        btnBack = new JButton("Back");
        btnBack.addActionListener(this);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        inputPanel.add(new JLabel("IP Address:"), gbc);

        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.ipadx = 80;
        inputPanel.add(txtIPAdress, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        inputPanel.add(new JLabel("Port:"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.ipadx = 80;
        inputPanel.add(txtPort, gbc);

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(btnBack);
        buttonPanel.add(btnConnect);

        JPanel overallPanel = new JPanel(new BorderLayout());
        overallPanel.add(inputPanel, BorderLayout.CENTER);
        overallPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(overallPanel);
    }
}
