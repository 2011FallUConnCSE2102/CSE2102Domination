package org.tools4j.jviterbi.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.tools4j.jviterbi.core.CoreHandler;
import org.tools4j.jviterbi.core.trellis.ConstelationPoint;

public class ConstalationFrame extends JFrame {
	
	private static final long serialVersionUID = -8003753389797035339L;

	private int _iInputsNumber = 0;
	private Hashtable _hashValues = null;
	
	private JComboBox _comboStates = null;
	private JTextField _fieldX = null;
	private JTextField _fieldY = null;
	private JButton _buttonInsert = null;
	private DefaultListModel _listModel = null;
	private CoreHandler _coreHandler = null;
	
	public ConstalationFrame(CoreHandler coreHandler) {
		_iInputsNumber = coreHandler.getOutputs().size();
		_coreHandler = coreHandler;
		_hashValues = new Hashtable();
		
		setTitle("Constalation Frame");
		getContentPane().setLayout(new GridBagLayout());
		
		initComponent();
		
		pack();
	}
	
	public void initComponent() {
		addLabelState();
		addComboState();
		addLabelX();
		addFieldX();
		addLabelY();
		addFieldY();
		addButtonInsert();
		addList();
	}
	
	public void addLabelState() {
		JLabel label = new JLabel("State: ");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(10, 10, 0, 0);

		getContentPane().add(label, gbc);
	}
	
	public void addComboState() {
		_comboStates = new JComboBox();
		
		generateComboValues();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 0, 10);
		
		getContentPane().add(_comboStates, gbc);		
	}

	public void addLabelX() {
		JLabel label = new JLabel("X: ");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(10, 10, 0, 0);

		getContentPane().add(label, gbc);
	}
	
	public void addFieldX() {
		_fieldX = new JTextField();
		_fieldX.setPreferredSize(new Dimension(40, 20));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 0, 0);
		
		getContentPane().add(_fieldX, gbc);		
	}

	public void addLabelY() {
		JLabel label = new JLabel("Y: ");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(10, 10, 0, 0);

		getContentPane().add(label, gbc);
	}
	
	public void addFieldY() {
		_fieldY = new JTextField();
		_fieldY.setPreferredSize(new Dimension(40, 20));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 0, 0);
		
		getContentPane().add(_fieldY, gbc);		
	}

	public void addButtonInsert() {
		_buttonInsert = new JButton("Add");
		_buttonInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doAddElement();
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 0, 10);
		
		getContentPane().add(_buttonInsert, gbc);		
	}

	public void addList() {
		_listModel = new DefaultListModel();
		JList list = new JList(_listModel);
		JScrollPane scrollPane = new JScrollPane(list);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 5;
		gbc.insets = new Insets(10, 10, 10, 10);
		
		getContentPane().add(scrollPane, gbc);		
	}

	private void generateComboValues() {
		for(int i = 0; i < Math.pow(2,_iInputsNumber); i++) {
			_comboStates.addItem(String.valueOf(i));
		}
	}
	
	private void doAddElement() {
		if(!"".equals(_fieldX.getText().trim()) &&
				!"".equals(_fieldY.getText().trim())) {
			int iElementIndex = _comboStates.getSelectedIndex();
			Point pointElementValue = new Point(Integer.valueOf(
					_fieldX.getText().trim()).intValue(), Integer.valueOf(
					_fieldY.getText().trim()).intValue());
			
			_hashValues.put(new Integer(iElementIndex), pointElementValue);
			
			revaluateList();
			_coreHandler.setConstelation(getConstalation());
			
			if(_comboStates.getSelectedIndex() < _comboStates.getItemCount() - 1) {
				_comboStates.setSelectedIndex(_comboStates.getSelectedIndex() + 1);
			} else {
				_comboStates.setSelectedIndex(0);
			}
			
			_fieldX.setText("");
			_fieldY.setText("");
			
			//TODO: Setar o focus no TextField
		}
	}
	
	private void revaluateList() {
		_listModel.clear();

		for(int i = 0; i < Math.pow(2, _iInputsNumber); i++) {
			Point pointElementValue = (Point) _hashValues.get(new Integer(i));
			if(pointElementValue != null) {
				_listModel.addElement(i + ": " + pointElementValue.x + "," + pointElementValue.y);
			}
		}
	}
	
	public Vector getConstalation() {
		Vector vctConstalation = new Vector();
		
		for(int i = 0; i < Math.pow(2, _iInputsNumber); i++) {
			Point pointElementValue = (Point) _hashValues.get(new Integer(i));
			if(pointElementValue != null) {
				ConstelationPoint constelationPoint = new ConstelationPoint(pointElementValue.y, pointElementValue.x, i);
				vctConstalation.add(constelationPoint);
			}
		}

		return vctConstalation;
	}
}
