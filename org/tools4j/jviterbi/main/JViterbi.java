//TODO: Botão: Mudar o modo de trocar os valores de entrada, abrir uma JOptionPane pedindo a nova palavra
//TODO: Botão: Criar uma janela para entrar com a constelação
//TODO: Criar o panel da trelissa com distancias de 100px entre pontos e 25 da margem

/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.main;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.tools4j.jviterbi.gui.EncoderPanel;
import org.tools4j.jviterbi.gui.FileListener;
import org.tools4j.jviterbi.gui.TrellisPanel;
import org.tools4j.jviterbi.gui.ViterbiMenuBar;
import org.tools4j.jviterbi.gui.ViterbiToolBar;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JViterbi extends JFrame implements FileListener {
	
	private static final long serialVersionUID = -5851723697427527699L;
	private File _file = null;
	private EncoderPanel _encoderPanel = null;
	private TrellisPanel _trellisPanel = null;
	private ViterbiToolBar _viterbiToolBar = null;
	private JTabbedPane _tabbedPane = null; 	

	public static void main(String[] args) {
		JFrame frame = new JViterbi();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public JViterbi() {
		_tabbedPane = new JTabbedPane();

		_encoderPanel = new EncoderPanel();
		_trellisPanel = new TrellisPanel();
		_trellisPanel.setCoreHandler(_encoderPanel.getCoreHandler());

		_tabbedPane.addTab("Encoder", _encoderPanel);
		_tabbedPane.addTab("Trellis", new JScrollPane(_trellisPanel));
		
		//tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		ViterbiMenuBar viterbiMenuBar = new ViterbiMenuBar();
		viterbiMenuBar.addFileListener(this);
		setJMenuBar(viterbiMenuBar);

		_viterbiToolBar = new ViterbiToolBar();
		_viterbiToolBar.addCommandsListener(_encoderPanel);
		
		getContentPane().add(_tabbedPane);
		getContentPane().add(_viterbiToolBar, BorderLayout.PAGE_START);
		setSize(500, 500);	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			_file = fileChooser.getSelectedFile();
			save();
		}
	}

	public void save() {
		if(_file == null) {
			saveAs();
		} else {
			try {
				FileOutputStream fos = new FileOutputStream(_file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				
				oos.writeObject(_encoderPanel);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void open() {
		JFileChooser fileChooser = new JFileChooser();
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				_file = fileChooser.getSelectedFile();
				
				FileInputStream fis = new FileInputStream(fileChooser.getSelectedFile());
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis);
				
				EncoderPanel encoderPanel = (EncoderPanel) ois.readObject();
				ois.close();
				
				_tabbedPane.remove(_encoderPanel);
				_viterbiToolBar.removeCommandsListener(_encoderPanel);
				
				_tabbedPane.add(encoderPanel, 0);
				_tabbedPane.setTitleAt(0, "Encoder");
				_tabbedPane.setSelectedComponent(encoderPanel);
				_viterbiToolBar.addCommandsListener(encoderPanel);
				_trellisPanel.setCoreHandler(encoderPanel.getCoreHandler());
				
				_encoderPanel = encoderPanel;
				
				repaint();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
