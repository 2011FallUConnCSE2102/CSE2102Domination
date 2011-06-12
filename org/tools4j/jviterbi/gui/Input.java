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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Input extends BaseComponent {
	
	private final int XOFFSET = 25;
	private final int YOFFSET = 15;
	
	private static int _iIdGenerator = 1;
	private int _iId = _iIdGenerator++; 
	
	public Input() {
		setSize(35 + XOFFSET, 7 + YOFFSET);
		
		addConnection(new Connection(new Point(18 + XOFFSET, 3 + YOFFSET), this));
	}
	
	/** (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void draw(Graphics g) {
		g.drawLine(0 + XOFFSET, 3 + YOFFSET, 9 + XOFFSET, 3 + YOFFSET);
		
		Polygon polygon = new Polygon();
		polygon.addPoint(9 + XOFFSET, 1 + YOFFSET);
		polygon.addPoint(15 + XOFFSET, 3 + YOFFSET);
		polygon.addPoint(9 + XOFFSET, 5 + YOFFSET);
		
		g.drawPolygon(polygon);
		g.fillPolygon(polygon);
		
		g.drawOval(16 + XOFFSET, 1 + YOFFSET, 4, 4);
		
		g.drawString("u" + _iId + " = " + getAbstractNode().getState(), 0, 9);
	}
}
