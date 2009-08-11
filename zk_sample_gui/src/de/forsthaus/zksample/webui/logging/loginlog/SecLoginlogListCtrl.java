package de.forsthaus.zksample.webui.logging.loginlog;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.logging.loginlog.model.SecLoginlogListModelItemRenderer;
import de.forsthaus.zksample.webui.logging.loginlog.model.WorkingThreadLoginList;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secLoginLogList.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 * 
 */
public class SecLoginlogListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private transient final static Logger logger = Logger.getLogger(SecLoginlogListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secLoginlogListWindow; // autowired

	// filter components
	protected transient Checkbox checkbox_SecLoginlogList_ShowAll; // autowired
	protected transient Checkbox checkbox_SecLoginlogList_ShowOnlySuccess; // autowired
	protected transient Checkbox checkbox_SecLoginlogList_ShowOnlyFailed; // autowired
	protected transient Checkbox checkbox_SecLoginlogList_ServerPush; // autowired

	// bandbox for date period search
	protected transient Bandbox bandbox_SecLoginlogList_PeriodSearch; // autowired
	protected transient Bandpopup bpop_SecLoginlogList_PeriodSearch; // autowired
	protected transient Datebox dbox_LoginLog_DateFrom; // autowired
	protected transient Datebox dbox_LoginLog_DateTo; // autowired

	// search comps for LoginName
	protected transient Textbox tb_SecUserlog_LoginName; // aurowired

	// listbox secLoginLogList
	protected transient Borderlayout borderLayout_SecUserlogList; // autowired
	protected transient Paging paging_SecUserLogList; // autowired
	protected transient Listbox listBoxSecUserlog; // aurowired
	protected transient Listheader listheader_SecLoginlogList_lglLogtime; // autowired
	protected transient Listheader listheader_SecLoginlogList_lglLoginname; // autowired
	protected transient Listheader listheader_SecLoginlogList_lglStatusid; // autowired
	protected transient Listheader listheader_SecLoginlogList_lglIp; // autowired
	protected transient Listheader listheader_SecLoginlogList_lglSessionid; // autowired

	// Server push
	private transient Desktop desktop;
	private transient WorkingThreadLoginList thread;
	private transient WorkingThreadLoginList serverPush;

	private transient HibernateSearchObject<SecLoginlog> searchObjSecLoginlog;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient LoginLoggingService loginLoggingService;
	private transient TestService testService;

	public SecLoginlogListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$secLoginlogListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(secLoginlogListWindow);

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 158);
		countRows = Math.round(maxListBoxHeight / 16);
		// listBoxSecRights.setPageSize(countRows);

		borderLayout_SecUserlogList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecLoginlogList_ShowAll.setChecked(true);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		// set the paging params
		paging_SecUserLogList.setPageSize(countRows);
		paging_SecUserLogList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecLoginlogList_lglLogtime.setSortAscending(new FieldComparator("lglLogtime", true));
		listheader_SecLoginlogList_lglLogtime.setSortDescending(new FieldComparator("lglLogtime", false));
		listheader_SecLoginlogList_lglLoginname.setSortAscending(new FieldComparator("lglLoginname", true));
		listheader_SecLoginlogList_lglLoginname.setSortDescending(new FieldComparator("lglLoginname", false));
		listheader_SecLoginlogList_lglStatusid.setSortAscending(new FieldComparator("lglStatusid", true));
		listheader_SecLoginlogList_lglStatusid.setSortDescending(new FieldComparator("lglStatusid", false));
		listheader_SecLoginlogList_lglIp.setSortAscending(new FieldComparator("lglIp", true));
		listheader_SecLoginlogList_lglIp.setSortDescending(new FieldComparator("lglIp", false));
		listheader_SecLoginlogList_lglSessionid.setSortAscending(new FieldComparator("lglSessionid", true));
		listheader_SecLoginlogList_lglSessionid.setSortDescending(new FieldComparator("lglSessionid", false));

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));

		listBoxSecUserlog.setItemRenderer(new SecLoginlogListModelItemRenderer());

		// // Set the ListModel and the itemRenderer. The ZKoss ListmodelList do
		// in
		// // most times satisfy your needs
		// listBoxSecUserlog.setModel(new
		// ListModelList(getLoginLoggingService().getAllLogs()));
		// listBoxSecUserlog.setItemRenderer(new
		// SecLoginlogListModelItemRenderer());
		//
		// // Assign the Comparator for sorting secLoginLogList
		// listheader_SecLoginlogList_lglLogtime
		// .setSortAscending(new SecLoginlogComparator(true,
		// SecLoginlogComparator.FieldsEnum.LOG_LOGTIME));
		// listheader_SecLoginlogList_lglLogtime.setSortDescending(new
		// SecLoginlogComparator(false,
		// SecLoginlogComparator.FieldsEnum.LOG_LOGTIME));
		//
		// listheader_SecLoginlogList_lglLoginname.setSortAscending(new
		// SecLoginlogComparator(true,
		// SecLoginlogComparator.FieldsEnum.LOG_LOGINNAME));
		// listheader_SecLoginlogList_lglLoginname.setSortDescending(new
		// SecLoginlogComparator(false,
		// SecLoginlogComparator.FieldsEnum.LOG_LOGINNAME));
		//
		// listheader_SecLoginlogList_lglStatusid
		// .setSortAscending(new SecLoginlogComparator(true,
		// SecLoginlogComparator.FieldsEnum.LOG_STATUS));
		// listheader_SecLoginlogList_lglStatusid.setSortDescending(new
		// SecLoginlogComparator(false,
		// SecLoginlogComparator.FieldsEnum.LOG_STATUS));
		//
		// listheader_SecLoginlogList_lglIp.setSortAscending(new
		// SecLoginlogComparator(true,
		// SecLoginlogComparator.FieldsEnum.LOG_CLIENT_IP));
		// listheader_SecLoginlogList_lglIp
		// .setSortDescending(new SecLoginlogComparator(false,
		// SecLoginlogComparator.FieldsEnum.LOG_CLIENT_IP));
		//
		// listheader_SecLoginlogList_lglSessionid.setSortAscending(new
		// SecLoginlogComparator(true,
		// SecLoginlogComparator.FieldsEnum.LOG_SESSION_ID));
		// listheader_SecLoginlogList_lglSessionid.setSortDescending(new
		// SecLoginlogComparator(false,
		// SecLoginlogComparator.FieldsEnum.LOG_SESSION_ID));

	}

	public void onClose$secLoginlogListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecLoginlogList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecUserlog_LoginName.setValue(""); // clear
		checkbox_SecLoginlogList_ShowOnlySuccess.setChecked(false);
		checkbox_SecLoginlogList_ShowOnlyFailed.setChecked(false);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));
	}

	public void serverPushList1() {
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));
	}

	public void serverPushList2() {
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		getSearchObjSecLoginlog().addFilter(new Filter("lglLoginname", "admin", Filter.OP_EQUAL));

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));
	}

	/**
	 * when the checkBox 'only success' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecLoginlogList_ShowOnlySuccess(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecUserlog_LoginName.setValue(""); // clear
		checkbox_SecLoginlogList_ShowAll.setChecked(false);
		checkbox_SecLoginlogList_ShowOnlyFailed.setChecked(false);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		getSearchObjSecLoginlog().addFilter(new Filter("lglStatusid", 1, Filter.OP_EQUAL));

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));

	}

	/**
	 * when the checkBox 'only failed' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecLoginlogList_ShowOnlyFailed(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecUserlog_LoginName.setValue(""); // clear
		checkbox_SecLoginlogList_ShowAll.setChecked(false);
		checkbox_SecLoginlogList_ShowOnlySuccess.setChecked(false);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
		getSearchObjSecLoginlog().addSort("lglLogtime", true);

		getSearchObjSecLoginlog().addFilter(new Filter("lglStatusid", 0, Filter.OP_EQUAL));

		listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
				.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));

	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecLoginlogList_PrintLoginList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

	}

	/**
	 * Filter the logins log list with 'like LoginName'. <br>
	 * We check additionally if something is selected in the right type listbox <br>
	 * for including in the search statement.<br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecLoginlogList_SearchLoginName(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecUserlog_LoginName.getValue().isEmpty()) {
			checkbox_SecLoginlogList_ShowAll.setChecked(false); // clear

			// ++ create the searchObject and init sorting ++//
			setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
			getSearchObjSecLoginlog().addSort("lglLogtime", true);

			getSearchObjSecLoginlog().addFilter(new Filter("lglLoginname", tb_SecUserlog_LoginName.getValue(), Filter.OP_EQUAL));

			listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
					.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));
		}

	}

	// public void doServerRefreshList() {
	// System.out.println("--");
	// listBoxSecUserlog.setModel(new
	// ListModelList(getLoginLoggingService().getAllLogs()));
	// }

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Start the server push mechanism to refresh the login list. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onCheck$checkbox_SecLoginlogList_ServerPush(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		if (checkbox_SecLoginlogList_ServerPush.isChecked()) {
			doStartServerPush(event);
		} else {
			doStopServerPush(event);
		}
	}

	private void doStopServerPush(Event event) {

		if (serverPush != null) {
			try {
				serverPush.setDone();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void doStartServerPush(Event event) {

		Device dv = new AjaxDevice();
		dv.setServerPushClass(org.zkoss.zkmax.ui.comet.CometServerPush.class);

		if (!getDesktop().isServerPushEnabled()) {
			getDesktop().enableServerPush(true);
		}

		serverPush = new WorkingThreadLoginList(this, (Listbox) this.getFellow("listBoxSecUserlog"), getLoginLoggingService());
		serverPush.start();
	}

	/**
	 * onClick button help. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ bandbox search date period ++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button of the search bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_SecLoginlogList_bb_SearchClose(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		bandbox_SecLoginlogList_PeriodSearch.close();
	}

	/**
	 * onPopup the bandbox for searching over a date periode. <br>
	 * The datebox 'dateFrom' and 'dateTo' are init with the actual date.<br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onOpen$bandbox_SecLoginlogList_PeriodSearch(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		dbox_LoginLog_DateFrom.setValue(new Date());
		dbox_LoginLog_DateTo.setValue(new Date());
	}

	/**
	 * when the "search/filter" button is clicked. It searches over a period. <br>
	 * 
	 * @param event
	 */
	public void onClick$button_SecLoginlogList_bb_SearchDate(Event event) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		if ((!(dbox_LoginLog_DateFrom.getValue() == null)) && (!(dbox_LoginLog_DateTo.getValue() == null))) {

			if (dbox_LoginLog_DateFrom.getValue().after(dbox_LoginLog_DateTo.getValue())) {
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(Labels.getLabel("message_EndDate_Before_BeginDate"));
			} else {
				Date dateFrom = dbox_LoginLog_DateFrom.getValue();
				Date dateTo = dbox_LoginLog_DateTo.getValue();

				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(dateFrom);
				calFrom.set(Calendar.AM_PM, 0);
				calFrom.set(Calendar.HOUR, 0);
				calFrom.set(Calendar.MINUTE, 0);
				calFrom.set(Calendar.SECOND, 1);
				dateFrom = calFrom.getTime();

				Calendar calTo = Calendar.getInstance();
				calTo.setTime(dateTo);
				calTo.set(Calendar.AM_PM, 1);
				calTo.set(Calendar.HOUR, 11);
				calTo.set(Calendar.MINUTE, 59);
				calTo.set(Calendar.SECOND, 59);
				dateTo = calTo.getTime();

				// ++ create the searchObject and init sorting ++//
				setSearchObjSecLoginlog(new HibernateSearchObject(SecLoginlog.class));
				getSearchObjSecLoginlog().addSort("lglLogtime", true);

				getSearchObjSecLoginlog().addFilter(new Filter("lglLogtime", dateFrom, Filter.OP_GREATER_OR_EQUAL));
				getSearchObjSecLoginlog().addFilter(new Filter("lglLogtime", dateTo, Filter.OP_LESS_OR_EQUAL));

				listBoxSecUserlog.setModel(new PagedListWrapper<SecLoginlog>(listBoxSecUserlog, paging_SecUserLogList, getTestService()
						.getSRBySearchObject(getSearchObjSecLoginlog(), 0, countRows), getSearchObjSecLoginlog()));

				checkbox_SecLoginlogList_ShowAll.setChecked(false);

			}
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	public TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
			setTestService(testService);
		}
		return testService;
	}

	public void setSearchObjSecLoginlog(HibernateSearchObject<SecLoginlog> searchObjSecLoginlog) {
		this.searchObjSecLoginlog = searchObjSecLoginlog;
	}

	public HibernateSearchObject<SecLoginlog> getSearchObjSecLoginlog() {
		return searchObjSecLoginlog;
	}

}
