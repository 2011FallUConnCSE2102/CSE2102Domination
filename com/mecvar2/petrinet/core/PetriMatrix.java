/*
 * Created on Jul 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.core;

import java.util.Vector;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriMatrix
{
	private int m_iRowCount;
	private int m_iColCount;
	private int m_iMatrix[][];
	
	public PetriMatrix(int iRow, int iCol) {
		m_iRowCount = iRow;
		m_iColCount = iCol;
		
		m_iMatrix = new int[iRow][iCol];
		fillWithZero();
	}
	
	public int getRows() {
		return m_iRowCount;
	}

	public int getCols() {
		return m_iColCount;
	}
	
	public void fillWithZero(){
		for (int r = 0; r < m_iRowCount; r++)
		{
			for (int c = 0; c < m_iColCount; c++)
			{
				m_iMatrix[r][c] = 0;
			}
		}
	}
	
	public void setElement(int iRow, int iCol, int iElement){
		m_iMatrix[iRow][iCol] = iElement;
	}
	
	public void incElement(int iRow, int iCol, int iIncrement) {
		m_iMatrix[iRow][iCol] += iIncrement; 	
	}
	
	public int getElement(int iRow, int iCol) {
		return m_iMatrix[iRow][iCol];
	}
	
	public static PetriMatrix subMatrixs(PetriMatrix matrix1, PetriMatrix matrix2) {
		PetriMatrix matrixResult = null;
		
		if (matrix1.getRows() == matrix2.getRows() && matrix1.getCols() == matrix2.getCols())
		{
			matrixResult = new PetriMatrix(matrix1.getRows(), matrix1.getCols());
			for (int r = 0; r < matrix1.getRows(); r++)
			{
				for (int c = 0; c < matrix1.getCols(); c++)
				{
					matrixResult.setElement(r, c, matrix1.getElement(r, c) - matrix2.getElement(r, c));
				}
			}
		}
		
		return matrixResult;		
	}

	public static PetriMatrix sumMatrixs(PetriMatrix matrix1, PetriMatrix matrix2) {
		PetriMatrix matrixResult = null;
		
		if (matrix1.getRows() == matrix2.getRows() && matrix1.getCols() == matrix2.getCols())
		{
			matrixResult = new PetriMatrix(matrix1.getRows(), matrix1.getCols());
			for (int r = 0; r < matrix1.getRows(); r++)
			{
				for (int c = 0; c < matrix1.getCols(); c++)
				{
					matrixResult.setElement(r, c, matrix1.getElement(r, c) + matrix2.getElement(r, c));
				}
			}
		}
		
		return matrixResult;		
	}

	public boolean verifyCovering(PetriMatrix thisMatrix) {
		if (thisMatrix.getRows() == m_iRowCount && thisMatrix.getCols() == m_iColCount && m_iRowCount == 1)
		{
			for (int i = 0; i < m_iColCount; i++)
			{
				if (m_iMatrix[0][i] != -1 && m_iMatrix[0][i] < thisMatrix.getElement(0, i))
					return false;				
			}
		}
		
		return true;		
	}
	
	public PetriMatrix getRowElements(int iRow) {
		PetriMatrix petriMatrixResult = null;
		
		if (iRow <= m_iRowCount)
		{
			petriMatrixResult = new PetriMatrix(1, m_iColCount);
			for (int i = 0; i < m_iColCount; i++)
			{
				petriMatrixResult.setElement(0, i, m_iMatrix[iRow][i]);
			}
		}
		
		return petriMatrixResult;
	}
	
	public Vector getTransitionsQualified(PetriMarking petriMarking) {
		Vector vctResult = null;
		PetriMatrix matrixMarking = petriMarking.getMarking();
		
		if (matrixMarking.getCols() == m_iColCount)
		{
			for (int i = 0; i < m_iRowCount; i++)
			{
				PetriMatrix matrixRow = getRowElements(i);
				if (matrixMarking.verifyCovering(matrixRow))
				{
					if (vctResult == null)
						vctResult = new Vector();
						
					vctResult.add(new Integer(i));
				}
			}
		}
		
		return vctResult;		
	}

	public PetriMatrix getNextMarking(PetriMatrix petriMatrixMarking, int iTransitionID)
	{
		PetriMatrix nextMarking = new PetriMatrix(1, petriMatrixMarking.getCols());
		
		nextMarking = sumMatrixs(petriMatrixMarking, getRowElements(iTransitionID));
		// Verifica se tem w
		for (int i = 0; i < petriMatrixMarking.getCols(); i++)
		{
			if (petriMatrixMarking.getElement(0, i) < 0)
			{
				nextMarking.setElement(0, i, -1);		
			}
		}
		
		return (nextMarking);
	}
	
	public String toString() {
		String strResult = new String();
		
		for (int r = 0; r < m_iRowCount; r++)
		{
			if (r > 0)
				strResult += " ";
				
			strResult += "[ ";
			for (int c = 0; c < m_iColCount; c++)
			{
				if (c > 0)
					strResult += ", ";
					
				if (m_iMatrix[r][c] < 0)
					strResult += "w";
				else
					strResult += String.valueOf(m_iMatrix[r][c]);								
			}
			strResult += " ]";
		}
		
		return strResult;
	}
}