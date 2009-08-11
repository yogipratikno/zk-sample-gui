package de.forsthaus.zksample.webui.user.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.Language;

public class LanguageListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(LanguageListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		Language language = (Language) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + language.getLanText());
		}

		Listcell lc = new Listcell(language.getLanText());
		lc.setParent(item);

		item.setAttribute("data", data);

	}

}
