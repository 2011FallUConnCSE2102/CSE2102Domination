/*
 * Created on Jun 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Editor extends JFrame {

	public static final String TEXT_TITLE = "JPetriNet V1.1";	
//	private boolean m_bCanDraw = false;
	private PetriToolBar m_petriToolBar;
	private PetriNetworkDraw m_petriNetworkDraw;
	private PetriLogPane m_petriLogPane;
	private PetriTree m_petriTree;
	private static JTree m_tree;
	private JMenuBar m_mainMenuBar;
	private JMenuItem m_exitMenuItem;
	private JMenuItem m_aboutMenuItem;
	
	public Editor() {
		super (Editor.TEXT_TITLE);
		initDialog ();
	}
	
	public static void main(String[] args) {
		final Editor myEditor = new Editor ();
		myEditor.setSize(800, 600);
		myEditor.show();
		
		myEditor.addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent event) {
				myEditor.finalizeApplication();
			}
		});
	}
	
	public void initDialog () {
		Container myContainer = this.getContentPane();
		m_petriLogPane = new PetriLogPane();
		m_petriTree = new PetriTree();
		m_petriNetworkDraw = new PetriNetworkDraw(m_petriLogPane, m_petriTree);
		m_petriToolBar = new PetriToolBar();
		m_mainMenuBar = new JMenuBar();
		
		
		m_petriToolBar.addPetriToolBarListener(m_petriNetworkDraw);
		
		// Building the menubar
		buildMenu();
		this.setJMenuBar(m_mainMenuBar);
		
		// Creating the toolbar and the menu bar
		myContainer.add(m_petriToolBar, BorderLayout.NORTH);
		
		// Cria o ScrollPane do PetriLogPane
		JScrollPane scrollPaneLogPane = new JScrollPane(m_petriLogPane);
		
		// Cria o ScrollPane do PetriTree
		m_tree = new JTree(m_petriTree);
		m_tree.setShowsRootHandles(true);
		JScrollPane scrollPaneTree = new JScrollPane(m_tree);
		
		// Cria o SplitPane entre a Tree e o Log
		JSplitPane splitPaneLogTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			scrollPaneLogPane, scrollPaneTree); 
		splitPaneLogTree.setDividerLocation(300);
		splitPaneLogTree.setOneTouchExpandable(true);
		
		
		// Cria o SplitPane principal
		JSplitPane splitPaneMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			new JScrollPane(m_petriNetworkDraw), splitPaneLogTree);
		splitPaneMain.setDividerLocation(400);
		splitPaneMain.setOneTouchExpandable(true);
		
		myContainer.add(splitPaneMain);
	}

	private void buildTopPane(Container myContainer) {
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridLayout(0,1));
		topPane.add(m_mainMenuBar);
		topPane.add(m_petriToolBar);
		myContainer.add(topPane, BorderLayout.NORTH);
	}

	private void buildMenu() {
		// Builds the file menu
		JMenu fileMenu = buildFileMenu();
		
		// Builds the preferences menu
		JMenu preferencesMenu = buildPreferencesMenu();
		
		// Builds the help menu
		JMenu helpMenu = buildHelpMenu();
		
		// Add the menus to the menubar
		m_mainMenuBar.add(fileMenu);
		m_mainMenuBar.add(preferencesMenu);
		m_mainMenuBar.add(helpMenu);
	}

	private JMenu buildPreferencesMenu() {
		JMenu preferencesMenu = new JMenu("Preferences");
		preferencesMenu.setMnemonic(KeyEvent.VK_P);
		
		JMenu lookAndFeelMenu = new JMenu("Look and Feel");
		lookAndFeelMenu.setMnemonic(KeyEvent.VK_L);
		
		ButtonGroup lookAndFeelGroup = new ButtonGroup();
		
		JRadioButtonMenuItem javaLookAndFellMenuItem = new JRadioButtonMenuItem("Java Look and Feel");
		javaLookAndFellMenuItem.setMnemonic(KeyEvent.VK_J);
		javaLookAndFellMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Error setting look and feel.");
				}
				SwingUtilities.updateComponentTreeUI(Editor.this);
			}
		});
		javaLookAndFellMenuItem.setSelected(true);

		JRadioButtonMenuItem systemLookAndFellMenuItem = new JRadioButtonMenuItem("System Look and Feel");
		systemLookAndFellMenuItem.setMnemonic(KeyEvent.VK_S);
		systemLookAndFellMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println("Error setting look and feel.");
				}
				SwingUtilities.updateComponentTreeUI(Editor.this);
			}
		});
		
		lookAndFeelGroup.add(javaLookAndFellMenuItem);
		lookAndFeelGroup.add(systemLookAndFellMenuItem);
		
		lookAndFeelMenu.add(javaLookAndFellMenuItem);
		lookAndFeelMenu.add(systemLookAndFellMenuItem);
		
		preferencesMenu.add(lookAndFeelMenu);
		return preferencesMenu;
	}
	
	private JMenu buildHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		// Builds the about menu item
		m_aboutMenuItem = new JMenuItem("About");
		m_aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		m_aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(Editor.this, "JPetriNet V1.1\n\n" +
					"Credits:\n" +
					"    Artur Luís Ribas Barbosa\n" +
					"    Márcio Emílio Cruz Vono de Azevedo\n\n" +
					"http://jpetrinet.sourceforge.net");
			}
		});
		
		helpMenu.add(m_aboutMenuItem);
		return helpMenu;
	}

	private JMenu buildFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		// Builds the exit menu item
		m_exitMenuItem = new JMenuItem("Exit");
		m_exitMenuItem.setMnemonic(KeyEvent.VK_E);
		m_exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		m_exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				finalizeApplication();
			}
		});
		
		fileMenu.add(m_exitMenuItem);
		return fileMenu;
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);  //paint background
	}
	
	public static void updateTree() {
		m_tree.updateUI();		
	}
	
	private void finalizeApplication() {
		// TODO: Implement procedures to finalize application, like save when needed.
		System.exit(0);
	}
}
