/*
 * Created on Jun 21, 2005
 */
package org.tools4j.jviterbi.core;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Márcio Emílio Cruz Vono de Azevedo.
 */
public class AdderNode extends BaseNode {
	
	private static final long serialVersionUID = -7374411504329759508L;

	private Hashtable m_receivedInformations = new Hashtable();

	public void step(boolean input, BaseNode source) {
		boolean output = false;
		m_receivedInformations.put(source, new Boolean(input));
		Enumeration informations = m_receivedInformations.elements();
		while(informations.hasMoreElements()) {
			output^= ((Boolean) informations.nextElement()).booleanValue();
		}
		stepNext(output);
	}

	public void reset() {
		super.reset();
		m_receivedInformations = new Hashtable();
	}

	public String serializeCurrentState() {
		return "";
	}

	public void restoreState(String state) {
		
	}
}
