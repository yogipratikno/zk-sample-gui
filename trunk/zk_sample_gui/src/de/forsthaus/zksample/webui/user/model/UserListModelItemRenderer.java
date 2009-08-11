package de.forsthaus.zksample.webui.user.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecUser;

public class UserListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(UserListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecUser user = (SecUser) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + user.getUsrLoginname() + "|" + user.getUsrLastname());
		}

		Listcell lc;
		lc = new Listcell(user.getUsrLoginname());
		lc.setParent(item);
		lc = new Listcell(user.getUsrLastname());
		lc.setParent(item);
		lc = new Listcell(user.getUsrEmail());
		lc.setParent(item);

		lc = new Listcell();
		Checkbox cb = new Checkbox();
		cb.setChecked(user.isUsrEnabled());
		cb.setDisabled(true);
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setChecked(user.isUsrAccountnonexpired());
		cb.setDisabled(true);
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setChecked(user.isUsrCredentialsnonexpired());
		cb.setDisabled(true);
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setChecked(user.isUsrAccountnonlocked());
		cb.setDisabled(true);
		lc.appendChild(cb);
		lc.setParent(item);

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		ComponentsCtrl.applyForward(item, "onDoubleClick=onUserListItemDoubleClicked");

	}

}
