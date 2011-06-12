/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Output extends BaseComponent {
	
	private final int XOFFSET = 8;
	private final int YOFFSET = 15;
	
	private static int _iIdGenerator = 0;
	private int _iId = _iIdGenerator++; 
	
	public Output() {
		setSize(35 + XOFFSET, 7 + YOFFSET);
		
		addConnection(new Connection(new Point(3, 3 + YOFFSET), this));
	}

	/** (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void draw(Graphics g) {
		g.drawLine(6, 3 + YOFFSET, 14, 3 + YOFFSET);
		
		Polygon polygon = new Polygon();
		polygon.addPoint(15, 1 + YOFFSET);
		polygon.addPoint(21, 3 + YOFFSET);
		polygon.addPoint(15, 5 + YOFFSET);
		
		g.drawPolygon(polygon);
		g.fillPolygon(polygon);
		
		g.drawOval(1, 1 + YOFFSET, 4, 4);
		
		g.drawString("v" + _iId + " = " + getAbstractNode().getState(), 4 + XOFFSET, 9);
	}
}
