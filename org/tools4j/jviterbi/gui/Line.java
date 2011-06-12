/*
 * Created on 16/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Line extends BaseComponent implements AncestorListener {
	
	private static final long serialVersionUID = -2382574334734662448L;
	private BaseComponent _firstComponent = null;
	private BaseComponent _lastComponent = null;

	public Line() {
		setMoveble(false);
		setConnectionsCanBeVisible(false);
	}
	
	protected void draw(Graphics graphics) {
		
		Vector vctConnections = new Vector(getConnections());

		for(int i = 0; i < vctConnections.size() - 1; i++) {
			Connection connection = (Connection) vctConnections.get(i);
			Point componentPoint = connection.getComponent().getLocation();
			Point point1 = new Point(connection.getPoint());
			point1.translate(componentPoint.x - getLocation().x, componentPoint.y - getLocation().y);
			
			
			connection = (Connection) vctConnections.get(i + 1);
			componentPoint = connection.getComponent().getLocation();
			Point point2 = new Point(connection.getPoint());
			point2.translate(componentPoint.x - getLocation().x, componentPoint.y - getLocation().y);
			
			graphics.drawLine(point1.x, point1.y, point2.x, point2.y);

			if(i == vctConnections.size() - 2) {
				drawArrow(graphics, point1, point2);
			}
		}
	}
	
	private void drawArrow(Graphics graphics, Point point1, Point point2) {
		Graphics2D g = (Graphics2D) graphics;
		
		AffineTransform oldTransform = g.getTransform();
		
		Point2D p1 = new Point2D.Double(point1.x, point1.y);
		Point2D p2 = new Point2D.Double(point2.x, point2.y);
		
		double theta = Math.atan2( p2.getX() - p1.getX() , p2.getY() - p1.getY() );

		AffineTransform arrowHeadTrans = new AffineTransform( );
		arrowHeadTrans.translate( p2.getX() - p1.getX() , p2.getY() - p1.getY() );
		
		arrowHeadTrans.rotate(-theta);

		Polygon polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(-2, -6);
		polygon.addPoint(2, -6);

		Shape s = arrowHeadTrans.createTransformedShape( polygon );

		AffineTransform newTransform = new AffineTransform( oldTransform );
		newTransform.translate( p1.getX() , p1.getY() );
		g.setTransform( newTransform );

		g.draw( s );
		g.fill(s);

		g.setTransform( oldTransform );
	}

	private void updateComponent() {
		int iMinX = 0;
		int iMinY = 0;
		int iMaxX = 0;
		int iMaxY = 0;
		
		Iterator iterator = getConnections().iterator();
		while(iterator.hasNext()) {
			Connection connection = (Connection) iterator.next();
			
			if(iMinX == 0 || iMinX > connection.getPoint().x + connection.getComponent().getLocation().x) {
				iMinX = connection.getPoint().x + connection.getComponent().getLocation().x;
			}
			
			if(iMinY == 0 || iMinY > connection.getPoint().y + connection.getComponent().getLocation().y) {
				iMinY = connection.getPoint().y + connection.getComponent().getLocation().y;
			}

			if(iMaxX == 0 || iMaxX < connection.getPoint().x + connection.getComponent().getLocation().x) {
				iMaxX = connection.getPoint().x + connection.getComponent().getLocation().x;
			}
			
			if(iMaxY == 0 || iMaxY < connection.getPoint().y + connection.getComponent().getLocation().y) {
				iMaxY = connection.getPoint().y + connection.getComponent().getLocation().y;
			}
		}
		
		iMinX -= Connection.CONNECTION_BOUND;
		iMinY -= Connection.CONNECTION_BOUND;
		iMaxX += Connection.CONNECTION_BOUND;
		iMaxY += Connection.CONNECTION_BOUND;
		
		setLocation(iMinX, iMinY);
		setSize(iMaxX - iMinX, iMaxY - iMinY);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
	 */
	public void ancestorAdded(AncestorEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
	 */
	public void ancestorRemoved(AncestorEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
	 */
	public void ancestorMoved(AncestorEvent event) {
		updateComponent();
	}
	
	public void setFirstConnection(Connection connection) {
		super.addConnection(connection);
		
		_firstComponent = connection.getComponent();
		_firstComponent.addAncestorListener(this);
		
		updateComponent();
	}
	
	public BaseComponent getFirstComponent() {
		return _firstComponent;
	}
	
	public void setLastConnection(Connection connection) {
		super.addConnection(connection);
		
		_lastComponent = connection.getComponent();
		_lastComponent.addAncestorListener(this);
		
		updateComponent();
	}
	
	public BaseComponent getLastComponent() {
		return _lastComponent;
	}
	
	public void updateDependencies() {
		_firstComponent.addDependentComponent(this);
		_lastComponent.addDependentComponent(this);
		
		addDependentComponent(_firstComponent);
		addDependentComponent(_lastComponent);
	}
}
