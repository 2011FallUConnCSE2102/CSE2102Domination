/*
 * Created on 19/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Point;
import java.io.Serializable;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Connection implements Serializable {
	public static final int CONNECTION_BOUND = 3;
	public static final int CONNECTION_SIZE = 6;
	
	private Point _point = null;
	private BaseComponent _component = null;
	private boolean _bSelected = false;
	
	public Connection(Point point, BaseComponent component) {
		_point = point;
		_component = component;
	}
	
	public BaseComponent getComponent() {
		return _component;
	}
	
	public void setComponent(BaseComponent component) {
		_component = component;
	}
	
	public Point getPoint() {
		return _point;
	}
	
	public void setPoint(Point point) {
		_point = point;
	}
	
	
	public boolean isSelected() {
		return _bSelected;
	}
	public void setSelected(boolean selected) {
		_bSelected = selected;
	}
}
