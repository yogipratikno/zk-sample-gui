package de.forsthaus.zksample.webui.customer.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.Kunde;

public class CustomerListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1925499383404057064L;
	private transient final static Logger logger = Logger.getLogger(CustomerListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		Kunde kunde = (Kunde) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + kunde.getKunMatchcode() + ", " + kunde.getKunOrt());
		}

		Listcell lc = new Listcell(kunde.getKunNr());
		lc.setParent(item);
		lc = new Listcell(kunde.getKunMatchcode());
		lc.setParent(item);
		lc = new Listcell(kunde.getKunName1());
		lc.setParent(item);
		lc = new Listcell(kunde.getKunName2());
		lc.setParent(item);
		lc = new Listcell(kunde.getKunOrt());
		lc.setParent(item);

		lc = new Listcell();
		Checkbox cb = new Checkbox();
		cb.setDisabled(true);
		cb.setChecked(kunde.getKunMahnsperre());
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
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerItemDoubleClicked");

	}

}
