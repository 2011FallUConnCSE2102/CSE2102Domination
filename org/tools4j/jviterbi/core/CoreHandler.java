/*
 * Created on Jun 22, 2005
 */
package org.tools4j.jviterbi.core;

import java.io.Serializable;
import java.util.Vector;

import org.tools4j.jviterbi.core.trellis.ConstelationPoint;

/**
 * @author marcio
 */
public class CoreHandler implements Serializable, Cloneable {
	private static final long serialVersionUID = -3749265206224850790L;
	private Vector m_nodes = new Vector();
	private Vector m_constelation = null;
	
	public AbstractNode createInput() {
		AbstractNode node = new InputNode();
		m_nodes.add(node);
		return node;
	}
	
	public AbstractNode createMemory() {
		AbstractNode node = new MemoryNode();
		m_nodes.add(node);
		return node;
	}

	public AbstractNode createAdder() {
		AbstractNode node = new AdderNode();
		m_nodes.add(node);
		return node;
	}

	public AbstractNode createOutput() {
		AbstractNode node = new OutputNode();
		m_nodes.add(node);
		return node;
	}
	
	public void linkNodes(AbstractNode sourceNode, AbstractNode targetNode) {
		((BaseNode) sourceNode).addNextNode((BaseNode) targetNode);
	}
	
	public void removeNode(AbstractNode node) {
		int toBeRemoved = -1;
		for(int i = 0; i < m_nodes.size(); i++) {
			((BaseNode) m_nodes.get(i)).removeNextNode((BaseNode) node);
			if(m_nodes.get(i) == node) {
				toBeRemoved = i;
			}
		}
		if(toBeRemoved >= 0) m_nodes.remove(toBeRemoved);
	}

	public void removeLink(AbstractNode sourceNode, AbstractNode targetNode) {
		((BaseNode) sourceNode).removeNextNode((BaseNode) targetNode);
	}
	
	public void reset() {
		for(int i = 0; i < m_nodes.size(); i++) {
			((BaseNode) m_nodes.get(i)).reset();
		}
	}
	
	public Vector getInputs() {
		Vector vctResult = new Vector();
		
		for(int i = 0; i < m_nodes.size(); i++) {
			if(m_nodes.get(i) instanceof InputNode) {
				vctResult.add(m_nodes.get(i));
			}
		}
		
		return vctResult;
	}
	
	public Vector getStates() {
		Vector vctResult = new Vector();
		
		for(int i = 0; i < m_nodes.size(); i++) {
			if(((AbstractNode) m_nodes.get(i)).isState()) {
				vctResult.add(m_nodes.get(i));
			}
		}
		
		return vctResult;
	}

	public Vector getOutputs() {
		Vector vctResult = new Vector();
		
		for(int i = 0; i < m_nodes.size(); i++) {
			if(m_nodes.get(i) instanceof OutputNode) {
				vctResult.add(m_nodes.get(i));
			}
		}
		
		return vctResult;
	}
	
	public boolean equals(Object object) {
		boolean isEquals = true;
		
		CoreHandler coreHandler = (CoreHandler) object;
		for(int i = 0; i < m_nodes.size(); i++) {
			if(coreHandler.m_nodes.get(i) != m_nodes.get(i)) {
				isEquals = false;
			}
		}
		
		return isEquals;
	}
	
	public Vector getCurrentState() {
		Vector vctResult = new Vector();
		
		for(int i = 0; i < m_nodes.size(); i++) {
			AbstractNode abstractNode = (AbstractNode) m_nodes.get(i);
			vctResult.add(abstractNode.serializeCurrentState());
		}
		
		return vctResult;
	}
	
	public void setCurrentState(Vector vctStates) {
		for(int i = 0; i < m_nodes.size(); i++) {
			AbstractNode abstractNode = (AbstractNode) m_nodes.get(i);
			abstractNode.restoreState((String) vctStates.get(i));
		}
	}
	/**
	 * @return
	 */
	public Vector getConstelation() {
		return m_constelation;
	}

	/**
	 * @param points
	 */
	public void setConstelation(Vector points) {
		m_constelation = points;
	}

}
