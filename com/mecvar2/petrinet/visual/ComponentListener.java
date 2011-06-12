/*
 * Created on Jul 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mecvar2.petrinet.visual;

import com.mecvar2.petrinet.core.Arc;
import com.mecvar2.petrinet.core.Place;
import com.mecvar2.petrinet.core.Transition;

/**
 * @author artur
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ComponentListener
{
	public void removeMe(Place place);
	public void removeMe(Transition transition);
	public void removeMe(Arc arc);
}
