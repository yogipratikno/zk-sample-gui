package de.forsthaus.zksample.webui.orderposition.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.Auftragposition;

public class OrderpositionListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(OrderpositionListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		Auftragposition auftragposition = (Auftragposition) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + auftragposition.getAupId());
		}

		Listcell lc = new Listcell(String.valueOf(auftragposition.getAupId()));
		lc.setParent(item);
		lc = new Listcell(auftragposition.getArtikel().getArtKurzbezeichnung());
		lc.setParent(item);
		lc = new Listcell(auftragposition.getAupMenge().toString());
		lc.setStyle("text-align: right");
		lc.setParent(item);
		lc = new Listcell(auftragposition.getAupEinzelwert().toString());
		lc.setStyle("text-align: right");
		lc.setParent(item);
		lc = new Listcell(auftragposition.getAupGesamtwert().toString());
		lc.setStyle("text-align: right");
		lc.setParent(item);

		// lc = new Listcell();
		// Image img = new Image();
		// img.setSrc("/images/icons/page_detail.gif");
		// lc.appendChild(img);
		// lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		ComponentsCtrl.applyForward(item, "onClick=onClickedOrderPositionItem");
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedOrderPositionItem");

	}
}
