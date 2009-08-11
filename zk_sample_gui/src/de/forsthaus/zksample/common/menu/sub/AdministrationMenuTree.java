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
public class AdministrationMenuTree implements Serializable {

	private static final long serialVersionUID = 3497268230108479263L;

	public AdministrationMenuTree(ZkossMenuUtil menuUtil) {
		super();

		TreecellValue value = new TreecellValue();

		if (menuUtil.addSubMenu(value.setAll("menu_Category_Administration", null, "menuCat_Administration", Labels
				.getLabel("menu_Category_Administration")))) {

			menuUtil.addTreeitem(value.setAll("menu_Item_Users", "/WEB-INF/pages/sec_user/userList.zul", "menuItem_Administration_Users",
					Labels.getLabel("menu_Item_Users")));

			if (menuUtil.addSubMenu(value.setAll("menu_Category_UserRights", null, "menuCat_UserRights", Labels
					.getLabel("menu_Category_UserRights")))) {

				menuUtil.addTreeitem(value.setAll("menu_Item_UserRoles", "/WEB-INF/pages/sec_userrole/secUserrole.zul",
						"menuItem_Administration_UserRoles", Labels.getLabel("menu_Item_UserRoles")));
				menuUtil.addTreeitem(value.setAll("menu_Item_Roles", "/WEB-INF/pages/sec_role/secRoleList.zul",
						"menuItem_Administration_Roles", Labels.getLabel("menu_Item_Roles")));
				menuUtil.addTreeitem(value.setAll("menu_Item_RoleGroups", "/WEB-INF/pages/sec_rolegroup/secRolegroup.zul",
						"menuItem_Administration_RoleGroups", Labels.getLabel("menu_Item_RoleGroups")));
				menuUtil.addTreeitem(value.setAll("menu_Item_Groups", "/WEB-INF/pages/sec_group/secGroupList.zul",
						"menuItem_Administration_Groups", Labels.getLabel("menu_Item_Groups")));
				menuUtil.addTreeitem(value.setAll("menu_Item_GroupRights", "/WEB-INF/pages/sec_groupright/secGroupright.zul",
						"menuItem_Administration_GroupRights", Labels.getLabel("menu_Item_GroupRights")));
				menuUtil.addTreeitem(value.setAll("menu_Item_Rights", "/WEB-INF/pages/sec_right/secRightList.zul",
						"menuItem_Administration_Rights", Labels.getLabel("menu_Item_Rights")));

				menuUtil.ebeneHoch();
			}

			menuUtil.addTreeitem(value.setAll("menu_Item_LoginsLog", "/WEB-INF/pages/sec_loginlog/secLoginLogList.zul",
					"menuItem_Administration_LoginsLog", Labels.getLabel("menu_Item_LoginsLog")));

			menuUtil.ebeneHoch();

		}
	}
}
