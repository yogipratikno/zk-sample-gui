/**
 * 
 */
package de.forsthaus.zksample.common.menu.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author bbruhns
 * 
 */
public class MenuDomain extends MenuItemDomain {
	@XmlElements( { @XmlElement(name = "menu", type = MenuDomain.class),
			@XmlElement(name = "menuItem", type = MenuItemDomain.class) })
	public List<IMenuDomain> getItems() {
		return this.items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<IMenuDomain> items) {
		this.items = items;
	}

	private List<IMenuDomain> items = new ArrayList<IMenuDomain>();
}
