/*
 * Created on Jul 5, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tools4j.jviterbi.core.trellis;

import java.util.Vector;

import org.tools4j.jviterbi.core.BaseNode;
import org.tools4j.jviterbi.core.CoreHandler;
import org.tools4j.jviterbi.core.InputNode;
import org.tools4j.jviterbi.core.OutputNode;

/**
 * @author marcio
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TrellisAnalyzer {
	CoreHandler m_coreHandler = null;
	public TrellisAnalyzer(CoreHandler handler) {
		m_coreHandler = handler;
	}
	
	public Vector buildTrellis(int maxTime) {
		CoreHandler initialHandler = m_coreHandler;
		initialHandler.reset();
		Vector trellis = new Vector();
		buildRecursiveTrellis(trellis, /*initialHandler,*/ initialHandler, 0, 0, maxTime);
		return trellis;
	}
	
	private void buildRecursiveTrellis(Vector trellis, /*CoreHandler initialHandler,*/ CoreHandler currentHandler, int beginState, int time, int maxTime) {
		Vector inputs = currentHandler.getInputs();
		int inputWords = (int) Math.pow(2, inputs.size());
		for(int i = 0; i < inputWords; i++) {
			Vector vctOldState = currentHandler.getCurrentState();
			
			// i é a inputword
			for(int j = 0; j < inputs.size(); j++) {
				int bit = (i >>> j) & 1;
				boolean value = bit == 1 ? true : false;
				((InputNode) inputs.get(j)).step(value);
			}
			// o é a outputword
			int o = 0;
			Vector outputs = currentHandler.getOutputs();
			for(int j = 0; j < outputs.size(); j++) {
				o |= (((OutputNode) outputs.get(j)).getState() << j);
			}
			// s é o endState
			int s = 0;
			Vector states = currentHandler.getStates();
			for(int j = 0; j < states.size(); j++) {
				s |= (((BaseNode) states.get(j)).getState() << j);
			}
			
			Transition transition = new Transition(beginState, s, time, i, o);
			
			boolean isInside = false;
			for(int j = 0; j < trellis.size(); j++) {
				if(((Transition) trellis.get(j)).toNormalizedString().equals(transition.toNormalizedString())) {
					isInside = true;
					break;
				}
			}
			
			if(time < maxTime && !isInside) {
				trellis.add(transition);

				buildRecursiveTrellis(trellis, currentHandler, s, time + 1, maxTime);
			}
			
			currentHandler.setCurrentState(vctOldState);
			
			if(isInside) {
				break;
			}
		}
	}
	
	public Vector hardDecode(Vector receivedWords) {
		int wordLenght = m_coreHandler.getOutputs().size();
		int numberOfWords = receivedWords.size();
		Vector trellis = buildTrellis(numberOfWords);
		for(int i = 0; i < numberOfWords; i++) {
			Vector timmedTransitions = getTransitionsByTime(trellis, i);
			for(int j = 0; j < timmedTransitions.size(); j++) {
				Transition transition = (Transition) timmedTransitions.get(j);
				double cummulatedWeight = transition.calculateHardDistance(((Integer)receivedWords.get(i)).intValue(), wordLenght);
				if(i != 0) {
					cummulatedWeight += getLastWeight(trellis, i - 1, transition.getBeginState());
				}
				transition.setCummulatedWeight(cummulatedWeight);
			}
		}
		
		return discoverLessWeightPath(trellis, numberOfWords);
	}

	public Vector softDecode(Vector receivedWords) {
		if(m_coreHandler.getConstelation() == null) {
			return null;
		}
		int numberOfWords = receivedWords.size();
		Vector trellis = buildTrellis(numberOfWords);
		for(int i = 0; i < numberOfWords; i++) {
			Vector timmedTransitions = getTransitionsByTime(trellis, i);
			for(int j = 0; j < timmedTransitions.size(); j++) {
				Transition transition = (Transition) timmedTransitions.get(j);
				double cummulatedWeight = transition.calculateSoftDistance((ConstelationPoint) receivedWords.get(i), m_coreHandler.getConstelation());
				if(i != 0) {
					cummulatedWeight += getLastWeight(trellis, i - 1, transition.getBeginState());
				}
				transition.setCummulatedWeight(cummulatedWeight);
			}
		}
		
		return discoverLessWeightPath(trellis, numberOfWords);
	}
	
	private Vector getTransitionsByTime(Vector trellis, int time) {
		Vector result = new Vector();
		for(int i = 0; i < trellis.size(); i++) {
			if(((Transition) trellis.get(i)).getFirstTime() == time) {
				result.add(trellis.get(i));
			}
		}
		return result;
	}
	
	private double getLastWeight(Vector trellis, int time, int state) {
		double lastWeight = -1;
		for(int i = 0; i < trellis.size(); i++) {
			Transition transition = (Transition) trellis.get(i);
			if(transition.getFirstTime() == time && transition.getEndState() == state) {
				if(lastWeight == -1) {
					lastWeight = transition.getCummulatedWeight();
				} else {
					if(lastWeight > transition.getCummulatedWeight()) {
						lastWeight = transition.getCummulatedWeight();
					}
				}
			}
		}
		if(lastWeight == -1) {
			return 0;
		}
		return lastWeight;
	}

	private Transition getLastWeightTransition(Vector trellis, int time, int state) {
		double lastWeight = -1;
		int lastWeightTransition = 0;
		for(int i = 0; i < trellis.size(); i++) {
			Transition transition = (Transition) trellis.get(i);
			if(transition.getFirstTime() == time && transition.getEndState() == state) {
				if(lastWeight == -1) {
					lastWeight = transition.getCummulatedWeight();
					lastWeightTransition = i;
				} else {
					if(lastWeight > transition.getCummulatedWeight()) {
						lastWeight = transition.getCummulatedWeight();
						lastWeightTransition = i;
					}
				}
			}
		}
		if(lastWeight == -1) {
			return null;
		}
		return (Transition) trellis.get(lastWeightTransition);
	}

	private Vector discoverLessWeightPath(Vector trellis, int numberOfWords) {
		Vector result = new Vector();
		Vector transitions = getTransitionsByTime(trellis, numberOfWords - 1);
		double lessWeight = -1;
		int lastLessIndex = 0;
		for(int i = 0; i < transitions.size(); i++) {
			Transition transition = (Transition) transitions.get(i);
			if(lessWeight == -1) {
				lessWeight = transition.getCummulatedWeight();
				lastLessIndex = i;
			} else {
				if(transition.getCummulatedWeight() < lessWeight) {
					lessWeight = transition.getCummulatedWeight();
					lastLessIndex = i;
				}
			}
		}
		Transition endTransition = (Transition) transitions.get(lastLessIndex);
		result.add(endTransition);
		Transition currentTransition = endTransition;
		for(int i = numberOfWords - 2; i >= 0; i--) {
			currentTransition = getLastWeightTransition(trellis, i, currentTransition.getBeginState());
			result.add(currentTransition);
		}
		return result;
	}

}
