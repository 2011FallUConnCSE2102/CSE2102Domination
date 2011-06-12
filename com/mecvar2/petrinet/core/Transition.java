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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.mecvar2.petrinet.visual.ComponentListener;
import com.mecvar2.petrinet.visual.PetriNetworkDraw;
import com.mecvar2.petrinet.visual.PetriToolBarListener;
/**
 * @author marcio
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Transition extends JPanel {

	private static final Dimension m_dimension = new Dimension (30, 10);
	private Vector m_vctArc;
	private JLabel m_jlObject;
	private JLabel m_jlName;
	private boolean m_bSelected = false;
	private int m_iID;
	private double m_timming = 0;
	private JLabel m_jlTimming = null;
	private boolean m_isTimming = false;
	private SimulationListener m_simulationListener;
	private ComponentListener m_componentListener = null;
	private boolean m_bHold = false;
	private double m_rate = 1;
	
	public Transition (int iID){
		setBackground(Color.WHITE);
		m_vctArc = new Vector ();
		m_iID = iID;
		
		m_jlObject = new JLabel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				
				if (verifyEnabled())
				{
					g.setColor(Color.GREEN);
					g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
				}
				
				if (m_isTimming)
				{
					g.setColor(Color.YELLOW);
					g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
				}
			}
		};
		
		m_jlObject.setSize(m_dimension.width, m_dimension.height);		
		m_jlName = new JLabel("t" + m_iID);
		m_jlName.setSize(m_jlName.getPreferredSize());
//		m_jlTimming = new JLabel("")
		
		setLayout(null);		
		setSize(m_jlName.getWidth() + m_jlObject.getWidth() + 2, 
			m_jlName.getHeight() > m_jlObject.getHeight() ? m_jlName.getHeight() : m_jlObject.getHeight());
		
		add(m_jlName);
		add(m_jlObject);			
		
		m_jlName.setLocation(0, (int) ((getSize().getHeight() - m_jlName.getHeight()) / 2));
		m_jlObject.setLocation((int) m_jlName.getWidth() + 2, (int) ((getSize().getHeight() - m_jlObject.getHeight()) / 2));

		m_jlTimming = new JLabel();
		m_jlTimming.setSize(m_jlTimming.getPreferredSize());
		this.add(m_jlTimming);
		setSize(m_jlName.getWidth() + m_jlObject.getWidth() + m_jlTimming.getWidth() + 4, 
			m_jlName.getHeight() > m_jlObject.getHeight() ? m_jlName.getHeight() : m_jlObject.getHeight());
		m_jlTimming.setLocation((int) m_jlName.getWidth() + m_jlObject.getWidth() + 4,
			(int) ((getSize().getHeight() - m_jlTimming.getHeight()) / 2));

		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent mouseEvent)
			{
				if (// mouseEvent.getClickCount() > 1 &&
					mouseEvent.getButton() == MouseEvent.BUTTON3
					&& verifyEnabled())
				{
					processFire ();
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
				else if (mouseEvent.getClickCount() > 1
					&& mouseEvent.getButton() == MouseEvent.BUTTON1
					&& PetriNetworkDraw.m_iComponentToInsert == PetriToolBarListener.IS_SELECT)
				{
					setTiming();
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
					setLocation(Transition.this.getX() + e.getPoint().x - (int) getCenter().getX(),
						Transition.this.getY() + e.getPoint().y - (int) getCenter().getY());
					m_bHold = false;
					Transition.this.getParent().repaint();
				}
			}
		});
	}
	
	public void setTiming() {
		boolean ok = false;
		while(!ok) {
			String strValue = JOptionPane.showInputDialog(this, "Timming:", new Double (m_timming));
			if(strValue != null) {
				try {
					m_timming = Double.parseDouble(strValue);
					ok = true;
					insertTimmingLabel();
					getParent().repaint();
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(this, "Please, enter a number!");
				}
			} else {
				ok = true;
			}
		}
	}
	
	public void insertTimmingLabel() {
		if(m_timming != 0) {
			//m_jlTimming.setFont(new Font("Times New Roman", Font.BOLD, 12));
			m_jlTimming.setText("\u03c4 = " + m_timming);
		} else {
			m_jlTimming.setText("");
		}
		m_jlTimming.setSize(m_jlTimming.getPreferredSize());
		this.add(m_jlTimming);
		setSize(m_jlName.getWidth() + m_jlObject.getWidth() + m_jlTimming.getWidth() + 4, 
			m_jlName.getHeight() > m_jlObject.getHeight() ? m_jlName.getHeight() : m_jlObject.getHeight());
		m_jlTimming.setLocation((int) m_jlName.getWidth() + m_jlObject.getWidth() + 4,
			(int) ((getSize().getHeight() - m_jlTimming.getHeight()) / 2));
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

	public Vector getArc() {
		return m_vctArc;
	}

	public void setArc(Vector vctArc) {
		this.m_vctArc = vctArc;
	}

	/**
	 * Return the Arc to connect to Place
	 */
	public Arc getArcToPlace(Place place) {
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			Arc thisArc = (Arc) m_vctArc.get(i);
			if (thisArc.getFistObjectType() == Arc.IS_TRANSITION && thisArc.getPlace().equals(place))
			{
				return thisArc;			
			}
		}
		
		return null;
	}

	public void paintComponent(Graphics g) {
		m_jlName.setText("t" + m_iID);
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
	
	public int getRadius() {
		return (m_dimension.width / 2);
	}
	
	public boolean verifyEnabled () {
		boolean bResult = true;
		
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			Arc thisArc = (Arc) m_vctArc.get(i);
			if (thisArc.getFistObjectType() == Arc.IS_PLACE && !thisArc.canEnable())
			{
				return false;
			}
		}

		return bResult;
	}
	
	public void addArc(Arc newArc) {
		m_vctArc.add(newArc);
	}
	
	public void processFire(){
		if(m_isTimming == false) {
			if((m_timming * m_rate) > 0) {
				for (int i = 0; i < m_vctArc.size(); i++)
				{
					Arc thisArc = (Arc) m_vctArc.get(i);
					thisArc.preFire();
				}
	
				m_isTimming = true;
	
				getParent().repaint();
	
				new Timer(new Double((m_timming * m_rate) * 1000).intValue() , new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < m_vctArc.size(); i++)
						{
							Arc thisArc = (Arc) m_vctArc.get(i);
							thisArc.posFire();
						}
			
						m_isTimming = false;
	
						((Timer)e.getSource()).stop();
					
						getParent().repaint();
	
						m_simulationListener.nextSimulation();
					}
				}).start();
			} else {
				for (int i = 0; i < m_vctArc.size(); i++)
				{
					Arc thisArc = (Arc) m_vctArc.get(i);
					thisArc.fire();		
				}
			
				getParent().repaint();
	
				m_simulationListener.nextSimulation();
			}
		}
	}

	public int getID() {
		return m_iID;
	}

	public void setID(int iID) {
		m_iID = iID;
	}
	
	public void setSimulationListener(SimulationListener simulationListener) {
		m_simulationListener = simulationListener;
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
	
	public void setRate(double rate) {
		m_rate = rate;
	}
}
