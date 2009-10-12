/**
 * 
 */
package de.forsthaus.zksample.common.menu.dropdown;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;

import de.forsthaus.zksample.common.menu.util.ILabelElement;

/**
 * @author sge
 * 
 */
class DefaultDropDownMenuItem extends Menuitem implements EventListener, Serializable, ILabelElement {

	private static final long serialVersionUID = -2813840859147955432L;
	private transient static final Logger logger = Logger.getLogger(DefaultDropDownMenuItem.class);

	private transient String zulNavigation;

	@Override
	public void onEvent(Event event) throws Exception {
		try {
			/* get an instance of the borderlayout defined in the zul-file */
			Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			/* get an instance of the searched CENTER layout area */
			Center center = bl.getCenter();
			center.setFlex(true);
			/* clear the center child comps */
			center.getChildren().clear();
			/*
			 * create the page and put it in the center layout area
			 */
			Executions.createComponents(getZulNavigation(), center, null);

			if (logger.isDebugEnabled()) {
				logger.debug("-->[" + getId() + "] calling zul-file: " + getZulNavigation());
			}
		} catch (Exception e) {
			Messagebox.show(e.toString());
		}
	}

	private String getZulNavigation() {
		return this.zulNavigation;
	}

	public void setZulNavigation(String zulNavigation) {
		this.zulNavigation = zulNavigation;
		if (!StringUtils.isEmpty(zulNavigation)) {
			addEventListener("onClick", this);
		}
	}

}
