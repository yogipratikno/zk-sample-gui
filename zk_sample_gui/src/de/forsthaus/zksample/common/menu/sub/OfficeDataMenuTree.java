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
public class OfficeDataMenuTree implements Serializable {

	private static final long serialVersionUID = 544475710333018350L;

	public OfficeDataMenuTree(ZkossMenuUtil menuUtil) {
		super();

		TreecellValue value = new TreecellValue();

		if (menuUtil.addSubMenu(value.setAll("menu_Category_OfficeData", null, "menuCat_OfficeData", Labels
				.getLabel("menu_Category_OfficeData")))) {

			menuUtil.addTreeitem(value.setAll("menu_Item_Customers", "/WEB-INF/pages/customer/customerList.zul",
					"menuItem_OfficeData_Customers", Labels.getLabel("menu_Item_Customers")));
			menuUtil.addTreeitem(value.setAll("menu_Item_Orders", "/WEB-INF/pages/order/orderList.zul", "menuItem_OfficeData_Orders",
					Labels.getLabel("menu_Item_Orders")));

			menuUtil.ebeneHoch();
		}
	}
}
