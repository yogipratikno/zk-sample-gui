/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import org.zkoss.zk.ui.Component;

/**
 * @author bbruhns
 *
 */
public interface ILabelElement extends Component{

	void setZulNavigation(String zulNavigation);

	void setLabel(String string);

	void setImage(String image);
}
