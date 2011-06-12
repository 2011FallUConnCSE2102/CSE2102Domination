package jpetrinet.Sources.Prototypes.PlacePrototype.src;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
*
* File:          Label.java
* Creation date: Mar 24, 2004
* Author:        Artur Luís Ribas Barbosa
*               
* Purpose:       Declaration of class Label
*
* Copyright 2003, INATEL Centro de Competência em Telecomunicações
* All rights are reserved. Reproduction in whole or part is
* prohibited without the written consent of the copyright owner.
*
*/

/**
 * @author Artur Luís Ribas Barbosa
 *
 * Classe responsável por ...
 */
public class Label extends ComponentMovement
{
	private String m_sName = null;
	
	public Label(String sName)
	{
		setName(sName);
		m_sName = sName;
	}
	
	/* (non-Javadoc)
	 * @see ComponentMovement#isInner(java.awt.event.MouseEvent)
	 */
	protected boolean isInner(MouseEvent mouseEvent)
	{
		// Label have the exact size
		return true;
	}
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	 
	protected void paintComponent(Graphics arg0)
	{
		arg0.drawString(m_sName, 0, getHeight());
		
		// TODO: The component size is bigger than is need
//		/*Read TODO up*/ arg0.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		super.paintComponent(arg0);
	}
}
