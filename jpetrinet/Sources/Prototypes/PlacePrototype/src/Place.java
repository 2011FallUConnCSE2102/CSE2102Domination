package jpetrinet.Sources.Prototypes.PlacePrototype.src;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
*
* File:          Place.java
* Creation date: Mar 23, 2004
* Author:        Artur Luís Ribas Barbosa
*               
* Purpose:       Declaration of class Place
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
public class Place extends ComponentBase
{
	public Place(String sName)
	{
		super(sName);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g)
	{
		g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

		super.paintComponent(g);
	}

	/* (non-Javadoc)
	 * @see ComponentMovement#isInner()
	 */
	protected boolean isInner(MouseEvent mouseEvent)
	{
		Ellipse2D ellipseRegion = new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
		
		return ellipseRegion.contains(mouseEvent.getX(), mouseEvent.getY());
	}
}
