package de.forsthaus.zksample.webui.security.groupright.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecGroupright;
import de.forsthaus.backend.service.SecurityService;

public class SecGrouprightListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecGrouprightListModelItemRenderer.class);

	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecGroupright groupRight = (SecGroupright) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + groupRight.getSecGroup().getGrpShortdescription());
		}

		Listcell lc = null;
		lc = new Listcell(groupRight.getSecGroup().getGrpShortdescription());
		lc.setParent(item);
		lc = new Listcell(groupRight.getSecRight().getRigName());
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
