package de.forsthaus.zksample.webui.util.searching;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SearchOperatorListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SearchOperatorListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SearchOperators searchOp = (SearchOperators) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + searchOp.getSearchOperatorName());
		}

		Listcell lc = new Listcell(searchOp.getSearchOperatorSign());
		lc.setParent(item);

		item.setAttribute("data", data);
	}

}
