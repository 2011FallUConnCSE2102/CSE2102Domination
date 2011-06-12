package jpetrinet.Sources.Prototypes.PlacePrototype.src;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
*
* File:          ComponentMovement.java
* Creation date: Mar 26, 2004
* Author:        Artur Luís Ribas Barbosa
*               
* Purpose:       Declaration of class ComponentMovement
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
public abstract class ComponentMovement extends JComponent implements MouseInputListener
{
	private LabelListener m_labelListener = null;
	private Point m_pointMouseClicked = null;
	private boolean m_bMove = false;
	
	public ComponentMovement()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void addLabelListener(LabelListener labelListener)
	{
		m_labelListener = labelListener;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e)
	{
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent mouseEvent)
	{
		if(isInner(mouseEvent))
		{
			m_bMove = true;
			m_pointMouseClicked = mouseEvent.getPoint();
		}
		else
		{
			m_bMove = false;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e)
	{
		if(m_bMove)
		{
			// Moving this component
			setLocation(getX() + e.getX() - m_pointMouseClicked.x, 
					getY() + e.getY() - m_pointMouseClicked.y);

			if(m_labelListener != null)
			{
				m_labelListener.moveLabel(e.getX() - m_pointMouseClicked.x, e.getY() - m_pointMouseClicked.y);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e)
	{
	}

	/**
	 * @return
	 */
	protected abstract boolean isInner(MouseEvent mouseEvent);
}
