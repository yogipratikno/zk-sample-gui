package de.forsthaus.zksample.webui.security.rolegroup.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.zksample.webui.security.rolegroup.SecRolegroupCtrl;

public class SecRolegroupGroupListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecRolegroupGroupListModelItemRenderer.class);

	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecGroup group = (SecGroup) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + group.getGrpShortdescription());
		}

		Listcell lc = null;

		lc = new Listcell();
		Checkbox cb = new Checkbox();
		// test
		SecRole role = SecRolegroupCtrl.getSelectedRole();

		//
		if (role != null) {
			if (getSecurityService().isGroupInRole(group, role)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
		} else if (role == null) {
			cb.setChecked(false);
		}

		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell(group.getGrpShortdescription());
		lc.setParent(item);

		item.setAttribute("data", data);
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
