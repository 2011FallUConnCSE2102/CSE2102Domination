package graphics;

import gameplay.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import networking.Client;
import networking.Message;
import networking.MessageReceiver;
import networking.Server;
import networking.ServerOptions;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 * @author Joe Stein
 */
public class LobbyWindow extends JFrame implements ActionListener, MessageReceiver {

    private JButton btnStart;
    private JButton btnLeave;
    private JButton btnEdit;
    private ServerOptions options = new ServerOptions();
    private Server server;
    private JTable table;
    private TableModel tableModel;

    public LobbyWindow(String serverName) {
        try {
            server = new Server(true, serverName, this, options);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                kill();
            }
        });

        GraphicsUtilities.setUIManager();
        initComponents();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(500, 400);
        setTitle("Domination");
        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(btnStart.getActionCommand())) {
            btnEdit.setEnabled(false);
            btnStart.setEnabled(false);
            server.startGame(20);
        } else if (evt.getActionCommand().equals(btnLeave.getActionCommand())) {
            kill();
        } else if (evt.getActionCommand().equals(btnEdit.getActionCommand())) {
            new ServerOptionsWindow(this, options).setVisible(true);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new TableModel(server.getClients());

        table = new JTable(tableModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(Color.LIGHT_GRAY);
                } else {
                    c.setBackground(getBackground());
                }
                return c;
            }
        };
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(false);

        JScrollPane tablePane = new JScrollPane(table);

        btnLeave = new JButton("Leave lobby");
        btnLeave.addActionListener(this);

        btnEdit = new JButton("Edit game options");
        btnEdit.addActionListener(this);

        btnStart = new JButton("Start Game");
        btnStart.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(btnLeave);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnStart);

        JPanel serverInfoPanel = new JPanel(new GridLayout(3, 1));

        serverInfoPanel.add(new JLabel("Internal IP Address: " + server.getInternalIPAddress()));
        serverInfoPanel.add(new JLabel("External IP Address: " + server.getExternalIPAddress()));
        serverInfoPanel.add(new JLabel("Port: " + server.getPort()));

        add(serverInfoPanel, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void kill() {
        try {
            server.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //server = null;
        new GameInitWindow().setVisible(true);
        dispose();
    }

    public void receiveMessage(Message message) {
        if (message.getType() == Message.PLAYERS_CHANGED) {
            tableModel.fireTableDataChanged();
        }
    }

    private class TableModel extends AbstractTableModel {

        private String[] columnNames = new String[]{"Username", "IP Address", "Port"};
        private final ArrayList<Client> rows;

        public TableModel(ArrayList<Client> clients) {
            rows = clients;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return rows.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                Player player = rows.get(row).getPlayer();
                if (player == null) {
                    return null;
                } else {
                    return player.getUsername();
                }
            } else if (col == 1) {
                return rows.get(row).getSocket().getInetAddress().getHostAddress();
            } else if (col == 2) {
                return rows.get(row).getSocket().getPort();
            } else {
                return null;
            }
        }
    }
}
