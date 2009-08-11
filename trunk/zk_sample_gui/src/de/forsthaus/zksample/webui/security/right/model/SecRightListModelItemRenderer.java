package de.forsthaus.zksample.webui.security.right.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;

public class SecRightListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecRightListModelItemRenderer.class);

	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecRight right = (SecRight) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + right.getRigName());
		}

		Listcell lc;

		lc = new Listcell(right.getRigName());
		lc.setParent(item);

		SecTyp typ = getSecurityService().getTypById(right.getRigType());

		lc = new Listcell(String.valueOf(typ.getStpTypname()));
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedRightItem");

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
