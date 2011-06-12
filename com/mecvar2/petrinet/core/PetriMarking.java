/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.core;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriMarking
{	
	public static final int IS_BOUNDARY = 0;
	public static final int IS_TERMINAL = 1;
	public static final int IS_DUPLICATE = 2;
	public static final int IS_INTERIORS = 3;
	public static final int IS_ROOT = 4;
	
	private Vector m_vctChildren = null;
	private PetriMarking m_petriMarkingParent = null;
	private PetriMarking m_petriMarkingRoot = null;
	private PetriMatrix m_petriMatrixMarking = null;
	private PetriMatrix m_petriMatrixDMinus = null;
	private PetriMatrix m_petriMatrixD = null;
	private DefaultMutableTreeNode m_treeNode = null;
	private int m_iTransitionID;
	private int m_iStatus;
	private String m_strDuplicateID;
	private String m_strID;

	public PetriMarking(PetriMatrix inicialMarking, PetriMatrix petriMatrixDMinus, PetriMatrix petriMatrixD) {
		m_strID = "0";
		m_petriMatrixMarking = inicialMarking;
		m_petriMatrixDMinus = petriMatrixDMinus;
		m_petriMatrixD = petriMatrixD;
		
		initialize();
		updateStatus();
	}
	
	public PetriMarking(String strID, int iTransitionID, PetriMatrix matrixMarking, PetriMarking petriMarkingRoot, PetriMarking petriMarkingParent, PetriMatrix petriMatrixDMinus, PetriMatrix petriMatrixD) {
		m_strID = strID;
		m_iTransitionID = iTransitionID;
		m_petriMatrixMarking = matrixMarking;
		m_petriMarkingRoot = petriMarkingRoot;
		m_petriMarkingParent = petriMarkingParent;
		m_petriMatrixDMinus = petriMatrixDMinus;
		m_petriMatrixD = petriMatrixD;
		
		initialize();
	}
	
	private void initialize() {
		m_vctChildren = new Vector();
	}
	
	public int updateStatus() {
		// Verifica se é ROOT
		if (m_petriMarkingParent == null && m_petriMarkingRoot == null)
		{
			m_iStatus = IS_ROOT;
			m_treeNode = new DefaultMutableTreeNode(this.toString());
			createChildren();				
		}
		else
		{
			// Verifica se é DUPLICATE
			PetriMarking thisPetriMarking = m_petriMarkingRoot.search(this);
			if (thisPetriMarking != null)
			{
				m_iStatus = IS_DUPLICATE;
				m_strDuplicateID = thisPetriMarking.getID();
				m_treeNode = new DefaultMutableTreeNode(this.toString());
			}
			else
			{
				// Verifica se é TERMINAL
				if (m_petriMatrixDMinus.getTransitionsQualified(this) == null)
				{
					m_iStatus = IS_TERMINAL;				
					m_treeNode = new DefaultMutableTreeNode(this.toString());
				}
				else
				{
					m_treeNode = new DefaultMutableTreeNode(this.toString());
					createChildren();
				}
			}
		}
		
		if (m_iStatus != IS_ROOT)
		{
			m_petriMarkingParent.addNode(m_treeNode);
		}
		
		return m_iStatus;
	}
	
	public PetriMarking search(PetriMarking thisPetriMarking) {
		// Verifica se não é essa a procurada
		if ((thisPetriMarking.getMarking().toString().compareTo(m_petriMatrixMarking.toString()) == 0) && (thisPetriMarking.getID().compareTo(m_strID) != 0))
		{
			return (this);
		}
		
		// Procurando nas filhas
		PetriMarking findPetriMarking = null;
		
		for (int i = 0; i < m_vctChildren.size(); i++)
		{
			findPetriMarking = ((PetriMarking) m_vctChildren.get(i)).search(thisPetriMarking);
//			findPetriMarking = findPetriMarking.search(thisPetriMarking);
			
			if (findPetriMarking != null)
				break;
		}
		
		return findPetriMarking;
	}
	
	private boolean createChildren() {
		m_vctChildren = new Vector();
		Vector vctIDChildren = m_petriMatrixDMinus.getTransitionsQualified(this);
		
		if (vctIDChildren != null)
		{
			for (int i = 0; i < vctIDChildren.size(); i++)
			{
				int iTransitionID = ((Integer) vctIDChildren.get(i)).intValue();
				
				PetriMatrix nextMarking = new PetriMatrix(1, m_petriMatrixMarking.getCols());
				nextMarking = m_petriMatrixD.getNextMarking(m_petriMatrixMarking, iTransitionID);

				// Verifica os W
				if (m_iStatus != IS_ROOT)
				{
				
					nextMarking = m_petriMarkingParent.verifyW(nextMarking);
					
					PetriMarking newMarking = new PetriMarking(m_strID + "." + (i + 1),
						iTransitionID, nextMarking,
						m_petriMarkingRoot, this, m_petriMatrixDMinus, m_petriMatrixD); 
				
					m_vctChildren.add(newMarking);
					newMarking.updateStatus();
				}
				else
				{
					PetriMarking newMarking = new PetriMarking(m_strID + "." + (i + 1),
						iTransitionID, nextMarking,
						this, this, m_petriMatrixDMinus, m_petriMatrixD); 
				
					m_vctChildren.add(newMarking);
					newMarking.updateStatus();
				}
			}
		}
		
		return (m_vctChildren != null);
	}
	
	private PetriMatrix verifyW(PetriMatrix matrixMarking) {
//		PetriMatrix newMarking = new PetriMatrix(1, matrixMarking.getCols());
		PetriMatrix newMarking = matrixMarking;
		
		if (matrixMarking.verifyCovering(m_petriMatrixMarking))
		{
			for (int i = 0; i < matrixMarking.getCols(); i++)
			{
				if (matrixMarking.getElement(0, i) > m_petriMatrixMarking.getElement(0, i))
				{
					newMarking.setElement(0, i, -1);
				}
				else
				{
					newMarking.setElement(0, i, matrixMarking.getElement(0, i));
				}
			}
		}
		
		if (m_iStatus != IS_ROOT)
			newMarking = m_petriMarkingParent.verifyW(newMarking);
		
		return newMarking;
	}
	
	public void addChild(PetriMarking thisPetriMatrix) {
		m_vctChildren.add(thisPetriMatrix);
	}
	
	public String toString() {
		String strResult = new String();
		
		strResult = "(" + m_strID + ") - ";
		
		if (m_iStatus != IS_ROOT) 
			strResult += "t" + (m_iTransitionID + 1) + ": ";
			
		strResult += m_petriMatrixMarking.toString();
		
		switch (m_iStatus)
		{
			case IS_TERMINAL :
				strResult += " / Terminal";
				break;
				
			case IS_DUPLICATE :
				strResult += " / Duplicate (" + m_strDuplicateID + ")";
		}
		
		return strResult;
	}
	
	public void addNode(DefaultMutableTreeNode newNode) {
		m_treeNode.add(newNode);
	}
	
	// Get / Set
	public DefaultMutableTreeNode getNode() {
		return (m_treeNode);
	}
	
	
	public Vector getChildren()
	{
		return m_vctChildren;
	}

	public void setChildren(Vector vctChildren)
	{
		m_vctChildren = vctChildren;
	}

	public PetriMarking getParent()
	{
		return m_petriMarkingParent;
	}

	public void setParent(PetriMarking petriMarkingParent)
	{
		m_petriMarkingParent = petriMarkingParent;
	}

	public PetriMatrix getMarking()
	{
		return m_petriMatrixMarking;
	}

	public void setMarking(PetriMatrix petriMatrixMarking)
	{
		m_petriMatrixMarking = petriMatrixMarking;
	}

	public int getTransition()
	{
		return m_iTransitionID;
	}

	public void setTransition(int iTransitionID)
	{
		m_iTransitionID = iTransitionID;
	}

	public int getStatus()
	{
		return m_iStatus;
	}

	public void setStatus(int iStatus)
	{
		m_iStatus = iStatus;
	}
	
	public String getID()
	{
		return m_strID;
	}

	public void setID(String strID)
	{
		m_strID = strID;
	}
}
