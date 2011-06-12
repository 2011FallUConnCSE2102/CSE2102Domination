/*
 * Created on Jun 21, 2005
 */
package org.tools4j.jviterbi.core;

/**
 * @author Márcio Emílio Cruz Vono de Azevedo.
 */
public class OutputNode extends BaseNode {
	private static final long serialVersionUID = 8922281790732410530L;
	private boolean m_currentState;

	public void step(boolean input, BaseNode source) {
		m_currentState = input;
		stepNext(input); // só para atualizar o state.
	}

	public boolean getCurrentState() {
		return m_currentState;
	}
	
	public void reset() {
		super.reset();
		m_currentState = false;
	}

	public String serializeCurrentState() {
		String sResult = new String();
		sResult += m_currentState ? 1 : 0;
		sResult += getState();
		
		return sResult;
	}

	public void restoreState(String state) {
		m_currentState = state.charAt(0) == '1';
		setState(state.charAt(1) == '1' ? 1 : 0);
	}
}
