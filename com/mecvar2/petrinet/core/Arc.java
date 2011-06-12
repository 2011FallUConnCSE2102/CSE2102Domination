/*
 * Created on 21/05/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import com.mecvar2.petrinet.visual.ComponentListener;
import com.mecvar2.petrinet.visual.PetriNetworkDraw;
import com.mecvar2.petrinet.visual.PetriToolBarListener;

/**
 * @author marcio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Arc extends Component {
	public static final boolean IS_PLACE = true;
	public static final boolean IS_TRANSITION = false;
	
	private Place m_place = null;
	private Transition m_transition = null;
	private int m_iWeight = 1;
	private boolean m_bFirstObjectType;
//	private Graphics m_graphicsRef;
	
	private double x1 = 0;
	private double y1 = 0;
	private double x2 = 0;
	private double y2 = 0;
	private Point2D midlePoint = null;
	private ComponentListener m_componentListener = null;

	public Arc () {
	}
	
	public MouseListener getMouseListener() {
		return new MyMouseListener();
	}
	
	private class MyMouseListener extends MouseAdapter {
		private boolean hold = false;		
		
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() >= 2
				&& e.getButton() == MouseEvent.BUTTON1
				&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT)
			{
				if(Arc.this.isNear(e.getPoint())) {
					boolean ok = false;
					while(!ok) {
						Component c = null;
						if(m_bFirstObjectType == IS_PLACE) {
							c = m_place;
						}
						if(m_bFirstObjectType == IS_TRANSITION) {
							c = m_transition;
						}
						String sWeight = JOptionPane.showInputDialog(c, "Weight: ", new Integer(m_iWeight));
						if(sWeight != null) {
							try {
								m_iWeight = Integer.parseInt(sWeight);
								ok = true;
								Arc.this.repaint();
							} catch(NumberFormatException exc) {
								JOptionPane.showMessageDialog(c, "Please, enter an integer number!");
							}
						} else {
							ok = true;
						}
					}
				}
			}
		}
		
		public void mousePressed(MouseEvent e) {
			if(Arc.this.isNear(e.getPoint())
				&& e.getButton() == MouseEvent.BUTTON1
				&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT)
			{
				hold = true;
			}

			if(Arc.this.isNear(e.getPoint())
				&& e.getButton() == MouseEvent.BUTTON1
				&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_DELETE)
			{
				removeMyself();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if(hold) {
				if(!Arc.this.isNear(e.getPoint())) {
					midlePoint = e.getPoint();
				}
				hold = false;
				Arc.this.repaint();
			}
		}
	}
	
	private boolean isNear(Point2D point) {
		if(midlePoint == null) {
			Line2D line = new Line2D.Double(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
			if(line.ptSegDist(point) <= 3) {
				return true;
			} else {
				return false;
			}
		} else {
			Line2D line1 = new Line2D.Double(new Point2D.Double(x1, y1), midlePoint);
			Line2D line2 = new Line2D.Double(midlePoint, new Point2D.Double(x2, y2));
			if((line1.ptSegDist(point) <= 3) || (line2.ptSegDist(point) <= 3)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public Place getPlace() {
		return m_place;
	}

	public void setPlace(Place place) {
		this.m_place = place;
		
		if (m_transition == null)
		{
			m_bFirstObjectType = IS_PLACE;			
		}
	}

	public Transition getTransition() {
		return m_transition;
	}

	public void setTrasition(Transition transition) {
		this.m_transition = transition;

		if (m_place == null)
		{
			m_bFirstObjectType = IS_TRANSITION;			
		}
	}

	/**
	 * 
	 */
	private void calculateLine() {
		if(m_bFirstObjectType == Arc.IS_PLACE) {
			Point center1 = m_place.getCenter();
			x1 = m_place.getX() + center1.getX();
			y1 = m_place.getY() + center1.getY();
			Point center2 = m_transition.getCenter();
			x2 = m_transition.getX() + center2.getX();
			y2 = m_transition.getY() + center2.getY();
		}
		if(m_bFirstObjectType == Arc.IS_TRANSITION) {
			Point center1 = m_transition.getCenter();
			x1 = m_transition.getX() + center1.getX();
			y1 = m_transition.getY() + center1.getY();
			Point center2 = m_place.getCenter();
			x2 = m_place.getX() + center2.getX();
			y2 = m_place.getY() + center2.getY();
		}

		Point2D p1;
		Point2D p2;
		if(midlePoint == null) {
			p2 = adjustLine(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
			p1 = adjustLine(p2, new Point2D.Double(x1, y1));
		} else {
			p1 = adjustLine(midlePoint, new Point2D.Double(x1, y1));
			p2 = adjustLine(midlePoint, new Point2D.Double(x2, y2));
		}
		
		x1 = p1.getX();
		y1 = p1.getY();
		x2 = p2.getX();
		y2 = p2.getY();
		
	}
	
	public Point2D adjustLine(Point2D pi, Point2D pf) {
		// Adjusts x2, y2
		double a = pf.getX() - pi.getX();
		double h = pf.distance(pi);
		double hl = 0;
		if(m_bFirstObjectType == Arc.IS_PLACE) {
			hl = m_transition.getRadius();
		}
		if(m_bFirstObjectType == Arc.IS_TRANSITION) {
			hl = m_place.getRadius();
		}
		double al = a * hl / h;
		
		double b = pf.getY() - pi.getY();
		double g = pf.distance(pi);
		double gl = 0;
		if(m_bFirstObjectType == Arc.IS_PLACE) {
			gl = m_transition.getRadius();
		}
		if(m_bFirstObjectType == Arc.IS_TRANSITION) {
			gl = m_place.getRadius();
		}
		double bl = b * gl / g;

		return new Point2D.Double(pf.getX() - al, pf.getY() - bl);
	}

	public boolean canEnable() {
		if (m_place.getToken() != 0 && m_place.getToken() >= m_iWeight)
		{
			return true;
		}
		
		return false;
	}

	public int getWeight() {
		return m_iWeight;
	}

	public void setWeight(int iWeight) {
		this.m_iWeight = iWeight;
	}
	
	public boolean isPlaceSeted() {
		return (m_place != null);
	}

	public boolean isTransitionSeted() {
		return (m_transition != null);
	}
	
	public boolean getFistObjectType() {
		return m_bFirstObjectType;
	}
	
	public void fire() {
		if (m_bFirstObjectType == IS_PLACE)
		{
			m_place.decTokens(m_iWeight);			
		}
		else
		{
			m_place.incTokens(m_iWeight);
		}
	}
	
	public void preFire() {
		if (m_bFirstObjectType == IS_PLACE)
		{
			m_place.decTokens(m_iWeight);			
		}
	}
	
	public void posFire() {
		if (m_bFirstObjectType == IS_TRANSITION)
		{
			m_place.incTokens(m_iWeight);
		}
	}
	
	public void paint(Graphics g1) {
		g1.setColor(Color.GRAY);
		if(m_transition != null && m_place != null) {
			calculateLine();

			Graphics2D g = (Graphics2D) g1;
			
			AffineTransform oldTransform = g.getTransform();
			
			Point2D p1 = new Point2D.Double(x1, y1);
			Point2D p2 = new Point2D.Double(x2, y2);
			
			if(midlePoint != null) {
				Line2D firstLine = new Line2D.Double(p1, midlePoint);
				g.draw(firstLine);
				p1 = midlePoint;
			}

			Line2D line = new Line2D.Double( 0 , 0 , p2.getX() - p1.getX() , p2.getY() - p1.getY() );
	
			double theta = Math.atan2( p2.getX() - p1.getX() , p2.getY() - p1.getY() );
	
			AffineTransform arrowHeadTrans = new AffineTransform( );
			arrowHeadTrans.translate( p2.getX() - p1.getX() , p2.getY() - p1.getY() );
			arrowHeadTrans.rotate( -theta );
	
			Line2D arrowL = new Line2D.Double( 0 , 0 ,  4.0 , -8.0 );
			Line2D arrowR = new Line2D.Double( 0 , 0 , -4.0 , -8.0 );
	
			Shape s = arrowHeadTrans.createTransformedShape( arrowL );
			Shape s2 = arrowHeadTrans.createTransformedShape( arrowR );
	
			AffineTransform newTransform = new AffineTransform( oldTransform );
			newTransform.translate( p1.getX() , p1.getY() );
			g.setTransform( newTransform );
	
			g.draw( line );
			g.draw( s );
			g.draw( s2 );

			g.setTransform( oldTransform );
			
			if(m_iWeight != 1) {
				if(midlePoint != null) {
					g.drawString(Integer.toString(m_iWeight), new Double(midlePoint.getX()).floatValue(), new Double(midlePoint.getY()).floatValue());
				} else {
					Point2D stringPoint = new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 2,  p1.getY() + (p2.getY() - p1.getY()) / 2);
					g.drawString(Integer.toString(m_iWeight), new Double(stringPoint.getX()).floatValue(), new Double(stringPoint.getY()).floatValue());
				}
			}
		}
	}

	public boolean compareTo (Arc thisArc)	{
		return (thisArc.getFistObjectType() == m_bFirstObjectType &&
			thisArc.getPlace() == m_place &&
			thisArc.getTransition() == m_transition);	
	}
	
	public void incWeight (int iIncrement) {
		if (iIncrement > 0)
		{
			m_iWeight += iIncrement;
		}
	}
	
	public void removeMyself () {
		m_place.removeMe(this);
		m_transition.removeMe(this);
		m_componentListener.removeMe(this);
		
		m_place = null;
		m_transition = null;
		
		//TODO: Remover este componente da memória
	}
	
	public void removeMe () {
		removeMyself();
	}

	public void addComopenteListener(ComponentListener listener) {
		m_componentListener = listener;
	}
}
