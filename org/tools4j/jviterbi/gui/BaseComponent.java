/*
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tools4j.jviterbi.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.MouseInputListener;

import org.tools4j.jviterbi.core.AbstractNode;

/**
 * @author artur
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BaseComponent extends JComponent implements MouseInputListener, ActionListener {
	
	protected JPopupMenu _popupMenu = null;
	private Point _initialPoint = null;
	private Collection _colConnections = null;
	private Collection _colCommandsListener = null;
	private Collection _colDependentComponents = null;
	
	private AbstractNode _abstractNode = null;
	
	private Connection _connectionSelected = null;
	
	private boolean _bConnectionsCanBeVisible = true;
	private boolean _bConnectionsVisible = false;
	private boolean _bDragging = false;
	private boolean _bMoveble = true;
	
	private boolean _bIsState = false;
	
	private JMenuItem _menuItemState = null;
	
	public BaseComponent() {
		_colConnections = new Vector();
		_colCommandsListener = new Vector();  
		_colDependentComponents = new Vector();
		
		addMouseMotionListener(this);
		addMouseListener(this);
		
		createPopupMenu();
	}
	
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Excluir")) {
			fireRemoveComponent(this);
		} else if(event.getActionCommand().equals("SetState")) {
			_abstractNode.setAsState(true);
			_menuItemState.setActionCommand("ResetState");
			_menuItemState.setText("Set as not state");
			_bIsState = true;
			repaint();
		} else if(event.getActionCommand().equals("ResetState")) {
			_abstractNode.setAsState(false);
			_menuItemState.setActionCommand("SetState");
			_menuItemState.setText("Set as state");
			_bIsState = false;
			repaint();
		}
	}

	private void createPopupMenu() {
		_popupMenu = new JPopupMenu();
		JMenuItem menuItemDelete = new JMenuItem("Excluir");
		menuItemDelete.setActionCommand("Excluir");
		menuItemDelete.addActionListener(this);
		
		_menuItemState = new JMenuItem("Set as state");
		_menuItemState.setActionCommand("SetState");
		_menuItemState.addActionListener(this);
		
		JMenuItem menuItemCancel = new JMenuItem("Cancelar");
		
		_popupMenu.add(menuItemDelete);
		_popupMenu.add(_menuItemState);
		_popupMenu.add(new JSeparator());
		_popupMenu.add(menuItemCancel);
	}
	
	private void fireRemoveComponent(BaseComponent sourceComponent) {
		Iterator iterator = _colCommandsListener.iterator();
		while(iterator.hasNext()) {
			((CommandslListener) iterator.next()).removeComponent(this);
		}
		
		updateDependentComponents(sourceComponent);
	}
	
	private void updateDependentComponents(BaseComponent sourceComponent) {
		Iterator iterator = _colDependentComponents.iterator();
		while(iterator.hasNext()) {
			BaseComponent component = (BaseComponent) iterator.next();
			
			if(component != sourceComponent) {
				if(this instanceof Line) {
					component.removeThisComponent(this);
				} else {
					component.fireRemoveComponent(sourceComponent);
				}
			}
		}
	}
	
	private void removeThisComponent(BaseComponent component) {
		_colDependentComponents.remove(component);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		
		Connection connection = getConnectionInside(event.getPoint());
		
		if(connection != null) {
			sendCommandLine(getConnectionInside(event.getPoint()));
		}
	}
	
	private Connection getConnectionInside(Point pointEvent) {
		Connection connectionResult = null;
		
		Iterator iterator = _colConnections.iterator();
		while(iterator.hasNext()) {
			Connection connection = (Connection) iterator.next();
			Point point = connection.getPoint();
			Rectangle rect = new Rectangle(point.x - Connection.CONNECTION_BOUND, 
					point.y - Connection.CONNECTION_BOUND, 
					Connection.CONNECTION_SIZE, 
					Connection.CONNECTION_SIZE);
			
			if(rect.contains(pointEvent)){
				connectionResult = connection;
			}
		}
		
		return connectionResult;
	}
	
	private void sendCommandLine(Connection connection) {
		Iterator iterator = _colCommandsListener.iterator();
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.addLine(connection);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		if (event.isPopupTrigger() && event.getComponent().contains(event.getPoint())) {
            _popupMenu.show(event.getComponent(), event.getX(), event.getY());
        } else if(event.getButton() == MouseEvent.BUTTON1) {
			_initialPoint = event.getPoint();
			_bDragging = true;
        }
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {
		if (event.isPopupTrigger() && event.getComponent().contains(event.getPoint())) {
            _popupMenu.show(event.getComponent(), event.getX(), event.getY());
		} else if(event.getButton() == MouseEvent.BUTTON1) {
			_bDragging = false;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		if(_bConnectionsCanBeVisible) {
			_bConnectionsVisible = true;
			repaint();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		if(_bConnectionsCanBeVisible) {
			_bConnectionsVisible = false;
			repaint();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {
		if(_initialPoint != null && _bMoveble && _bDragging) {
			int xMovement = event.getPoint().x - _initialPoint.x;
			int yMovement = event.getPoint().y - _initialPoint.y;
			
//			if((xMovement != 0 && ((xMovement + getLocation().x) % 5) == 0) || 
//					(yMovement != 0 && ((yMovement + getLocation().y) % 5) == 0)) {
				setLocation(getLocation().x + xMovement, getLocation().y + yMovement);
//			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent event) {
		if(_bConnectionsVisible) {
			Connection connection = getConnectionInside(event.getPoint());
			
			if(connection != null) {
				if(_connectionSelected != connection) {
					if(_connectionSelected != null) {
						_connectionSelected.setSelected(false);
					}
					
					connection.setSelected(true);
					_connectionSelected = connection;
					repaint();
				}
			} else {
				if(_connectionSelected != null) {
					_connectionSelected.setSelected(false);
					repaint();
					
					_connectionSelected = null;
				}
			}
		}
	}
	
	protected void addConnection(Connection connection) {
		_colConnections.add(connection);
	}
	
	protected Collection getConnections() {
		return _colConnections;
	}
	
	/** (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {

		if(_bIsState) {
			g.setColor(new Color(100, 150, 200));
		} else {
			g.setColor(Color.BLACK);
		}
		
		draw(g);
		
		if(_bConnectionsVisible || _bDragging) {
			Iterator iterator = _colConnections.iterator();
			
			while(iterator.hasNext()) {
				g.setColor(Color.BLUE);

				Connection connection = (Connection) iterator.next();
				Point point = connection.getPoint();
				
				if(connection.isSelected()) {
					g.setColor(Color.RED);
				}
				
				g.drawRect(point.x - Connection.CONNECTION_BOUND, 
						point.y - Connection.CONNECTION_BOUND, 
						Connection.CONNECTION_SIZE, 
						Connection.CONNECTION_SIZE);
			}
		}
		
		super.paintComponent(g);
	}
	
	public void addCommandsListener(CommandslListener listener) {
		_colCommandsListener.add(listener);
	}
	
	public boolean isMoveble() {
		return _bMoveble;
	}

	public void setMoveble(boolean moveble) {
		_bMoveble = moveble;
	}
	
	public boolean isConnectionsCanBeVisible() {
		return _bConnectionsCanBeVisible;
	}
	public void setConnectionsCanBeVisible(boolean connectionsCanBeVisible) {
		_bConnectionsCanBeVisible = connectionsCanBeVisible;
	}
	
	public void addDependentComponent(BaseComponent baseComponent) {
		_colDependentComponents.add(baseComponent);
	}
	
	public AbstractNode getAbstractNode() {
		return _abstractNode;
	}

	public void setAbstractNode(AbstractNode abstractNode) {
		_abstractNode = abstractNode;
	}
	
	protected void sendStateChangedEvent() {
		Iterator iterator = _colCommandsListener.iterator();
		while(iterator.hasNext()) {
			CommandslListener listener = (CommandslListener) iterator.next();
			listener.stateChanged();
		}
	}
	
	protected abstract void draw(Graphics g);
}
