package de.forsthaus.zksample.webui.guestbook.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.GuestBook;

public class GuestBookListtemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(GuestBookListtemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		GuestBook guestBook = (GuestBook) data;

		// if (logger.isDebugEnabled()) {
		// logger.debug("--> " + branche.getBraNr() + "|" +
		// branche.getBraBezeichnung());
		// }
		Listcell lc = null;
		// lc = new Listcell(guestBook.getGubDate().toString());
		lc = new Listcell(getDateTime(guestBook.getGubDate()));
		lc.setParent(item);
		lc = new Listcell(guestBook.getGubUsrname());
		lc.setParent(item);
		lc = new Listcell(guestBook.getGubSubject());
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onClick=onGuestBookItemClicked");
		ComponentsCtrl.applyForward(item, "onDoubleClick=onGuestBookItemDoubleClicked");
	}

	/**
	 * Format the date/time. <br>
	 * 
	 * @return String of date/time
	 */
	private String getDateTime(Date date) {
		String result = "";

		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			return dateFormat.format(date);
		} else {
			return "";
		}
	}
}
