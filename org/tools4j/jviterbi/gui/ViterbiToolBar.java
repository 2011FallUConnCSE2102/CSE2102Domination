/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViterbiToolBar extends JToolBar {

	private static final long serialVersionUID = 5893387753562688392L;
	private Collection _colCommandsListener = null;
	
	public ViterbiToolBar() {
		_colCommandsListener = new Vector();
		
		JButton buttonAddInput = new JButton(new ImageIcon("img/input.gif"));
		buttonAddInput.setBackground(Color.WHITE);
		buttonAddInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doAddInput();
			}
		});

		JButton buttonAddMemory = new JButton(new ImageIcon("img/memory.gif"));
		buttonAddMemory.setBackground(Color.WHITE);
		buttonAddMemory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doAddMemory();
			}
		});
		
		JButton buttonAddAdder = new JButton(new ImageIcon("img/adder.gif"));
		buttonAddAdder.setBackground(Color.WHITE);
		buttonAddAdder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doAddAdder();
			}
		});
		
		JButton buttonAddOutput = new JButton(new ImageIcon("img/output.gif"));
		buttonAddOutput.setBackground(Color.WHITE);
		buttonAddOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doAddOutput();
			}
		});

		JButton buttonChangeInput = new JButton("Change Input");
		buttonChangeInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doChangeInput();
			}
		});
		
		JButton buttonCreateConstalation = new JButton("Create Constalation");
		buttonCreateConstalation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doCreateConstalation();
			}
		});

		add(buttonAddInput);
		add(buttonAddMemory);
		add(buttonAddAdder);
		add(buttonAddOutput);
		add(new JSeparator(JSeparator.VERTICAL));
		add(buttonChangeInput);
		add(buttonCreateConstalation);
	}
	
	private void doAddMemory() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.addMemory();
		}
	}
	
	private void doAddAdder() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.addAdder();
		}
	}
	
	private void doAddInput() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.addInput();
		}
	}

	private void doAddOutput() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.addOutput();
		}
	}
	
	private void doChangeInput() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.changeInputValue();
		}
	}
	
	private void doCreateConstalation() {
		Iterator iterator = _colCommandsListener.iterator();
		
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.createConstalation();
		}
	}

	public void addCommandsListener(CommandslListener listener) {
		_colCommandsListener.add(listener);
	}
	
	public void removeCommandsListener(CommandslListener listener) {
		_colCommandsListener.remove(listener);
	}
}
