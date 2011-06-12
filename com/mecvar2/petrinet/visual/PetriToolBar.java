/*
 * Created on Jun 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PetriToolBar extends JToolBar {
	
	private JButton m_jbClear;
	private JButton m_jbDelete;
	private JButton m_jbSelect;
	private JButton m_jbPlace;
	private JButton m_jbTransition;
	private JButton m_jbArc;
	private JButton m_jbDMatrix;
	private JButton m_jbBuildTree;
	private JButton m_jbRun;
	private JButton m_jbRate;
	private JTextField m_jtfRate;
	private PetriToolBarListener m_ptblThisListener;
	
	public PetriToolBar () {
		
		createButtons();

		m_jtfRate = new JTextField ("1");
		m_jtfRate.setPreferredSize(new Dimension(50, m_jtfRate.getPreferredSize().height));
		m_jtfRate.setMaximumSize(new Dimension(50, m_jtfRate.getPreferredSize().height));
		m_jtfRate.setMinimumSize(new Dimension(50, m_jtfRate.getPreferredSize().height));

		createButtonsActionListener();

		m_jbSelect.setEnabled(false);
		placeComponentsOnToolbar();
		this.setRollover(true);
	}

	private void placeComponentsOnToolbar() {
		add(new JLabel ("Edit: "));
		add(m_jbClear);
		add(m_jbDelete);
		add(m_jbSelect);
		addSeparator();
		add(new JLabel ("Insert: "));
		add(m_jbPlace);
		add(m_jbTransition);
		add(m_jbArc);
		addSeparator();
		add(new JLabel ("Analysis: "));
		add(m_jbDMatrix);
		add(m_jbBuildTree);
		addSeparator();
		add(new JLabel ("Timed Petri Net: "));
		add(m_jbRun);
		add(m_jbRate);
		add(new JLabel (" Rate: "));
		add(m_jtfRate);
	}

	private void createButtonsActionListener() {
		m_jbClear.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbSelect.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_CLEAR);				
			}
		});
		
		m_jbDelete.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbDelete.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_DELETE);				
			}
		});
		
		m_jbSelect.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbSelect.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_SELECT);				
			}
		});
		
		m_jbPlace.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbPlace.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_PLACE);				
			}
		});
		
		m_jbTransition.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbTransition.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_TRANSITION);				
			}
		});
		
		m_jbArc.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				enableAll();
				m_jbArc.setEnabled(false);
				m_ptblThisListener.insertComponent(PetriToolBarListener.IS_ARC);				
			}
		});
		
		m_jbDMatrix.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				m_ptblThisListener.processAnalysis(PetriToolBarListener.IS_DMATRIX);				
			}
		});
		
		m_jbBuildTree.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				m_ptblThisListener.processAnalysis(PetriToolBarListener.IS_BUILDTREE);				
			}
		});
		
		m_jbRun.addActionListener(new ActionListener () {
			private boolean running = false;
			public void actionPerformed(ActionEvent actionEvent) {
				m_ptblThisListener.processTimming(PetriToolBarListener.IS_RUN, 1.0);	
				if(running) {
					running = false;
					m_jbRun.setIcon(getImageIcon("images/run.gif"));
				} else {
					running = true;
					m_jbRun.setIcon(getImageIcon("images/stop.gif"));	
				}
			}
		});
		
		m_jbRate.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					double dRate = new Double(m_jtfRate.getText()).doubleValue();
					m_ptblThisListener.processTimming(PetriToolBarListener.IS_SET_RATE, dRate);	
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(PetriToolBar.this, "Please, enter an double value.");
				}
			}
		});
	}

	private void createButtons() {
		m_jbClear = new JButton (getImageIcon("images/clear.gif"));
		m_jbClear.setToolTipText("Clear");
		m_jbDelete = new JButton (getImageIcon("images/delete.gif"));
		m_jbDelete.setToolTipText("Delete");
		m_jbSelect = new JButton (getImageIcon("images/select.gif"));
		m_jbSelect.setToolTipText("Select");
		m_jbPlace = new JButton (getImageIcon("images/place.gif"));
		m_jbPlace.setToolTipText("Place");
		m_jbTransition = new JButton (getImageIcon("images/transition.gif"));
		m_jbTransition.setToolTipText("Transition");
		m_jbArc = new JButton (getImageIcon("images/arc.gif"));
		m_jbArc.setToolTipText("Arc");
		m_jbDMatrix = new JButton (getImageIcon("images/dmatrix.gif"));
		m_jbDMatrix.setToolTipText("D Matrix");
		m_jbBuildTree = new JButton (getImageIcon("images/buildtree.gif"));
		m_jbBuildTree.setToolTipText("Build tree");
		m_jbRun = new JButton (getImageIcon("images/run.gif"));
		m_jbRun.setToolTipText("Run/Stop");
		m_jbRate = new JButton (getImageIcon("images/setrate.gif"));
		m_jbRate.setToolTipText("Set rate");
	}

	private ImageIcon getImageIcon(String location) {
		return new ImageIcon(ClassLoader.getSystemResource(location));
	}
	
	public void enableAll () {
		m_jbClear.setEnabled(true);
		m_jbDelete.setEnabled(true);
		m_jbSelect.setEnabled(true);
		m_jbPlace.setEnabled(true);
		m_jbTransition.setEnabled(true);
		m_jbArc.setEnabled(true);
		m_jbDMatrix.setEnabled(true);
		m_jbBuildTree.setEnabled(true);
		m_jbRun.setEnabled(true);
	}
	
	public void addPetriToolBarListener (PetriToolBarListener listener) {
		m_ptblThisListener = listener;
	}
}
