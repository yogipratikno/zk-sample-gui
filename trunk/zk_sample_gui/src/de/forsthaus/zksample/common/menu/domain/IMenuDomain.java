/**
 * 
 */
package de.forsthaus.zksample.common.menu.domain;

/**
 * @author bbruhns
 *
 */
public interface IMenuDomain {

	String getRightName();

	String getId();

	String getLabel();

	Boolean isWithOnClickAction();

	String getZulNavigation();
	
	String getIconName();

}
