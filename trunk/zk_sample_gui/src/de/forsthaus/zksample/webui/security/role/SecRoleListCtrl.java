package de.forsthaus.zksample.webui.security.role;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.role.model.SecRoleListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secRoleList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class SecRoleListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private transient final static Logger logger = Logger.getLogger(SecRoleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secRoleListWindow; // autowired

	// filter components
	protected transient Checkbox checkbox_SecRoleList_ShowAll; // autowired
	protected transient Textbox tb_SecRole_RoleName; // autowired

	// Listbox SecRole
	protected transient Borderlayout borderLayout_secRolesList; // autowired
	protected transient Listbox listBoxSecRoles; // aurowired
	protected transient Paging paging_SecRoleList; // aurowired
	protected transient Listheader listheader_SecRoleList_rolShortdescription; // aurowired
	protected transient Listheader listheader_SecRoleList_rolLongdescription; // aurowired

	private transient HibernateSearchObject<SecRole> searchObjSecRole;

	// row count for listbox
	private transient int countRows;

	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecRoleListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$secRoleListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(secRoleListWindow);

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 158);
		countRows = Math.round(maxListBoxHeight / 17);
		// listBoxSecRoles.setPageSize(countRows);

		borderLayout_secRolesList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecRoleList_ShowAll.setChecked(true);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRole(new HibernateSearchObject(SecRole.class));
		getSearchObjSecRole().addSort("rolShortdescription", false);

		// set the paging params
		paging_SecRoleList.setPageSize(countRows);
		paging_SecRoleList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecRoleList_rolShortdescription.setSortAscending(new FieldComparator("rolShortdescription", true));
		listheader_SecRoleList_rolShortdescription.setSortDescending(new FieldComparator("rolShortdescription", false));
		listheader_SecRoleList_rolLongdescription.setSortAscending("");
		listheader_SecRoleList_rolLongdescription.setSortDescending("");

		listBoxSecRoles.setModel(new PagedListWrapper<SecRole>(listBoxSecRoles, paging_SecRoleList, getTestService().getSRBySearchObject(
				getSearchObjSecRole(), 0, countRows), getSearchObjSecRole()));

		listBoxSecRoles.setItemRenderer(new SecRoleListModelItemRenderer());

	}

	@SuppressWarnings("unchecked")
	public void onRoleItemDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecRoles.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			SecRole role = (SecRole) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + role.getRolShortdescription());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("role", role);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxSecRoles", listBoxSecRoles);
			map.put("secRoleListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/sec_role/secRoleDialog.zul", null, map);
			} catch (Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());

				// Show a error box
				String msg = e.getMessage();
				String title = Labels.getLabel("message_Error");

				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);

				if (win != null) {
					win.detach();
				}
			}

		}
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

	/*
	 * call the user dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecRoleList_NewSecRole(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		SecRole role = getSecurityService().getNewSecRole();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("role", role);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecRoles", listBoxSecRoles);
		map.put("secRoleListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/sec_role/secRoleDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			// Show a error box
			String msg = e.getMessage();
			String title = Labels.getLabel("message_Error");

			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);

			if (win != null) {
				win.detach();
			}
		}

	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecRoleList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecRole_RoleName.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRole(new HibernateSearchObject(SecRole.class));
		getSearchObjSecRole().addSort("rolShortdescription", false);

		listBoxSecRoles.setModel(new PagedListWrapper<SecRole>(listBoxSecRoles, paging_SecRoleList, getTestService().getSRBySearchObject(
				getSearchObjSecRole(), 0, countRows), getSearchObjSecRole()));

	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecRoleList_PrintSecRole(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Filter the role list with 'like RoleName'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecRoleList_rolShortdescription(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecRole_RoleName.getValue().isEmpty()) {
			checkbox_SecRoleList_ShowAll.setChecked(false); // unCheck

			// ++ create the searchObject and init sorting ++//
			setSearchObjSecRole(new HibernateSearchObject(SecRole.class));
			getSearchObjSecRole().addSort("rolShortdescription", false);

			getSearchObjSecRole().addFilter(new Filter("rolShortdescription", "%" + tb_SecRole_RoleName.getValue() + "%", Filter.OP_ILIKE));

			listBoxSecRoles.setModel(new PagedListWrapper<SecRole>(listBoxSecRoles, paging_SecRoleList, getTestService()
					.getSRBySearchObject(getSearchObjSecRole(), 0, countRows), getSearchObjSecRole()));
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

	public void setSearchObjSecRole(HibernateSearchObject<SecRole> searchObjSecRole) {
		this.searchObjSecRole = searchObjSecRole;
	}

	public HibernateSearchObject<SecRole> getSearchObjSecRole() {
		return searchObjSecRole;
	}

}
