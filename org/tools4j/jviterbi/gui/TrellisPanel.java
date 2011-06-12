package org.tools4j.jviterbi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import org.tools4j.jviterbi.core.BaseConverter;
import org.tools4j.jviterbi.core.CoreHandler;
import org.tools4j.jviterbi.core.trellis.ConstelationPoint;
import org.tools4j.jviterbi.core.trellis.Transition;
import org.tools4j.jviterbi.core.trellis.TrellisAnalyzer;

public class TrellisPanel extends JPanel implements ActionListener {
	
	private final int BORDER_SPACE = 50;
	private final int BETWEEN_SPACE = 100;
	
	private static final long serialVersionUID = -2003952191986743802L;
	
	private CoreHandler _coreHandler = null;
	private int _iCols = 10;
	private int _iRows = 4;
	private Collection _colTransitions = new Vector();
	private TrellisAnalyzer _trellisAnalyzer = null;
	private Vector _vctSoftValues = null;
	private Vector _vctHardValues = null;
	private String _sLastStream = new String();
	
	private JPopupMenu _popupMenu = null;
	
	private Transition _currentTransition = null;

	public TrellisPanel() {
		setBorder(new TitledBorder("Trellis Panel"));
		setLayout(null);
		setBackground(Color.WHITE);

		setPreferredSize(new Dimension(_iCols * BETWEEN_SPACE, _iRows * BETWEEN_SPACE));
		
		createPopupMenu();
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent event) {
				if(event.getButton() == MouseEvent.BUTTON1) {
					highlightNear(event.getPoint());
				}
			}
			
