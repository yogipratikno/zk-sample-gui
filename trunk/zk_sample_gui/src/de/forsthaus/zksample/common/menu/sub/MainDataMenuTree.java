/**
 * 
 */
package de.forsthaus.zksample.common.menu.sub;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;

import de.forsthaus.zksample.common.menu.util.TreecellValue;
import de.forsthaus.zksample.common.menu.util.ZkossMenuUtil;

/**
 * @author bj
 * 
 */
public class MainDataMenuTree implements Serializable {

	private static final long serialVersionUID = -8775252046873965151L;

	public MainDataMenuTree(ZkossMenuUtil menuUtil) {
		super();

		TreecellValue value = new TreecellValue();
		if (menuUtil
				.addSubMenu(value.setAll("menu_Category_MainData", null, "menuCat_MainData", Labels.getLabel("menu_Category_MainData")))) {

			menuUtil.addTreeitem(value.setAll("menu_Item_Articles", "/WEB-INF/pages/article/articleList.zul",
					"menuItem_MainData_ArticleItems", Labels.getLabel("menu_Item_Articles")));
			menuUtil.addTreeitem(value.setAll("menu_Item_Branches", "/WEB-INF/pages/branch/branchList.zul", "menuItem_MainData_Branch",
					Labels.getLabel("menu_Item_Branches")));

			menuUtil.ebeneHoch();
		}
	}
}
