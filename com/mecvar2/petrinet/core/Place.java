/*
 * Created on 21/05/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mecvar2.petrinet.visual.ComponentListener;
import com.mecvar2.petrinet.visual.PetriNetworkDraw;
import com.mecvar2.petrinet.visual.PetriToolBarListener;
/**
 * @author marcio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Place extends JPanel {

	private static final Dimension m_dimension = new Dimension (30, 30);
	private Vector m_vctArc;
	private JLabel m_jlObject;
	private JLabel m_jlName;
	private int m_iTokens = 0;
	private boolean m_bSelected = false;
	private int m_iID;
	private ComponentListener m_componentListener = null;
	private boolean m_bHold = false;
//	private MouseInputAdapter m_inputAdapter;
	
	public Place (int iID){
		setBackground(Color.WHITE);
		m_vctArc = new Vector();
		m_iID = iID;
		
		m_jlObject = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (m_bSelected)
					setForeground(Color.BLUE);
				else
					setForeground(Color.BLACK);
								
				g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
				
				if (m_iTokens > 0)
				{
					setText(String.valueOf(m_iTokens));
				}
				else
				{
					setText("");
				}
			}
		};

		m_jlObject.setSize(m_dimension.width, m_dimension.height);
		m_jlObject.setHorizontalAlignment(JLabel.CENTER);
		
		m_jlName = new JLabel("p" + m_iID);
		m_jlName.setSize(m_jlName.getPreferredSize());
		
		setLayout(null);		
		setSize(m_jlName.getWidth() + m_jlObject.getWidth() + 2, 
			m_jlName.getHeight() > m_jlObject.getHeight() ? m_jlName.getHeight() : m_jlObject.getHeight());
		
		add(m_jlName);
		add(m_jlObject);			
		
		m_jlName.setLocation(0, (int) ((getSize().getHeight() - m_jlName.getHeight()) / 2));
		m_jlObject.setLocation((int) m_jlName.getWidth() + 2, (int) ((getSize().getHeight() - m_jlObject.getHeight()) / 2));


		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (mouseEvent.getClickCount() > 1
					&& mouseEvent.getButton() == MouseEvent.BUTTON1
					&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT)
				{
					processToken();
				}
				else if (mouseEvent.getClickCount() == 1
					&& mouseEvent.getButton() == MouseEvent.BUTTON1
					&& (PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT
					|| PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_ARC))
				{
					setSelected (!m_bSelected);
				}					
				else if (mouseEvent.getClickCount() == 1
					&& mouseEvent.getButton() == MouseEvent.BUTTON1
					&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_DELETE)
				{
					removeMyself();
				}


				for (int i = 0; i < m_vctArc.size(); i++)
				{
					Arc arc = (Arc) m_vctArc.get(i);
					arc.repaint();		
				}
			}

			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1
					&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT)
				{
					m_bHold = true;
				}
			}

			public void mouseReleased(MouseEvent e) {
				if(m_bHold) {
					setLocation(Place.this.getX() + e.getPoint().x - (int) getCenter().getX(),
						Place.this.getY() + e.getPoint().y - (int) getCenter().getY());
					m_bHold = false;
					Place.this.getParent().repaint();
				}
				
			}
		});
		
	}
	
	public void setSelected(boolean bSelected)
	{
		m_bSelected = bSelected;

		if (m_bSelected)
		{
			m_jlObject.setForeground(Color.BLUE);
			m_jlName.setForeground(Color.BLUE);
		}
		else
		{
			m_jlObject.setForeground(Color.BLACK);
			m_jlName.setForeground(Color.BLACK);
		}

		repaint();
	}

	private void processToken()
	{
		boolean ok = false;
		while(!ok) {
			String strValue = JOptionPane.showInputDialog(this, "Tokens:", new Integer (m_iTokens));
			if(strValue != null) {
				try {
					m_iTokens = Integer.parseInt(strValue);
					ok = true;
					getParent().repaint();
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(this, "Please, enter an integer number!");
				}
			} else {
				ok = true;
			}
		}
	}

	public Vector getArc() {
		return m_vctArc;
	}

	public void setArc(Vector vctArc) {
		this.m_vctArc = vctArc;
	}

	/**
	 * Return the number of tokens in place
	 */
	public int getToken() {
		return m_iTokens;
	}

	/**
	 * Set the number of tokens in place
	 */
	public void setToken(int iTokens) {
		this.m_iTokens = iTokens;
	}

	/**
	 * Increment with iTokens the number of tokens in place
	 */
	public void incTokens(int iTokens) {
		m_iTokens += iTokens;
	}

	/**
	 * Decrement with iTokens the number of tokens in place
	 */
	public boolean decTokens(int iTokens) {
		if (m_iTokens >= iTokens)
		{
			m_iTokens -= iTokens;
			return true;				
		}
		
		return false; 
	}

	/**
	 * Return the Arc to connect to Transition
	 */
	public Arc getArcToTransition(Transition transition) {
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			Arc thisArc = (Arc) m_vctArc.get(i);
			if (thisArc.getFistObjectType() == Arc.IS_PLACE && thisArc.getTransition().equals(transition))
			{
				return thisArc;	
			}
		}
		
		return null;
	}

	public void paintComponent(Graphics g) {
		m_jlName.setText("p" + m_iID);
		super.paintComponent(g);
//		g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);				
	}

	public boolean contains(Point p)
	{
		return m_jlObject.getBounds().contains(p.x - this.getX(), p.y - this.getY());
	}
	
	public Point getCenter() {
//		return new Point (this.getBounds().x + (int) m_jlObject.getBounds().getCenterX(), 
//			this.getBounds().y + (int) m_jlObject.getBounds().getCenterY());

		return new Point ((int) m_jlObject.getBounds().getCenterX(), 
			(int) m_jlObject.getBounds().getCenterY());
	}
	
	public int getRadius () {
		return (m_dimension.width / 2);
	}

	public void addArc (Arc newArc) {
		m_vctArc.add(newArc);
	}
	
	public int getID() {
		return m_iID;
	}
	
	public void setID(int iID) {
		m_iID = iID;
	}

	public void removeMyself() {
		while (m_vctArc.size() > 0)
		{
			Arc thisArc = (Arc) m_vctArc.firstElement();
			thisArc.removeMe();
		}
		
		m_componentListener.removeMe(this);
		
		m_vctArc = null;
		//TODO: Remover este componente da memória
	}
	
	public void removeMe(Arc arc) {
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			Arc thisArc = (Arc) m_vctArc.get(i);
			if (thisArc == arc)
			{
				m_vctArc.remove(i);		
			}
		}
	}
	
	public void addComopenteListener(ComponentListener listener) {
		m_componentListener = listener;
	}
	
//	public MouseInputAdapter getMouseInputAdapter () {
//		return m_inputAdapter;
//	}
}
