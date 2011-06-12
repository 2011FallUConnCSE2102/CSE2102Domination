/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Graphics;
import java.awt.Point;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Adder extends BaseComponent {
	
	public Adder() {
		setSize(27, 27);
		
		// Page Start
		addConnection(new Connection(new Point((getWidth() - 1) / 2, Connection.CONNECTION_BOUND), this));
		// Line Start
		addConnection(new Connection(new Point(Connection.CONNECTION_BOUND, (getHeight() - 1) / 2), this));
		// Page End
		addConnection(new Connection(new Point((getWidth() - 1) / 2,
				getHeight() - Connection.CONNECTION_BOUND - 1), this));
		// Line End
		addConnection(new Connection(new Point(getWidth()
				- Connection.CONNECTION_BOUND - 1, (getHeight() - 1) / 2), this));
	}

	/** (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void draw(Graphics g) {
		g.drawOval(Connection.CONNECTION_BOUND, Connection.CONNECTION_BOUND,
				getWidth() - (2 * Connection.CONNECTION_BOUND) - 1, getHeight()
						- (2 * Connection.CONNECTION_BOUND) - 1);
		
		g.drawLine((int) Math.round((getWidth() - 1) / 3.0), (int) Math.round((getHeight() - 1) / 2.0), 
				(int) Math.round((getWidth() - 1) / 3.0 * 2), (int) Math.round((getHeight() - 1) / 2.0));
		g.drawLine((int) Math.round((getWidth() - 1) / 2.0), (int) Math.round((getHeight() - 1) / 3.0), 
				(int) Math.round((getWidth() - 1) / 2.0), (int) Math.round((getHeight() - 1) / 3.0 * 2)); 
	}
}
