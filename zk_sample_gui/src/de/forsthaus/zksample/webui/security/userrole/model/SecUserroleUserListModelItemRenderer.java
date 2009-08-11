package de.forsthaus.zksample.webui.security.userrole.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecUser;

public class SecUserroleUserListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecUserroleUserListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecUser user = (SecUser) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + user.getUsrLoginname());
		}

		Listcell lc;

		lc = new Listcell(user.getUsrLoginname());
		lc.setParent(item);

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		ComponentsCtrl.applyForward(item, "onClick=onUserItemClicked");
		// ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

	}

}
