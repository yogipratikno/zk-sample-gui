package de.forsthaus.zksample.webui.login;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import de.forsthaus.zksample.webui.util.BaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the ZKLoginDialog.zul. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering
 * 
 */
public class ZkLoginDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -71422545405325060L;
	private transient final static Logger logger = Logger.getLogger(ZkLoginDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window loginwin; // autowired
	protected transient Label lbl_ServerTime; // autowired

	/**
	 * default constructor.<br>
	 */
	public ZkLoginDialogCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super() ");
		}
	}

	public void onCreate$loginwin(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(loginwin); // do the autowire

		loginwin.doModal();

	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @throws IOException
	 */
	public void onClick$button_ZKLoginDialog_Close() throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("-->");
		}

		Executions.sendRedirect("/j_spring_logout");
	}

	/**
	 * when the "getServerTime" button is clicked. <br>
	 * 
	 * @throws IOException
	 */
	public void onClick$button_ZKLoginDialog_ServerTime() throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> get the server date/time");
		}

		lbl_ServerTime.setValue("time on server: " + getDateTime());
	}

	/**
	 * Get the actual date/time on server. <br>
	 * 
	 * @return String of date/time
	 */
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
