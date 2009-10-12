/**
 * 
 */
package de.forsthaus.zksample.common.menu.tree;


import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import de.forsthaus.zksample.common.menu.util.ILabelElement;
import de.forsthaus.zksample.common.menu.util.MenuFactoryDto;
import de.forsthaus.zksample.common.menu.util.ZkossMenuFactory;

/**
 * @author bbruhns
 * 
 */
public class ZkossTreeMenuFactory extends ZkossMenuFactory {
	private static final long serialVersionUID = -1601202637698812546L;

	public static void addMainMenu(Component component) {
		new ZkossTreeMenuFactory(component);
	}

	/**
	 * @param component
	 */
	private ZkossTreeMenuFactory(Component component) {
		super(component);
	}

	protected MenuFactoryDto createMenuComponent(Component parent) {
		Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		ILabelElement item = insertTreeCell(treeitem);

		Treechildren treechildren = new Treechildren();
		treeitem.appendChild(treechildren);

		return new MenuFactoryDto(treechildren, item);
	}

	protected ILabelElement createItemComponent(Component parent) {
		Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		ILabelElement item = insertTreeCell(treeitem);

		return item;
	}

	private ILabelElement insertTreeCell(Component parent) {
		Treerow treerow = new Treerow();
		parent.appendChild(treerow);

		DefaultTreecell treecell = new DefaultTreecell();
		treerow.appendChild(treecell);

		return treecell;
	}
}
