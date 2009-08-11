/**
 * This class is only used for designing the home menuItem 
 * like the other used menuItems.
 */
package de.forsthaus.zksample.common.menu.sub;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;

import de.forsthaus.zksample.common.menu.util.TreecellValue;
import de.forsthaus.zksample.common.menu.util.ZkossMenuUtil;

public class HomeMenuTree implements Serializable {

	private static final long serialVersionUID = -563952876714738088L;

	public HomeMenuTree(ZkossMenuUtil menuUtil) {
		super();
		TreecellValue value = new TreecellValue();

		menuUtil.addTreeitem(value.setAll("menu_Item_Home", "/WEB-INF/pages/welcome.zul", null, Labels.getLabel("menu_Item_Home")));

		menuUtil.addTreeitem(value.setAll("menu_Item_Chat", "/WEB-INF/pages/chat/chat.zul", null, Labels.getLabel("menu_Item_Chat")));
	}
}
