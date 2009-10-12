/**
 * 
 */
package de.forsthaus.zksample.common.menu.dropdown;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Menupopup;

import de.forsthaus.zksample.common.menu.domain.IMenuDomain;
import de.forsthaus.zksample.common.menu.util.ILabelElement;
import de.forsthaus.zksample.common.menu.util.MenuFactoryDto;
import de.forsthaus.zksample.common.menu.util.ZkossMenuFactory;

/**
 * @author bbruhns
 * 
 */
public class ZkossDropDownMenuFactory extends ZkossMenuFactory {
	private static final long serialVersionUID = -6930474675371322560L;

	public static void addDropDownMenu(Component component) {
		new ZkossDropDownMenuFactory(component);
	}

	/**
	 * @param component
	 */
	private ZkossDropDownMenuFactory(Component component) {
		super(component);
	}

	@Override
	protected MenuFactoryDto createMenuComponent(Component parent) {
		DefaultDropDownMenu menu = new DefaultDropDownMenu();
		parent.appendChild(menu);

		Menupopup menupopup = new Menupopup();
		menu.appendChild(menupopup);

		return new MenuFactoryDto(menupopup, menu);
	}

	@Override
	protected ILabelElement createItemComponent(Component parent) {
		DefaultDropDownMenuItem item = new DefaultDropDownMenuItem();
		parent.appendChild(item);
		return item;
	}

	@Override
	protected void setAttributes(IMenuDomain treecellValue, ILabelElement defaultTreecell) {
		super.setAttributes(treecellValue, defaultTreecell);
		defaultTreecell.setImage(treecellValue.getIconName());
	}

}
