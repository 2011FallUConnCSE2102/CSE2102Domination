/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;



/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CommandslListener {
	
	public void addInput();
	public void addOutput();
	public void addMemory(); 
	public void addAdder();
	public void addLine(Connection connection);
	public void removeComponent(BaseComponent component);
	public void stateChanged();
	public void createConstalation();
	
	/**
	 * Método usado para enviar um pedido de nova entrada de valores para o Input
	 */
	public void changeInputValue();
}
