/*
 * Created on Jun 22, 2005
 */
package org.tools4j.jviterbi.core.trellis;

import java.io.Serializable;

/**
 * @author Márcio Emílio Cruz Vono de Azevedo.
 */
public class ConstelationPoint implements Serializable {
	private int m_x;
	private int m_y;
	private int m_symbol;
	
	public ConstelationPoint(int x, int y, int symbol) {
		m_x = x;
		m_y = y;
		m_symbol = symbol;
	}
	
	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}

	public int getSymbol() {
		return m_symbol;
	}
}
