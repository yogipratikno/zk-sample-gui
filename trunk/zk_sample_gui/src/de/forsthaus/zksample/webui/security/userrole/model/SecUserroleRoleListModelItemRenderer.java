package de.forsthaus.zksample.webui.security.userrole.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.zksample.webui.security.userrole.SecUserroleCtrl;

public class SecUserroleRoleListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecUserroleRoleListModelItemRenderer.class);

	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecRole role = (SecRole) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + role.getRolShortdescription());
		}

		Listcell lc = null;

		lc = new Listcell();
		Checkbox cb = new Checkbox();
		// test
		SecUser user = SecUserroleCtrl.getSelectedUser();

		if (user != null) {
			if (getSecurityService().isUserInRole(user, role)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
		} else if (user == null) {
			cb.setChecked(false);
		}

		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell(role.getRolShortdescription());
		lc.setParent(item);

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		// ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public SecurityService getSecurityService() {
		if (securityService == null) {
			securityService = (SecurityService) SpringUtil.getBean("securityService");
			setSecurityService(securityService);
		}
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

}
