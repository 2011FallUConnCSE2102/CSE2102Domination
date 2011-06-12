/*
 * Created on Jun 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tools4j.jviterbi.core;

import java.io.Serializable;

/**
 * @author marcio
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface AbstractNode extends Serializable, Cloneable {
	public void step(boolean input);
	
	public int getState();
	
	public void setAsState(boolean isState);
	
	public boolean isState();
	
	public String serializeCurrentState();
	
	public void restoreState(String state);
}