			public void mousePressed(MouseEvent event) {
				if (event.isPopupTrigger() && event.getComponent().contains(event.getPoint())) {
		            _popupMenu.show(event.getComponent(), event.getX(), event.getY());
		        }
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent event) {
				if (event.isPopupTrigger() && event.getComponent().contains(event.getPoint())) {
		            _popupMenu.show(event.getComponent(), event.getX(), event.getY());
				}
			}

		};
		
		addMouseListener(mouseAdapter);
	}
	
	public void createPopupMenu() {
		_popupMenu = new JPopupMenu();
		
		JMenuItem menuItemMaxTime = new JMenuItem("Set maximum time");
		menuItemMaxTime.setActionCommand("MaxTime");
		menuItemMaxTime.addActionListener(this);
		
		JMenuItem menuItemDecodeStream = new JMenuItem("Decode stream");
		menuItemDecodeStream.setActionCommand("Decode");
		menuItemDecodeStream.addActionListener(this);
		
		_popupMenu.add(menuItemMaxTime);
		_popupMenu.add(menuItemDecodeStream);
	}
	
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("MaxTime")) {
			changeMaxTime();
		} else if(event.getActionCommand().equals("Decode")) {
			decodeStream();
		}
	}
	
	public void decodeStream() {
		String sStream = JOptionPane.showInputDialog(this, "Stream:", _sLastStream);
		_sLastStream = sStream;
		
/*		if(sStream.charAt(0) == 'S') {
			decodeSoftStream(sStream.substring(2));
		} else if(sStream.charAt(0) == 'H') {
			decodeHardStream(sStream.substring(2));
		}
*/
		decodeSoftStream(sStream);
	}
	
	public void decodeHardStream(String sStream) {
		_vctSoftValues = null;
		_vctHardValues = new Vector();

		for(int i = 0; i < sStream.length(); i++) {
			if(sStream.charAt(i) == '0') {
				_vctHardValues.add(new Integer(0));
			} else {
				_vctHardValues.add(new Integer(1));
			}
		}
		
		repaint();
	}
	
	public void decodeSoftStream(String sStream) {
		_vctHardValues = null;
		_vctSoftValues = new Vector();
		String sValues[] = sStream.split(";");
		
		for(int i = 0; i < sValues.length; i++) {
			String sPoint[] = sValues[i].split(",");
			
			ConstelationPoint constelationPoint = new ConstelationPoint(Integer
					.parseInt(sPoint[0]), Integer.parseInt(sPoint[1]), i);
			
			_vctSoftValues.add(constelationPoint);
		}
		
		repaint();
	}
	
	public void changeMaxTime() {
		_iCols = Integer.parseInt(JOptionPane.showInputDialog(this,
				"Max. Time:", new Integer(_iCols)));
		
		repaint();
	}
	
	public void highlightNear(Point point) {
/*		TrellisAnalyzer trellisAnalyzer = new TrellisAnalyzer(_coreHandler);
		Collection colTransitions = trellisAnalyzer.buildTrellis(_iCols - 1);
*/		
		Iterator iterator = _colTransitions.iterator();
		while(iterator.hasNext()) {
			Transition transition = (Transition) iterator.next();
			
			int iX1 = translateCoordinate(transition.getFirstTime());
			int iX2 = translateCoordinate(transition.getFirstTime() + 1);
			int iY1 = translateCoordinate(transition.getBeginState());
			int iY2 = translateCoordinate(transition.getEndState());

			Line2D line = new Line2D.Float(iX1, iY1, iX2, iY2);
			
			if(line.ptLineDist(point) < 5 && iX1 < point.x && iX2 > point.x) {
				Graphics g = getGraphics();
				
				if(_currentTransition != null) {
					// TODO: Verificar se a linha é de decodificação
					g.setColor(Color.LIGHT_GRAY);
					drawTrellisLine(g, _currentTransition);
				}
				
				g.setColor(Color.BLUE);
				
				drawTrellisLine(g, transition);
				_currentTransition = transition;
				
				break;
			}

		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		_iRows = (int) Math.pow(2, _coreHandler.getStates().size());
		//_iCols = 
		setPreferredSize(new Dimension(_iCols * BETWEEN_SPACE, _iRows * BETWEEN_SPACE));
		//getParent().validate();
		
		drawBasicTrellis(g);
		drawMatrixPoint(g);
		
		drawSoftDecoded(g);
		drawHardDecoded(g);
	}
	
	private void drawSoftDecoded(Graphics g) {
		if(_vctSoftValues != null) {
			Vector vctTransitions = _trellisAnalyzer.softDecode(_vctSoftValues);
			
			g.setColor(Color.RED);
			
			for(int i = 0; i < vctTransitions.size(); i++) {
				Transition transition = (Transition) vctTransitions.get(i);
				drawTrellisLine(g, transition);
			}
		}
	}

	private void drawHardDecoded(Graphics g) {
		if(_vctHardValues != null) {
			Vector vctTransitions = _trellisAnalyzer.hardDecode(_vctHardValues);
			
			g.setColor(Color.RED);
			
			for(int i = 0; i < vctTransitions.size(); i++) {
				Transition transition = (Transition) vctTransitions.get(i);
				drawTrellisLine(g, transition);
			}
		}
	}

	private void drawBasicTrellis(Graphics g) {
		_trellisAnalyzer = new TrellisAnalyzer(_coreHandler);
		_colTransitions = _trellisAnalyzer.buildTrellis(_iCols - 1);
		
		g.setColor(Color.LIGHT_GRAY);
		
		Iterator iterator = _colTransitions.iterator();
		while(iterator.hasNext()) {
			Transition transition = (Transition) iterator.next();
			
			drawTrellisLine(g, transition);
		}
	}

	private void drawTrellisLine(Graphics g, Transition transition) {
		int iX1 = translateCoordinate(transition.getFirstTime());
		int iX2 = translateCoordinate(transition.getFirstTime() + 1);
		int iY1 = translateCoordinate(transition.getBeginState());
		int iY2 = translateCoordinate(transition.getEndState());
		
		String sInputTemp = BaseConverter.decimalToBinary(transition
				.getInputWord(), _coreHandler.getInputs().size());
		
		String sInput = new String();
		for(int i = 0; i < sInputTemp.length(); i++) {
			sInput += sInputTemp.charAt(sInputTemp.length() - 1 - i);
		}
		
		String sOutputTemp = BaseConverter.decimalToBinary(transition
				.getOutputWord(), _coreHandler.getOutputs().size());

		String sOutput = new String();
		for(int i = 0; i < sOutputTemp.length(); i++) {
			sOutput += sOutputTemp.charAt(sOutputTemp.length() - 1 - i);
		}

		g.drawLine(iX1, iY1, iX2, iY2);
		
		if(iX1 != iX2) {
			iX1 = (iX1 + iX2) / 2;
		}
		
		if(iY1 != iY2) {
			iY1 = (iY1 + iY2) / 2;
		}
		
		String sText = sInput + "/" + sOutput;
		g.drawString(sText, iX1 + 5, iY1 - 5);
		
		g.drawLine(iX1, iY1, iX1 + 4, iY1 - 4);
		g.drawLine(iX1 + 4, iY1 - 4, iX1 + sText.length() * 7, iY1 - 4);
		g.drawOval(iX1 - 1, iY1 - 1, 3, 3);
		g.fillOval(iX1 - 1, iY1 - 1, 3, 3);
	}
	
	private void drawMatrixPoint(Graphics g) {
		g.setColor(Color.BLACK);
		
		for(int i = 0; i < _iRows; i++) {
			for(int j = 0; j < _iCols; j++) {
				g.drawOval(translateCoordinate(j) - 1, translateCoordinate(i) - 1, 3, 3);
				g.fillOval(translateCoordinate(j) - 1, translateCoordinate(i) - 1, 3, 3);
			}
		}
	}

	private int translateCoordinate(int j) {
		return j * BETWEEN_SPACE + BORDER_SPACE;
	}

	public CoreHandler getCoreHandler() {
		return _coreHandler;
	}
	
	public void setCoreHandler(CoreHandler coreHandler) {
		_coreHandler = coreHandler;
	}
}
