/*
 * Created on Jun 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;

import com.mecvar2.petrinet.core.Arc;
import com.mecvar2.petrinet.core.PetriAnalysis;
import com.mecvar2.petrinet.core.PetriMatrix;
import com.mecvar2.petrinet.core.Place;
import com.mecvar2.petrinet.core.SimulationListener;
import com.mecvar2.petrinet.core.Transition;
/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriNetworkDraw extends JPanel implements PetriToolBarListener, ComponentListener {

	private Vector m_vctPlace;
	private Vector m_vctTransition;
	private Vector m_vctArc;
	private Arc m_arcNew;
	public static int m_iComponentToInsert = 0;
	private boolean m_bIsFirstPointArc = true;
	private MouseListener m_mouseListener;
	private PetriLogPane m_petriLogPane = null;
	private boolean m_isSimulating = false;
	private PetriTree m_petriTree = null;
	private SimulationListener m_simulationListener = new SimulationListener() {
		public void nextSimulation() {
			if(m_isSimulating) {
				for(int i = 0; i < m_vctTransition.size(); i++) {
					Transition t = (Transition) m_vctTransition.get(i);
					if(t.verifyEnabled()) {
						t.processFire();
					}
				}
			}
		}
	};

	public PetriNetworkDraw (PetriLogPane petriLogPane, PetriTree petriTree) {
//		this.setBounds(0, 0, 2000, 2000);
		this.setSize(2000, 2000);
		this.setPreferredSize(new Dimension(2000, 2000));
		this.setMinimumSize(new Dimension(2000, 2000));
		this.setMaximumSize(new Dimension(2000, 2000));
		m_vctPlace = new Vector ();
		m_vctTransition = new Vector ();
		m_vctArc = new Vector();
		
		m_petriLogPane = petriLogPane;
		m_petriTree = petriTree;
		
		this.setBackground(Color.WHITE);
		this.setLayout(null);

		m_mouseListener = new MouseAdapter () {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseEvent.BUTTON1)
				{
					processMouseClick (mouseEvent);
				}
			}
		};
		
		this.addMouseListener(m_mouseListener);
	}

	private void processMouseClick (MouseEvent mouseEvent)
	{
		switch (m_iComponentToInsert) {
			case IS_SELECT :
				selectAll(false);
							
				break;
				
			case IS_DELETE :
				selectAll(false);
				
				break;
				
			case IS_PLACE :
				addPlace (mouseEvent);
				selectAll(false);
											
				break;
					
			case IS_TRANSITION :
				addTransition (mouseEvent);
				selectAll(false);

				break;
						
			case IS_ARC :
				if (verifyClickForArc (mouseEvent))
				{
					if (m_bIsFirstPointArc)
					{
						m_bIsFirstPointArc = false;
					}
					else
					{
						m_bIsFirstPointArc = true;
						addArc ();								
						selectAll(false);		
					}
				}
				else
				{
					m_bIsFirstPointArc = true;
					selectAll(false);		
				}
						
				break;
		}
	}

	private boolean addPlace (MouseEvent mouseEvent) {
		Place thisPlace = new Place (m_vctPlace.size() + 1);
//		Place thisPlace = new Place (m_iPlaceIndex++);
		thisPlace.addMouseListener(m_mouseListener);
//		this.addMouseMotionListener(thisPlace.getMouseInputAdapter());
		thisPlace.addComopenteListener(this);
		m_vctPlace.add(thisPlace);
		
		add (thisPlace);
		thisPlace.setBounds((int) (mouseEvent.getX() - (thisPlace.getSize().getWidth() / 2)),
			(int) (mouseEvent.getY() - (thisPlace.getSize().getHeight() / 2)),
			(int) thisPlace.getSize().getWidth (), 
			(int) thisPlace.getSize().getHeight());
		
		return true;
	}

	private boolean addTransition (MouseEvent mouseEvent) {
		Transition thisTransition = new Transition (m_vctTransition.size() + 1);
//		Transition thisTransition = new Transition (m_iTransitionIndex++);
		thisTransition.addMouseListener(m_mouseListener);
		thisTransition.setSimulationListener(m_simulationListener);
		thisTransition.addComopenteListener(this);
		m_vctTransition.add(thisTransition);
		
		add (thisTransition);
		thisTransition.setBounds((int) (mouseEvent.getX() - (thisTransition.getSize().getWidth() / 2)),
			(int) (mouseEvent.getY() - (thisTransition.getSize().getHeight() / 2)),
			(int) thisTransition.getSize().getWidth (), 
			(int) thisTransition.getSize().getHeight());
		
		return true;
	}
	
	private void addArc () {
		//m_arcNew.addMouseListener(m_mouseListener);
		
		// Verifica se já existe esse arco
		boolean bFind = false;
		Arc thisArc = null;
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			thisArc = (Arc) m_vctArc.get(i);
			if (thisArc.compareTo(m_arcNew))
			{
				bFind = true;
				break;
			}
		}
		
		if (bFind) 
		{
			thisArc.incWeight(1);
			thisArc.repaint();
		}
		else
		{
			m_arcNew.getPlace().addArc(m_arcNew);
			m_arcNew.getTransition().addArc(m_arcNew);
			m_arcNew.addComopenteListener(this);
			m_vctArc.add(m_arcNew);
			this.addMouseListener(m_arcNew.getMouseListener());

			add (m_arcNew, 0);
			m_arcNew.setBounds(new Rectangle (this.getSize()));
		}
	}
		
	private boolean verifyClickForArc (MouseEvent mouseEvent) {
		if (m_bIsFirstPointArc)
			m_arcNew = new Arc();
			
		// Verifica se é Place
		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Place myPlace = (Place) m_vctPlace.get(i);
			if (myPlace == mouseEvent.getSource())
			{
				if (m_arcNew.isPlaceSeted())
				{
					// TODO: Enviar menssagem no StatusBar de opção invalida 
					return false;
				}
				else
				{
					m_arcNew.setPlace(myPlace);
					return true;
				}					
			}
		}
		
		for (int i = 0; i < m_vctTransition.size(); i++)
		{
			Transition myTransition = (Transition) m_vctTransition.get(i);
			if (myTransition == mouseEvent.getSource())
			{
				if (m_arcNew.isTransitionSeted())
				{
					// TODO: Enviar menssagem no StatusBar de opção invalida 
					return false;
				}
				else
				{
					m_arcNew.setTrasition(myTransition);
					return true;
				}			
			}
		}
		
		return false;
	}
	
	public void selectAll(boolean bSelect) {
		// Verifica se é Place
		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Place myPlace = (Place) m_vctPlace.get(i);
			myPlace.setSelected(bSelect);
		}
		
		for (int i = 0; i < m_vctTransition.size(); i++)
		{
			Transition myTransition = (Transition) m_vctTransition.get(i);
			myTransition.setSelected(bSelect);
		}
	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public Vector getVctPlace() {
		return m_vctPlace;
	}

	public void setVctPlace(Vector vctPlace) {
		this.m_vctPlace = vctPlace;
	}

	public void insertComponent(int iComponent) {
		if (iComponent ==  IS_CLEAR){
			removeAll();
			repaint();
			
			m_vctPlace = new Vector ();
			m_vctTransition = new Vector ();
			m_vctArc = new Vector();
			m_petriLogPane.setText(Editor.TEXT_TITLE);
				
			iComponent = IS_SELECT;
		}
				
		selectAll(false);
		m_bIsFirstPointArc = true;
		m_iComponentToInsert = iComponent;
	}

	/* (non-Javadoc)
	 * @see com.mecvar2.petrinet.visual.PetriToolBarListener#processAnalysis(int)
	 */
	public void processAnalysis(int iAnalysis)
	{
		switch (iAnalysis)
		{
			case PetriToolBarListener.IS_BUILDTREE :
				execBuildTree();
							
				break;
			
			case PetriToolBarListener.IS_DMATRIX :
				execBuildDMatrix();			
			break;
		}
	}
	
	public void execBuildTree () {
		m_petriLogPane.append("\n\n\n **********     Building Tree     ********** \n\n");
		PetriAnalysis petriAnalysis = new PetriAnalysis (m_vctPlace, m_vctTransition, m_vctArc, m_petriLogPane);
		
		PetriMatrix inicialMarking = new PetriMatrix(1, m_vctPlace.size());
		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Place tempPlace = (Place) m_vctPlace.get(i);
			inicialMarking.setElement(0, tempPlace.getID() - 1, tempPlace.getToken());			
		}
		
		m_petriTree.removeAllChildren();
		m_petriTree.add(petriAnalysis.buildTree(inicialMarking));
		Editor.updateTree();
	}
	
	public void execBuildDMatrix () {
		m_petriLogPane.append("\n\n\n **********     Building D Matrix     ********** \n\n");
		PetriAnalysis petriAnalysis = new PetriAnalysis (m_vctPlace, m_vctTransition, m_vctArc, m_petriLogPane);
		petriAnalysis.calculateDMatrix();
	}
	
	public void startSimulation() {
		m_isSimulating = true;
		
		m_simulationListener.nextSimulation();
	}
	
	public void stopSimulation() {
		m_isSimulating = false;
	}

	/* (non-Javadoc)
	 * @see com.mecvar2.petrinet.visual.PetriToolBarListener#processTimming(int)
	 */
	public void processTimming(int iTimming, double rate) {
		switch (iTimming) {
			case PetriToolBarListener.IS_RUN :
				if(!m_isSimulating) {
					startSimulation();
				} else {
					stopSimulation();
				}
				break;
			case PetriToolBarListener.IS_SET_RATE :
					setTransitionsRate(rate);
				break;
		}
	}
	
	public void setTransitionsRate(double rate) {
		for(int i = 0; i < m_vctTransition.size(); i++) {
			((Transition) m_vctTransition.get(i)).setRate(rate);
		}
	}

	/* (non-Javadoc)
	 * @see com.mecvar2.petrinet.visual.ComponentListener#removeMe(com.mecvar2.petrinet.core.Place)
	 */
	public void removeMe(Place place)
	{
		boolean bFind = false;
		int iID = 0;
		
		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Place thisPlace = (Place) m_vctPlace.get(i);
			if ((thisPlace == place) && !bFind)
			{
				iID = thisPlace.getID();
				bFind = true;
				m_vctPlace.remove(i);
				remove(place);
				repaint();
				break;
			}
		}

		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Place thisPlace = (Place) m_vctPlace.get(i);
			if (bFind && thisPlace.getID() > iID)
			{
				thisPlace.setID(thisPlace.getID() - 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mecvar2.petrinet.visual.ComponentListener#removeMe(com.mecvar2.petrinet.core.Transition)
	 */
	public void removeMe(Transition transition)
	{
		boolean bFind = false;
		int iID = 0;
		
		for (int i = 0; i < m_vctTransition.size(); i++)
		{
			Transition thisTransition = (Transition) m_vctTransition.get(i);
			if ((thisTransition == transition) && !bFind)
			{
				iID = thisTransition.getID();
				bFind = true;
				m_vctTransition.remove(i);
				remove(transition);
				repaint();
				break;
			}
		}
		
		for (int i = 0; i < m_vctPlace.size(); i++)
		{
			Transition thisTransition = (Transition) m_vctTransition.get(i);
			if (bFind && thisTransition.getID() > iID)
			{
				thisTransition.setID(thisTransition.getID() - 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mecvar2.petrinet.visual.ComponentListener#removeMe(com.mecvar2.petrinet.core.Arc)
	 */
	public void removeMe(Arc arc)
	{
		for (int i = 0; i < m_vctArc.size(); i++)
		{
			Arc thisArc = (Arc) m_vctArc.get(i);
			if (thisArc == arc)
			{
				m_vctArc.remove(i);
				remove(arc);
				repaint();
				break;
			}
		}
	}	
}
