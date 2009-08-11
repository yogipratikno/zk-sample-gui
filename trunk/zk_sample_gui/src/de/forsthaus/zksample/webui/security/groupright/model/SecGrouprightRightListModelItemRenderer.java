package de.forsthaus.zksample.webui.security.groupright.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.zksample.webui.security.groupright.SecGrouprightCtrl;

public class SecGrouprightRightListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecGrouprightRightListModelItemRenderer.class);

	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecRight right = (SecRight) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + right.getRigName());
		}

		Listcell lc = null;

		lc = new Listcell();
		Checkbox cb = new Checkbox();
		// test
		SecGroup group = SecGrouprightCtrl.getSelectedGroup();

		if (group != null) {
			if (getSecurityService().isRightinGroup(right, group)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
		} else if (group == null) {
			cb.setChecked(false);
		}

		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell(right.getRigName());
		lc.setParent(item);

		SecTyp typ = getSecurityService().getTypById(right.getRigType());
		lc = new Listcell(String.valueOf(typ.getStpTypname()));
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

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
