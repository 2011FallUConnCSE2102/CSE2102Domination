package graphics;

import gameplay.PlayerRunner;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import networking.NetUtils;
import networking.ServerInfo;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class GameInitWindow extends JFrame implements ActionListener, ListSelectionListener {

    public static final int USERNAME_LENGTH_LIMIT = 15;
    public static final int SERVER_NAME_LENGTH_LIMIT = 30;
    private static final int TIMER_DELAY = 1000;
    private JButton btnHost;
    private JButton btnJoin;
    private JButton btnMainMenu;
    private JButton btnManualConnect;
    private boolean selectionChanging = false;
    private JTable table;
    private TableModel tableModel;
    private Timer timer;

    public GameInitWindow() {
        GraphicsUtilities.setUIManager();
        initComponents();

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                kill();
            }
        });

        NetUtils.startListening();
        timer = new Timer(TIMER_DELAY, this);
        timer.setActionCommand("Timer");
        timer.start();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(500, 200);
        setTitle("Domination");
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(timer.getActionCommand())) {
            updateServerList();
        } else if (evt.getActionCommand().equals(btnMainMenu.getActionCommand())) {
            kill();
            new MainInitWindow().setVisible(true);
        } else if (evt.getActionCommand().equals(btnHost.getActionCommand())) {
            String serverName = JOptionPane.showInputDialog(this,
                    "Enter a server name to proceed (" + SERVER_NAME_LENGTH_LIMIT
                    + " characters max).");
            if (serverName != null) {
                if (serverName.length() == 0
                        || serverName.length() > SERVER_NAME_LENGTH_LIMIT) {
                    JOptionPane.showMessageDialog(this, "The server name you entered"
                            + " was invalid.", "Invalid Server Name",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    new LobbyWindow(serverName).setVisible(true);
                    kill();
                }
            }
        } else if (evt.getActionCommand().equals(btnJoin.getActionCommand())) {
            try {
                String username = JOptionPane.showInputDialog(this,
                        "Enter a username to proceed (" + USERNAME_LENGTH_LIMIT
                        + " characters max).");
                if (username != null) {
                    if (username.length() == 0
                            || username.length() > USERNAME_LENGTH_LIMIT) {
                        JOptionPane.showMessageDialog(this, "The username you entered"
                                + " was invalid.", "Invalid Username",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        new PlayerRunner(username, tableModel.getValueAt(table.getSelectedRow()));
                        kill();
                    }
                }
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "An error occurred while trying"
                        + " to connect to the server you selected."
                        + "\nPlease try again" + " or choose a different server.",
                        "Error Connecting To Server", JOptionPane.ERROR_MESSAGE);
                ioe.printStackTrace();
            }
        } else if (evt.getActionCommand().equals(btnManualConnect.getActionCommand())) {
            new ManualConnectWindow().setVisible(true);
            kill();
        }
    }

    private void initComponents() {
        tableModel = new TableModel();

        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(this);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tablePane = new JScrollPane(table);

        btnMainMenu = new JButton("Main Menu");
        btnMainMenu.addActionListener(this);

        btnJoin = new JButton("Join Game");
        btnJoin.addActionListener(this);
        btnJoin.setEnabled(false);

        btnManualConnect = new JButton("Manually Connect");
        btnManualConnect.addActionListener(this);

        btnHost = new JButton("Host Game");
        btnHost.addActionListener(this);

        JPanel connectPanel = new JPanel(new GridLayout(1, 4));
        connectPanel.add(btnMainMenu);
        connectPanel.add(btnJoin);
        connectPanel.add(btnManualConnect);
        connectPanel.add(btnHost);

        JPanel serversPanel = new JPanel(new BorderLayout());
        serversPanel.add(tablePane, BorderLayout.CENTER);
        serversPanel.add(connectPanel, BorderLayout.SOUTH);
        add(serversPanel);
    }

    private void kill() {
        dispose();
        timer.stop();
        NetUtils.stopListening();
    }

    private void updateServerList() {
        selectionChanging = true;
        int selectedRow = table.getSelectedRow();
        ServerInfo server = null;
        if (selectedRow != -1) {
            server = tableModel.getValueAt(selectedRow);
        }

        tableModel.setRows(NetUtils.getAvailableServers());

        int selectionIndex = tableModel.indexOf(server);
        if (selectionIndex != -1) {
            table.setRowSelectionInterval(selectionIndex, selectionIndex);
        } else {
            btnJoin.setEnabled(false);
        }

        selectionChanging = false;
    }

    public void valueChanged(ListSelectionEvent evt) {
        if (!selectionChanging) {
            btnJoin.setEnabled(evt.getFirstIndex() != -1);
        }
    }

    private class TableModel extends AbstractTableModel {

        private String[] columnNames = new String[]{"Name", "IP Address", "Port"};
        private ServerInfo[] rows = new ServerInfo[]{};

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return rows.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return rows[row].getServerName();
            } else if (col == 1) {
                return rows[row].getServerIPAddress();
            } else if (col == 2) {
                return rows[row].getServerPort();
            } else {
                return null;
            }
        }

        private ServerInfo getValueAt(int row) {
            return rows[row];
        }

        private int indexOf(ServerInfo server) {
            for (int i = 0; i < rows.length; i++) {
                if (rows[i].equals(server)) {
                    return i;
                }
            }
            return -1;
        }

        private void setRows(ServerInfo[] newRows) {
            rows = newRows;
            fireTableDataChanged();
        }
    }
}
