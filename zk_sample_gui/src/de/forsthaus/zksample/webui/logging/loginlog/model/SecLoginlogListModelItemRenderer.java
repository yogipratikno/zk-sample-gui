package de.forsthaus.zksample.webui.logging.loginlog.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.LoginStatus;
import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.backend.service.SecurityService;

public class SecLoginlogListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(SecLoginlogListModelItemRenderer.class);

	private transient LoginLoggingService loginLoggingService;
	private transient SecurityService securityService;

	@Override
	public void render(Listitem item, Object data) throws Exception {

		SecLoginlog log = (SecLoginlog) data;

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + log.getLglLogtime() + "/ " + log.getLglLoginname());
		}

		Listcell lc;
		LoginStatus loginStatus = getLoginLoggingService().getTypById(log.getLglStatusid());

		// lc = new Listcell(log.getLglLogtime().toString());
		lc = new Listcell(getDateTime(log.getLglLogtime()));

		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(log.getLglLoginname());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(loginStatus.getStpTypname());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(log.getLglIp());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(log.getLglSessionid());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		// ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SecurityService getSecurityService() {
		if (securityService == null) {
			securityService = (SecurityService) SpringUtil.getBean("securityService");
			setSecurityService(securityService);
		}
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public LoginLoggingService getLoginLoggingService() {
		if (loginLoggingService == null) {
			loginLoggingService = (LoginLoggingService) SpringUtil.getBean("loginLoggingService");
			setLoginLoggingService(loginLoggingService);
		}
		return loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

}
