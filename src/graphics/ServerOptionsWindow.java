package graphics;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.MaskFormatter;
import networking.ServerOptions;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class ServerOptionsWindow extends JDialog implements ActionListener {

    private static final Integer[] HEALTH_REGENERATION_INCREMENTS = {1, 2, 5};
    private static final String[] HEALTH_REGENERATION_STRINGS = {"Slow", "Normal", "Fast"};
    private static final Integer[] RESPAWN_DELAYS = {1, 2, 3, 5, 10};
    private static final Integer[] SCORE_LIMITS = {-1, 100, 200, 300, 500, 1000, 2000};
    private static final Integer[] TIME_LIMITS = {-1, 2, 5, 10, 15, 20, 30};
    private JCheckBox chkAutoStart;
    private JButton btnCancel;
    private JButton btnSave;
    private JComboBox healthRegenerationList;
    private JComboBox mapList;
    private JFormattedTextField txtMaxPlayers;
    private JFormattedTextField txtMinPlayers;
    private JComboBox respawnList;
    private JComboBox scoreLimitList;
    private ServerOptions serverOptions;
    private JComboBox timeLimitList;

    public ServerOptionsWindow(LobbyWindow window, ServerOptions options) {
        super(window);
        serverOptions = options;
        GraphicsUtilities.setUIManager();
        initComponents();
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Domination");
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(btnCancel.getActionCommand())) {
            dispose();
        } else if (evt.getActionCommand().equals(btnSave.getActionCommand())) {
            serverOptions.setAutoStart(chkAutoStart.isSelected());
            serverOptions.setHealthRegenerationIncrement(
                    HEALTH_REGENERATION_INCREMENTS[healthRegenerationList.getSelectedIndex()]);
            serverOptions.setMap(String.valueOf(mapList.getSelectedItem()));
            serverOptions.setMaximumPlayers(Integer.valueOf((String) txtMaxPlayers.getValue()));
            serverOptions.setMinimumPlayers(Integer.valueOf((String) txtMinPlayers.getValue()));
            serverOptions.setRespawnDelay((Integer) respawnList.getSelectedItem());
            serverOptions.setScoreLimit(SCORE_LIMITS[scoreLimitList.getSelectedIndex()]);
            serverOptions.setTimeLimit(TIME_LIMITS[timeLimitList.getSelectedIndex()]);
            dispose();
        }
    }

    private void initComponents() {
        ArrayList<String> availableMaps = GraphicsUtilities.getAvailableMaps();
        String[] maps = new String[availableMaps.size()];
        availableMaps.toArray(maps);
        mapList = new JComboBox(maps);
        mapList.setSelectedItem(serverOptions.getMap());

        respawnList = new JComboBox(RESPAWN_DELAYS);
        respawnList.setSelectedItem(serverOptions.getRespawnDelay());

        healthRegenerationList = new JComboBox(HEALTH_REGENERATION_STRINGS);
        for (int i = 0; i < HEALTH_REGENERATION_INCREMENTS.length; i++) {
            if (HEALTH_REGENERATION_INCREMENTS[i] == serverOptions.getHealthRegenerationIncrement()) {
                healthRegenerationList.setSelectedIndex(i);
            }
        }

        String[] scoreLimitStrings = new String[SCORE_LIMITS.length];
        for (int i = 0; i < scoreLimitStrings.length; i++) {
            if (SCORE_LIMITS[i] == -1) {
                scoreLimitStrings[i] = "Unlimited";
            } else {
                scoreLimitStrings[i] = String.valueOf(SCORE_LIMITS[i]);
            }
        }
        scoreLimitList = new JComboBox(scoreLimitStrings);
        for (int i = 0; i < SCORE_LIMITS.length; i++) {
            if (SCORE_LIMITS[i] == serverOptions.getScoreLimit()) {
                scoreLimitList.setSelectedIndex(i);
            }
        }

        String[] timeLimitStrings = new String[TIME_LIMITS.length];
        for (int i = 0; i < timeLimitStrings.length; i++) {
            if (TIME_LIMITS[i] == -1) {
                timeLimitStrings[i] = "Unlimited";
            } else {
                timeLimitStrings[i] = TIME_LIMITS[i] + " Minutes";
            }
        }
        timeLimitList = new JComboBox(timeLimitStrings);
        for (int i = 0; i < TIME_LIMITS.length; i++) {
            if (TIME_LIMITS[i] == serverOptions.getTimeLimit()) {
                timeLimitList.setSelectedIndex(i);
            }
        }

        chkAutoStart = new JCheckBox("Auto Start");
        chkAutoStart.setSelected(serverOptions.isAutoStarting());

        try {
            MaskFormatter twoNumberMask = new MaskFormatter("##");
            twoNumberMask.setPlaceholderCharacter('0');
            txtMaxPlayers = new JFormattedTextField(twoNumberMask);
            txtMinPlayers = new JFormattedTextField(twoNumberMask);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        String maxPlayerDisplay;
        String minPlayerDisplay;
        if ((Integer) serverOptions.getMaximumPlayers() < 10) {
            maxPlayerDisplay = "0" + String.valueOf(serverOptions.getMaximumPlayers());
        } else {
            maxPlayerDisplay = String.valueOf(serverOptions.getMinimumPlayers());
        }
        DecimalFormat twoNumberFormat = new DecimalFormat("00");
        txtMaxPlayers.setValue(twoNumberFormat.format(serverOptions.getMaximumPlayers()));
        txtMinPlayers.setValue(twoNumberFormat.format(serverOptions.getMinimumPlayers()));
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Game Rules"), gbc);

        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.ipadx = 10;
        optionsPanel.add(new JLabel(""), gbc);

        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Player Options"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Map:"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.ipadx = 0;
        optionsPanel.add(mapList, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Score Limit:"), gbc);

        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.ipadx = 0;
        optionsPanel.add(scoreLimitList, gbc);

        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Respawn Delay (sec):"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.ipadx = 0;
        optionsPanel.add(respawnList, gbc);

        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Health Regeneration:"), gbc);

        gbc.gridy = 2;
        gbc.gridx = 4;
        gbc.ipadx = 0;
        optionsPanel.add(healthRegenerationList, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Time Limit (min):"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.ipadx = 0;
        optionsPanel.add(timeLimitList, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(chkAutoStart, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Maximum Players:"), gbc);

        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.ipadx = 0;
        optionsPanel.add(txtMaxPlayers, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.ipadx = 0;
        optionsPanel.add(new JLabel("Minimum Players:"), gbc);

        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.ipadx = 0;
        optionsPanel.add(txtMinPlayers, gbc);


        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);

        btnSave = new JButton("Save");
        btnSave.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        JPanel overallPanel = new JPanel(new BorderLayout());
        overallPanel.add(optionsPanel, BorderLayout.CENTER);
        overallPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(overallPanel);
    }
}
