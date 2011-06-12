/*
 * Created on Jun 21, 2005
 */
package org.tools4j.jviterbi.core;


/**
 * @author Márcio Emílio Cruz Vono de Azevedo.
 */
public class InputNode extends BaseNode {

	private static final long serialVersionUID = -1762892322823504955L;

	public void step(boolean input, BaseNode source) {
		stepNext(input);
	}
	
	public String serializeCurrentState() {
		String sResult = "" + getState();
		
		return sResult;
	}

	public void restoreState(String state) {
		if(state.equals("1")) {
			setState(1);
		} else {
			setState(0);
		}
	}
}
