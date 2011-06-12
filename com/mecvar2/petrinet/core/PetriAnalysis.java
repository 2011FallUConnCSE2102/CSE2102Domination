/*
 * Created on Jul 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.core;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.mecvar2.petrinet.visual.PetriLogPane;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriAnalysis
{
	private Vector m_vctPlace;
	private Vector m_vctTransition;
	private Vector m_vctArc;
	private PetriLogPane m_petriLogPane;
	private int m_iPlacesCount;	
	private int m_iTransitionsCount;
	private int m_iArcsCount;
	private PetriMatrix m_matrixDMinus;
	private PetriMatrix m_matrixDPlus;
	private PetriMatrix m_matrixD;
	
	public PetriAnalysis (Vector vctPlace, Vector vctTransition, Vector vctArc, PetriLogPane petriLogPane) {
		m_vctPlace = vctPlace;
		m_vctTransition = vctTransition;
		m_vctArc = vctArc;
		
		m_iPlacesCount = m_vctPlace.size();
		m_iTransitionsCount = m_vctTransition.size();
		m_iArcsCount = m_vctArc.size();
		
		m_matrixDMinus = new PetriMatrix (m_iTransitionsCount, m_iPlacesCount); 
		m_matrixDPlus = new PetriMatrix (m_iTransitionsCount, m_iPlacesCount);
		m_matrixD = new PetriMatrix (m_iTransitionsCount, m_iPlacesCount);
		
		m_petriLogPane = petriLogPane;
		
	}
	
	public DefaultMutableTreeNode buildTree (PetriMatrix inicialMarking) {
		buildMatrixs();
		
		PetriMarking initialMarking = new PetriMarking (inicialMarking, m_matrixDMinus, m_matrixD);
		
		return (initialMarking.getNode());
	}
	
	private void buildMatrixs(){
		calculateDMatrix();
	}
	
	private void fillDMinusMatrix() {
		Place thisPlace = null;
		Transition thisTransition = null;
		
		m_petriLogPane.append("D- Matrix:\n");
		
		for (int t = 0; t < m_iTransitionsCount; t++)
		{
			thisTransition = (Transition) m_vctTransition.get(t);
			
			for (int p = 0; p < m_iPlacesCount; p++)
			{
				thisPlace = (Place) m_vctPlace.get(p);
				Arc thisArc = thisPlace.getArcToTransition(thisTransition);
				if (thisArc != null)// && thisArc.getFistObjectType() == Arc.IS_PLACE)
				{
					m_matrixDMinus.incElement(t, p, thisArc.getWeight());
				}
			}
		}
		
		showDMinusMatrix();

		m_petriLogPane.append("\n\n");
	}
	
	private void showDMinusMatrix() {
		String strMatrix = new String();
		
		for (int t = 0; t < m_iTransitionsCount; t++)
		{
			for (int p = 0; p < m_iPlacesCount; p++)
			{
				if (p == 0)
					strMatrix += new String ("\t[ ");
					
				String strNumber = String.valueOf(m_matrixDMinus.getElement(t, p));
				while (strNumber.length() < 4)
					strNumber = " " + strNumber;
					
				strMatrix += strNumber;
			}
			
			strMatrix += new String (" ]\n");
		}
		
		m_petriLogPane.append(strMatrix + "\n");
	}

	private void fillDPlusMatrix() {
		Place thisPlace = null;
		Transition thisTransition = null;
		
		m_petriLogPane.append("D+ Matrix:\n");
		
		for (int t = 0; t < m_iTransitionsCount; t++)
		{
			thisTransition = (Transition) m_vctTransition.get(t);
			
			for (int p = 0; p < m_iPlacesCount; p++)
			{
				thisPlace = (Place) m_vctPlace.get(p);
				Arc thisArc = thisTransition.getArcToPlace(thisPlace);
				if (thisArc != null)// && thisArc.getFistObjectType() == Arc.IS_TRANSITION)
				{
					m_matrixDPlus.incElement(t, p, thisArc.getWeight());
				}
			}
		}
		
		showDPlusMatrix();

		m_petriLogPane.append("\n\n");
	}
	
	private void showDPlusMatrix() {
		String strMatrix = new String();
		
		for (int t = 0; t < m_iTransitionsCount; t++)
		{
			for (int p = 0; p < m_iPlacesCount; p++)
			{
				if (p == 0)
					strMatrix += new String ("\t[ ");
					
				String strNumber = String.valueOf(m_matrixDPlus.getElement(t, p));
				while (strNumber.length() < 4)
					strNumber = " " + strNumber;
					
				strMatrix += strNumber;
			}
			
			strMatrix += new String (" ]\n");
		}
		
		m_petriLogPane.append(strMatrix + "\n");
	}
	
	public void calculateDMatrix() {
		m_petriLogPane.setText("");
		fillDMinusMatrix();
		fillDPlusMatrix();
		
		m_petriLogPane.append("D Matrix\n");

		m_matrixD = PetriMatrix.subMatrixs(m_matrixDPlus, m_matrixDMinus);

		if (m_matrixD != null)
			showDMatrix();
		else
			m_petriLogPane.append("Error -> D Matrix\n\n");

		m_petriLogPane.append("\n\n");
	}
	
	public void showDMatrix() {
		String strMatrix = new String();

		for (int t = 0; t < m_iTransitionsCount; t++)
		{
			for (int p = 0; p < m_iPlacesCount; p++)
			{
				if (p == 0)
					strMatrix += new String ("\t[ ");
					
				String strNumber = String.valueOf(m_matrixD.getElement(t, p));
				while (strNumber.length() < 4)
					strNumber = " " + strNumber;
					
				strMatrix += strNumber;
			}
			
			strMatrix += new String (" ]\n");
		}
		
		m_petriLogPane.append(strMatrix + "\n");
	}
}
