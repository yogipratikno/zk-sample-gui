package de.forsthaus.zksample.webui.security.userrole;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.model.SecUserrole;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.groupright.SecGrouprightCtrl;
import de.forsthaus.zksample.webui.security.userrole.model.SecUserroleRoleListModelItemRenderer;
import de.forsthaus.zksample.webui.security.userrole.model.SecUserroleUserListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secUserrole.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * This is the controller class for the Security UserRole Page described in the
 * secUserrole.zul file. <br>
 * <br>
 * This page allows the visual editing of three tables that represents<br>
 * roles that belongs to users. <br>
 * <br>
 * 1. Users (table: secUser) <br>
 * 2. Roles (table: secRole) <br>
 * 3. User-Roles (table: secUserrole - The intersection of the two tables <br>
 * <br>
 * for working:
 * 
 * @see SecGrouprightCtrl
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class SecUserroleCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(SecUserroleCtrl.class);

	// for init the rendering of the rights
	public transient static SecUser selectedUser;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secUserroleWindow; // autowired

	// area listBox SecUser
	protected transient Borderlayout borderLayout_Users; // autowired
	protected transient Paging paging_ListBoxSecUser; // autowired
	protected transient Listbox listBoxSecUser; // autowired
	protected transient Listheader listheader_SecUserRole_usrLoginname; // autowired

	// area listBox SecUserRoles
	protected transient Borderlayout borderLayout_Roles; // autowired
	protected transient Paging paging_ListBoxSecRoles; // autowired
	protected transient Listbox listBoxSecRoles; // autowired
	protected transient Listheader listheader_SecUserRole_GrantedRight; // autowired
	protected transient Listheader listheader_SecUserRole_RoleName; // autowired

	// CRUD Buttons
	protected transient Button btnSave; // autowired
	protected transient Button btnClose; // autowired

	// search objects
	private transient HibernateSearchObject<SecUser> searchObjSecUser;
	private transient HibernateSearchObject<SecRole> searchObjSecRole;

	// row count for listbox
	private transient int countRowsSecUser;
	private transient int countRowsSecRole;

	protected transient Borderlayout borderlayoutSecUserrole; // autowired

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;
	private transient UserService userService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecUserroleCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$secUserroleWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(secUserroleWindow, event);

		/* ++++ calculate the heights +++++ */
		int topHeader = 30;
		int btnTopArea = 30;
		int winTitle = 25;

		secUserroleWindow.setHeight((UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader) + "px");
		//System.out.println(UserWorkspace.getInstance().getCurrentDesktopHeight
		// ());

		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader - btnTopArea - winTitle);
		countRowsSecUser = 20;
		countRowsSecRole = 20;

		/* set the PageSize */
		paging_ListBoxSecUser.setPageSize(countRowsSecUser);
		paging_ListBoxSecUser.setDetailed(true);

		paging_ListBoxSecRoles.setPageSize(countRowsSecRole);
		paging_ListBoxSecRoles.setDetailed(true);

		// main borderlayout height = window.height - (Panels Top)
		borderlayoutSecUserrole.setHeight(String.valueOf(maxListBoxHeight) + "px");
		borderLayout_Users.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");
		borderLayout_Roles.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");

		listBoxSecUser.setHeight(String.valueOf(maxListBoxHeight - 150) + "px");
		listBoxSecRoles.setHeight(String.valueOf(maxListBoxHeight - 150) + "px");
		/* Tab Details */
		// ++ create the searchObject and init sorting ++//
		setSearchObjSecUser(new HibernateSearchObject(SecUser.class));
		getSearchObjSecUser().addSort("usrLoginname", false);

		listBoxSecUser.setModel(new PagedListWrapper<SecUser>(listBoxSecUser, paging_ListBoxSecUser, getTestService().getSRBySearchObject(
				getSearchObjSecUser(), 0, countRowsSecUser), getSearchObjSecUser()));

		listBoxSecUser.setItemRenderer(new SecUserroleUserListModelItemRenderer());

		// Before we set the ItemRenderer we select the first Item
		// for init the rendering of the rights
		setSelectedUser((SecUser) listBoxSecUser.getModel().getElementAt(0));
		listBoxSecUser.setSelectedIndex(0);

		listBoxSecRoles.setModel(new ListModelList(getSecurityService().getAllRoles()));
		listBoxSecRoles.setItemRenderer(new SecUserroleRoleListModelItemRenderer());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		// Assign the Comparator for sorting listbox secUserList
		listheader_SecUserRole_usrLoginname.setSortAscending(new FieldComparator("usrLoginname", true));
		listheader_SecUserRole_usrLoginname.setSortDescending(new FieldComparator("usrLoginname", false));

		// Assign the Comparator for sorting listbox secUserRolesList
		listheader_SecUserRole_RoleName.setSortAscending(new FieldComparator("rolShortdescription", true));
		listheader_SecUserRole_RoleName.setSortDescending(new FieldComparator("rolShortdescription", false));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "save" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSave();

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

	/**
	 * when the "close" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		secUserroleWindow.onClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * doSave(). This method saves the status of the checkboxed right-item.<br>
	 * <br>
	 * 1. we iterate over all items in the listbox <br>
	 * 2. for each 'checked item' we must check if it is 'newly' checked <br>
	 * 3. if newly than get a new object first and <b>save</b> it to DB. <br>
	 * 4. for each 'unchecked item' we must check if it newly unchecked <br>
	 * 5. if newly unchecked we must <b>delete</b> this item from DB. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		List<Listitem> li = listBoxSecRoles.getItems();

		for (Listitem listitem : li) {

			Listcell lc = (Listcell) listitem.getFirstChild();
			Checkbox cb = (Checkbox) lc.getFirstChild();

			if (cb != null) {

				if (cb.isChecked() == true) {

					// Get the role object by casting
					SecRole role = (SecRole) listitem.getAttribute("data");
					// get the user
					SecUser user = getSelectedUser();

					// check if the item is newly checked. If so we cannot
					// found it in the SecUserrole-table
					SecUserrole userRole = getSecurityService().getUserroleByUserAndRole(user, role);

					// if new, we make a newly Object
					if (userRole == null) {
						userRole = getSecurityService().getNewSecUserrole();
						userRole.setSecUser(user);
						userRole.setSecRole(role);
					}

					try {
						// save to DB
						getSecurityService().saveOrUpdate(userRole);
					} catch (Exception e) {
						String message = e.getMessage();
						// String message = e.getCause().getMessage();
						String title = Labels.getLabel("message_Error");
						MultiLineMessageBox.doSetTemplate();
						MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "ERROR", true);
					}

				} else if (cb.isChecked() == false) {

					// Get the role object by casting
					SecRole role = (SecRole) listitem.getAttribute("data");
					// get the user
					SecUser user = getSelectedUser();

					// check if the item is newly checked. If so we cannot
					// found it in the SecUserrole-table
					SecUserrole userRole = getSecurityService().getUserroleByUserAndRole(user, role);

					if (userRole != null) {
						// delete from DB
						getSecurityService().delete(userRole);
					}
				}
			}
		}

	}

	public void onUserItemClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecUser.getSelectedItem();

		// Casting
		SecUser user = (SecUser) item.getAttribute("data");
		setSelectedUser(user);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRole(new HibernateSearchObject(SecRole.class));
		getSearchObjSecRole().addSort("rolShortdescription", false);

		listBoxSecRoles.setModel(new PagedListWrapper<SecRole>(listBoxSecRoles, paging_ListBoxSecRoles, getTestService()
				.getSRBySearchObject(getSearchObjSecRole(), 0, countRowsSecRole), getSearchObjSecRole()));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		if (userService == null) {
			userService = (UserService) SpringUtil.getBean("userService");
			setUserService(userService);
		}

		return userService;
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

	public void setSelectedUser(SecUser user) {
		this.selectedUser = user;
	}

	public static SecUser getSelectedUser() {
		return selectedUser;
	}

	public void setSearchObjSecRole(HibernateSearchObject<SecRole> searchObjSecRole) {
		this.searchObjSecRole = searchObjSecRole;
	}

	public HibernateSearchObject<SecRole> getSearchObjSecRole() {
		return searchObjSecRole;
	}

	public void setSearchObjSecUser(HibernateSearchObject<SecUser> searchObjSecUser) {
		this.searchObjSecUser = searchObjSecUser;
	}

	public HibernateSearchObject<SecUser> getSearchObjSecUser() {
		return searchObjSecUser;
	}

}
