/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.tools4j.jviterbi.core.CoreHandler;
import org.tools4j.jviterbi.core.InputNode;
import org.tools4j.jviterbi.core.trellis.ConstelationPoint;
import org.tools4j.jviterbi.exceptions.CantBeginConnectionInOutputException;
import org.tools4j.jviterbi.exceptions.CantConnectToInputException;
import org.tools4j.jviterbi.exceptions.ConnectionAlreadyExist;
import org.tools4j.jviterbi.exceptions.ManyConnectionsInMemoryException;
import org.tools4j.jviterbi.exceptions.SameComponentException;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EncoderPanel extends JPanel implements CommandslListener {
	
	private static final long serialVersionUID = 6608746796789397891L;

	private CoreHandler _coreHandler = null;
	
	private Collection _colLines = null;
/*	private Collection _colMemories = null;
	private Collection _colAdders = null;
	private Collection _colInputs = null;
	private Collection _colOutputs = null;
*/	
	private Line _currentLine = null;
	
	public EncoderPanel() {
		_colLines = new Vector();
		_coreHandler = new CoreHandler();
		
		setBorder(new TitledBorder("Encoder Draw Panel"));
		setLayout(null);
		setBackground(Color.WHITE);
	}
	
	public CoreHandler getCoreHandler() {
		return _coreHandler;
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#addInput()
	 */
	public void addInput() {
		Input newInput = new Input();
		newInput.addCommandsListener(this);
		newInput.setLocation(20, 20);
		add(newInput, 0);
		
		newInput.setAbstractNode(_coreHandler.createInput());
		
		_coreHandler.reset();
		repaint();
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#addOutput()
	 */
	public void addOutput() {
		Output newOutput = new Output();
		newOutput.addCommandsListener(this);
		newOutput.setLocation(20, 20);
		add(newOutput, 0);
		
		newOutput.setAbstractNode(_coreHandler.createOutput());
		
		_coreHandler.reset();
		repaint();
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#addMemory()
	 */
	public void addMemory() {
		Memory newMemory = new Memory();
		newMemory.addCommandsListener(this);
		newMemory.setLocation(20, 20);
		add(newMemory, 0);
		
		newMemory.setAbstractNode(_coreHandler.createMemory());
		
		_coreHandler.reset();
		repaint();
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#addAdder()
	 */
	public void addAdder() {
		Adder newAdder = new Adder();
		newAdder.addCommandsListener(this);
		newAdder.setLocation(20, 20);
		add(newAdder, 0);
		
		newAdder.setAbstractNode(_coreHandler.createAdder());
		
		_coreHandler.reset();
		repaint();
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#addLine()
	 */
	public void addLine(Connection connection) {
		try {
			if(_currentLine == null) {
				_currentLine = new Line();
				_currentLine.setFirstConnection(connection);
			} else {
				_currentLine.setLastConnection(connection);
				
				verifyCurrentLine();
				
				_currentLine.addCommandsListener(this);
				_currentLine.updateDependencies();
				_colLines.add(_currentLine);
				add(_currentLine);
				
				_coreHandler.linkNodes(_currentLine.getFirstComponent().getAbstractNode(),
						_currentLine.getLastComponent().getAbstractNode());
				
				//_currentLine.repaint();
				_currentLine = null;
				_coreHandler.reset();
				repaint();
			}
		} catch (SameComponentException e) {
			JOptionPane.showMessageDialog(this, "Não podemos " +
					"conectar uma saída a uma entrada do mesmo componente.",
					"Conexão inválida", JOptionPane.ERROR_MESSAGE);
			_currentLine = null;
		} catch (ConnectionAlreadyExist e) {
			JOptionPane.showMessageDialog(this, "Já existe a conexão entre os " +
					"dois componentes.", 
					"Conexão inválida", JOptionPane.ERROR_MESSAGE);
			_currentLine = null;
		} catch (CantConnectToInputException e) {
			JOptionPane.showMessageDialog(this, "Uma conexão não pode ser finalizada em uma entrada.", 
					"Conexão inválida", JOptionPane.ERROR_MESSAGE);
			_currentLine = null;
		} catch (CantBeginConnectionInOutputException e) {
			JOptionPane.showMessageDialog(this, "Uma conexão não pode ser iniciada em uma saída.", 
					"Conexão inválida", JOptionPane.ERROR_MESSAGE);
			_currentLine = null;
		} catch (ManyConnectionsInMemoryException e) {
			JOptionPane.showMessageDialog(this, "Em uma memória não pode haver mais que duas entradas.", 
					"Conexão inválida", JOptionPane.ERROR_MESSAGE);
			_currentLine = null;
		}
	}
	
	private void verifyCurrentLine() throws SameComponentException,
			ConnectionAlreadyExist, CantConnectToInputException,
			CantBeginConnectionInOutputException, ManyConnectionsInMemoryException {
		
		verifySameComponent();
		verifyAlreadyConnectionExist();
		verifyInputConnection();
		verifyOutputConnection();
		verifyManyMemoryConnections();
		
	}
	
	private void verifyManyMemoryConnections() throws ManyConnectionsInMemoryException {
		if(_currentLine.getLastComponent() instanceof Memory) {
			Iterator iterator = _colLines.iterator();
			while(iterator.hasNext()) {
				Line line = (Line) iterator.next();
				if(_currentLine.getLastComponent() == line.getLastComponent()) {
					throw new ManyConnectionsInMemoryException();
				}
			}
		}
	}
	
	private void verifyOutputConnection() throws CantBeginConnectionInOutputException {
		if(_currentLine.getFirstComponent() instanceof Output) {
			throw new CantBeginConnectionInOutputException();
		}
	}

	private void verifyInputConnection() throws CantConnectToInputException {
		if(_currentLine.getLastComponent() instanceof Input) {
			throw new CantConnectToInputException();
		}
	}

	private void verifyAlreadyConnectionExist() throws ConnectionAlreadyExist {
		Vector vctLines = new Vector(_colLines);
		
		for(int i = 0; i < vctLines.size(); i++) {
			Line line = (Line) vctLines.get(i);
			
			if(line.getFirstComponent() == _currentLine.getFirstComponent() &&
					line.getLastComponent() == _currentLine.getLastComponent()) {
				throw new ConnectionAlreadyExist();
			}
		}
	}

	private void verifySameComponent() throws SameComponentException {
		if(_currentLine.getFirstComponent() == _currentLine.getLastComponent()) {
			throw new SameComponentException();
		}
	}

	/** (non-Javadoc)
	 * @see org.tools4j.jviterbi.gui.CommandslListener#removeComponent(javax.swing.JComponent)
	 */
	public void removeComponent(BaseComponent component) {
		remove(component);
		
		if(component instanceof Line) {
			_colLines.remove(component);
			
			Line line = (Line) component;
			_coreHandler.removeLink(line.getFirstComponent().getAbstractNode(),
					line.getLastComponent().getAbstractNode());
		} else {
			_coreHandler.removeNode(component.getAbstractNode());
		}
		
		_coreHandler.reset();
		repaint();
	}
	
	public void stateChanged() {
		repaint();
	}

	public void changeInputValue() {
		String sValue = JOptionPane.showInputDialog(this, "Word:");
		Vector vctInputs = _coreHandler.getInputs();
		
		sValue = sValue.trim();
		if(sValue.length() != vctInputs.size()) {
			int iSize = vctInputs.size();
			JOptionPane.showMessageDialog(this, 
					"We have " + iSize + " inputs, so need to enter with " + iSize + " digits!",
					"Invalid word", JOptionPane.ERROR_MESSAGE);
		} else {
			for(int i = 0; i < vctInputs.size(); i++) {
				InputNode inputNode = (InputNode) vctInputs.get(i);
				
				boolean bNextStep = sValue.charAt(i) == '0' ? false : true;
				
				inputNode.step(bNextStep);
			}
			
			repaint();
		}
	}

	public void createConstalation() {
		ConstalationFrame frame = new ConstalationFrame(_coreHandler);
		frame.setLocationRelativeTo(this);
		frame.setVisible(true);
	}
}
