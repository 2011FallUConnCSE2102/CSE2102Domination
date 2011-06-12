/*
 * Created on Jun 21, 2005
 */
package org.tools4j.jviterbi.core;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Márcio Emílio Cruz Vono de Azevedo.
 */
public abstract class BaseNode implements AbstractNode {
	private Vector m_nextNodes = new Vector();
	private boolean m_state = false;
	private boolean m_isState = false;
	
	public void step(boolean input) {
		step(input, null);
	}
	
	public int getState() {
		return m_state ? 1 : 0;
	}
	
	public void setState(int iState) {
		m_state = iState == 1;
	}
	
	public abstract void step(boolean input, BaseNode source);

	protected void stepNext(boolean output) {
		m_state = output;
		Iterator nextNodeIterator = m_nextNodes.iterator();
		while(nextNodeIterator.hasNext()) {
			((BaseNode) nextNodeIterator.next()).step(output, this);
		}
	}

	public void addNextNode(BaseNode nextNode) {
		m_nextNodes.add(nextNode);
	}

	public void removeNextNode(BaseNode nextNode) {
		int toBeRemoved = -1;
		for(int i = 0; i < m_nextNodes.size(); i++) {
			if(m_nextNodes.get(i) == nextNode) {
				toBeRemoved = i;
			}
		}
		if(toBeRemoved >= 0) m_nextNodes.remove(toBeRemoved);
	}
	
	public void reset() {
		m_state = false;
	}
	
	public boolean isState() {
		return m_isState;
	}

	public void setAsState(boolean isState) {
		m_isState = true;
	}
	
	Vector getNextNodes() {
		return m_nextNodes;
	}
	
	public boolean equals(Object object) {
		boolean isEquals = true;
		BaseNode baseNode = (BaseNode) object;
		if(baseNode.m_state != m_state) {
			isEquals = false;
		}
		return isEquals;
	}
}
