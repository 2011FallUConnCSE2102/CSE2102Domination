/*
 * Created on Jun 22, 2005
 */
package org.tools4j.jviterbi.core.trellis;

import java.util.Vector;

/**
 * Esta classe representa o modelo de uma transi��o no diagrama de treli�as. Uma transi��o � representada pela sua
 * localiza��o, ou seja, estado origem e destino (beginState e endState) e o tempo da primeira apari��o, e tamb�m
 * pelas palavras de entrada e sa�da daquela transi��o. Al�m da representa��o, ainda � poss�vel calcular a dist�ncia
 * entre uma palavra recebida qualquer e a palavra de sa�da da transi��o, tanto em hard decision quanto em soft
 * decision.
 * @author M�rcio Em�lio Cruz Vono de Azevedo.
 */
public class Transition {
	private int m_beginState;
	private int m_endState;
	private int m_firstTime;
	private int m_inputWord;
	private int m_outputWord;
	private double m_cummulatedWeight = 0;
	
	/**
	 * Cria uma transi��o dado o estado inicial, estado final, o tempo da primeira apari��o da transi��o, a palavra
	 * de entrada e a palavra de sa�da.
	 * @param beginState Estado de onde esta transi��o se origina.
	 * @param endState Estado onde esta transi��o termina.
	 * @param firstTime Primeira vez que a transi��o aparece. Por exemplo, uma transi��o que parte do estado inicial
	 * 					para um outro tem tempo 0. Uma transi��o que se origina em um estado que s� foi atingido ap�s
	 * 					outras duas transi��es tempo 2, pois j� se passaram o tempo 0 e 1.
	 * @param inputWord concatena��o dos bits de entrada que resultaram nesta transi��o ( u1 u2 ... uM ).
	 * @param outputWord concatena��o dos bits de sa�da que resultaram nesta transi��o ( v0 v1 ... vN ).
	 */
	public Transition(int beginState, int endState, int firstTime, int inputWord, int outputWord) {
		m_beginState = beginState;
		m_endState = endState;
		m_firstTime = firstTime;
		m_inputWord = inputWord;
		m_outputWord = outputWord;
	}
	
	public int calculateHardDistance(int receivedWord, int wordLenght) {
		int xor = m_outputWord ^ receivedWord;
		int result = 0;
		for(int i = 0; i < wordLenght; i++) {
			result += (xor >>> i) & 1;
		}
		return result;
	}

	public double calculateSoftDistance(ConstelationPoint receivedPoint,
										Vector constelation) {
		for(int i = 0; i < constelation.size(); i++) {
			if(((ConstelationPoint) constelation.get(i)).getSymbol() == m_outputWord) {
				return euclidianDistance((ConstelationPoint) constelation.get(i), receivedPoint);
			}
		}
		return 0;
	}
	
	/**
	 * Calcula a dist�ncia euclidiana entre dois pontos. A dist�ncia euclidiana � dada pela raiz quadrada da soma do
	 * quadrado da subra��o das coordenadas X de cada ponto com o quadrado da subra��o das coordenas Y dos mesmos.
	 * <br>
	 * d(i,j),(m,n) = SQRT( ( i - m )^2 + ( j - n )^2 )
	 * @param firstPoint Primeiro ponto para o c�lculo.
	 * @param secondPoint Segundo ponto para o c�lculo.
	 * @return Dist�ncia euclidiana entre os pontos.
	 */
	private double euclidianDistance(ConstelationPoint firstPoint, ConstelationPoint secondPoint) {
		return Math.sqrt(Math.pow(firstPoint.getX() - secondPoint.getX(), 2) + 
						 Math.pow(firstPoint.getY() - secondPoint.getY(), 2));
	}
	
	public int getBeginState() {
		return m_beginState;
	}

	public int getEndState() {
		return m_endState;
	}

	public int getFirstTime() {
		return m_firstTime;
	}

	public int getInputWord() {
		return m_inputWord;
	}

	public int getOutputWord() {
		return m_outputWord;
	}
	
	public String toNormalizedString() {
		return "" + m_beginState + "-" + m_endState + "-" + m_firstTime;
	}

	public double getCummulatedWeight() {
		return m_cummulatedWeight;
	}

	public void setCummulatedWeight(double d) {
		m_cummulatedWeight = d;
	}
	
}
