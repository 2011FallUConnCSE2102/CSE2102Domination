package jpetrinet.Sources.Prototypes.PlacePrototype.src;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
*
* File:          ComponentBase.java
* Creation date: Mar 26, 2004
* Author:        Artur Luís Ribas Barbosa
*               
* Purpose:       Declaration of class ComponentBase
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
public abstract class ComponentBase extends ComponentMovement implements AncestorListener, LabelListener
{
	private Label m_label = null;
	
	public ComponentBase(String sName)
	{
		m_label = new Label(sName);
		
		addAncestorListener(this);
		addLabelListener(this);
	}
	
	public void moveLabel(int iDx, int iDy)
	{
		m_label.setLocation(m_label.getX() + iDx, m_label.getY() + iDy);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
	 */
	public void ancestorAdded(AncestorEvent event)
	{
		// Setting the label size
		Font font = getFont();
		FontMetrics fontMetrics = getFontMetrics(font);
		m_label.setSize(fontMetrics.stringWidth(m_label.getName()), fontMetrics.getHeight());
		// TODO: Need to do a better way to set component size... this size is bigger than is need

		// Inserting label
		getParent().add(m_label);
	
		// Moving to side of this component
		m_label.setLocation(getX() + getWidth() + 5, getY() + (getHeight() - m_label.getHeight()) / 2);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
	 */
	public void ancestorRemoved(AncestorEvent event)
	{
		// Removing label
		getParent().remove(m_label);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
	 */
	public void ancestorMoved(AncestorEvent event)
	{
//		// Moving the label
//		m_label.setLocation(m_label.getX() + e.getX() - m_pointMouseClicked.x, 
//				m_label.getY() + e.getY() - m_pointMouseClicked.y);		
	}
	
}
