package de.forsthaus.zksample.webui.customer.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.Branche;

public class CustomerBrancheListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(CustomerBrancheListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		Branche branche = (Branche) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + branche.getBraNr() + "|" + branche.getBraBezeichnung());
		}

		Listcell lc = new Listcell(branche.getBraBezeichnung());
		lc.setParent(item);

		item.setAttribute("data", data);
	}

}
