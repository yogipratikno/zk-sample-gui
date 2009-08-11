package de.forsthaus.zksample.webui.security.right.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecTyp;

public class SecRightSecTypListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecRightSecTypListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecTyp typ = (SecTyp) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + typ.getStpTypname());
		}

		Listcell lc = new Listcell(typ.getStpTypname());
		lc.setParent(item);

		item.setAttribute("data", data);
	}

}
