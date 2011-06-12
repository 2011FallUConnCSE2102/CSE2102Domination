/*
 * Created on Jun 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface PetriToolBarListener {

	public static final int IS_CLEAR = 0;
	public static final int IS_DELETE = 1;
	public static final int IS_SELECT = 2;
	public static final int IS_PLACE = 3;
	public static final int IS_TRANSITION = 4;
	public static final int IS_ARC = 5;
	
	public static final int IS_BUILDTREE = 10;
	public static final int IS_DMATRIX = 11;

	public static final int IS_RUN = 20;
	public static final int IS_SET_RATE = 21;

	void insertComponent(int iComponent);
	void processAnalysis(int iAnalysis);
	void processTimming(int iTimming, double rate);

}
