package de.forsthaus.zksample.webui.security.rolegroup.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecRole;

public class SecRolegroupRoleListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecRolegroupRoleListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecRole role = (SecRole) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + role.getRolShortdescription());
		}

		Listcell lc;

		lc = new Listcell(role.getRolShortdescription());
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onClick=onRoleItemClicked");

	}

}
