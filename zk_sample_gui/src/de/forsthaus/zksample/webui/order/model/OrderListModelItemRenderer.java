package de.forsthaus.zksample.webui.order.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.Auftrag;

public class OrderListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(OrderListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		Auftrag auftrag = (Auftrag) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + auftrag.getAufNr() + "|" + auftrag.getAufBezeichnung());
		}

		Listcell lc = new Listcell(auftrag.getAufNr());
		lc.setParent(item);
		lc = new Listcell(auftrag.getAufBezeichnung());
		lc.setParent(item);

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedOrderItem");

	}

}
