/*
 * Created on Jul 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

import javax.swing.JTextArea;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriLogPane extends JTextArea
{
	public PetriLogPane() {
		setEditable(false);
		append("JPetriNet V1.1");
	}
}
