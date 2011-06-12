/*
 * Created on 24/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViterbiMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 8858894944694462896L;
	private Collection _colFileListener = null;
	
	public ViterbiMenuBar() {
		_colFileListener = new Vector();
		
		createMenuArquivo();
	}
	
	public void addFileListener(FileListener listener) {
		_colFileListener.add(listener);
	}
	
	public void createMenuArquivo() {
		JMenu menuArquivo = new JMenu("Arquivo");
		menuArquivo.setMnemonic('A');

		JMenuItem menuItemOpen = new JMenuItem("Open");
		menuItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOpen();
			}
		});

		JMenuItem menuItemSave = new JMenuItem("Salvar");
		menuItemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});

		JMenuItem menuItemSaveAs = new JMenuItem("Salvar como...");
		menuItemSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSaveAs();
			}
		});
		
		menuArquivo.add(menuItemOpen);
		menuArquivo.add(menuItemSave);
		menuArquivo.add(menuItemSaveAs);
		
		add(menuArquivo);
	}
	
	public void doOpen() {
		Iterator iterator = _colFileListener.iterator();
		while(iterator.hasNext()) {
			FileListener listener = (FileListener) iterator.next();
			listener.open();
		}
	}

	public void doSave() {
		Iterator iterator = _colFileListener.iterator();
		while(iterator.hasNext()) {
			FileListener listener = (FileListener) iterator.next();
			listener.save();
		}
	}

	public void doSaveAs() {
		Iterator iterator = _colFileListener.iterator();
		while(iterator.hasNext()) {
			FileListener listener = (FileListener) iterator.next();
			listener.saveAs();
		}
	}

}
