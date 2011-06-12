package jpetrinet.Sources.Prototypes.PlacePrototype.src;

import jpetrinet.Sources.Prototypes.PlacePrototype.src.Place;
import javax.swing.JFrame;

/**
*
* File:          PlaceBeanTest.java
* Creation date: Mar 23, 2004
* Author:        Artur Luís Ribas Barbosa
*               
* Purpose:       Declaration of class PlaceBeanTest
*
* Copyright 2003, INATEL Centro de Competência em Telecomunicações
* All rights are reserved. Reproduction in whole or part is
* prohibited without the written consent of the copyright owner.
*
*/

/**
 * @author Artur Luís Ribas Barbosa
 *
 * Classe responsável por ...
 */
public class PlaceBeanTest extends JFrame
{

	private javax.swing.JPanel jContentPane = null;

	private Place place1 = null;
	private Place place2 = null;
	private Place place3 = null;
	private Place place = null;

	public static void main(String[] args)
	{
		new PlaceBeanTest().setVisible(true);
	}
	/**
	 * This is the default constructor
	 */
	public PlaceBeanTest()
	{
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getPlace(), null);
			jContentPane.add(getPlace1(), null);
			jContentPane.add(getPlace2(), null);
			jContentPane.add(getPlace3(), null);
		}
		return jContentPane;
	}
	/**
	 * This method initializes place
	 * 
	 * @return Place
	 */
	private Place getPlace() {
		if(place == null) {
			place = new Place("P1");
			place.setSize(25, 25);
			place.setLocation(52, 83);
		}
		return place;
	}
	/**
	 * This method initializes place1
	 * 
	 * @return Place
	 */
	private Place getPlace1() {
		if(place1 == null) {
			place1 = new Place("P2");
			place1.setSize(25, 25);
			place1.setLocation(170, 36);
		}
		return place1;
	}
	/**
	 * This method initializes place2
	 * 
	 * @return Place
	 */
	private Place getPlace2() {
		if(place2 == null) {
			place2 = new Place("P3");
			place2.setSize(25, 25);
			place2.setLocation(47, 35);
		}
		return place2;
	}
	/**
	 * This method initializes place3
	 * 
	 * @return Place
	 */
	private Place getPlace3() {
		if(place3 == null) {
			place3 = new Place("P4");
			place3.setSize(25, 25);
			place3.setLocation(187, 130);
		}
		return place3;
	}
	private void setPlace(Place place)
	{
		this.place = place;
	}

}
