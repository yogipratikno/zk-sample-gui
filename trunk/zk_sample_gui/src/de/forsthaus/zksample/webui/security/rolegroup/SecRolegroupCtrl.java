package de.forsthaus.zksample.webui.security.rolegroup;

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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.model.SecRolegroup;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.groupright.SecGrouprightCtrl;
import de.forsthaus.zksample.webui.security.rolegroup.model.SecRolegroupGroupListModelItemRenderer;
import de.forsthaus.zksample.webui.security.rolegroup.model.SecRolegroupRoleListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secRolegroup.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * This is the controller class for the Security RoleGroup Page described in the
 * secRolegroup.zul file. <br>
 * <br>
 * This page allows the visual editing of three tables that represents<br>
 * groups that belongs to roles. <br>
 * <br>
 * 1. Roles (table: secRole) <br>
 * 2. Groups (table: secGroup) <br>
 * 3. Role-Groups (table: secRolegroup - The intersection of the two tables <br>
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
public class SecRolegroupCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(SecRolegroupCtrl.class);

	// init the rendering of the rights
	public transient static SecRole selectedRole;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secRolegroupWindow; // autowired

	// listbox SecRoles
	protected transient Borderlayout borderLayout_Roles; // autowired
	protected transient Paging paging_ListBoxSecRole; // autowired
	protected transient Listbox listBoxSecRole; // autowired
	protected transient Listheader listheader_SecRoleGroup_Rolename; // autowired

	// listbox granted groups
	protected transient Borderlayout borderLayout_Groups; // autowired
	protected transient Paging paging_ListBoxSecRolegroup; // autowired
	protected transient Listbox listBoxSecRolegroup; // autowired
	protected transient Listheader listheader_SecRoleGroup_GrantedRight; // autowired
	protected transient Listheader listheader_SecRoleGroup_GroupName; // autowired

	// CRUD Buttons
	protected transient Button btnSave; // autowired
	protected transient Button btnClose; // autowired

	protected transient Borderlayout borderlayoutSecRolegroup; // autowired

	// search objects
	private transient HibernateSearchObject<SecRole> searchObjSecRole;
	private transient HibernateSearchObject<SecRolegroup> searchObjSecRolegroup;

	// row count for listbox
	private transient int countRowsSecRole;
	private transient int countRowsSecRolegroup;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecRolegroupCtrl() {
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
	public void onCreate$secRolegroupWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(secRolegroupWindow, event);

		/* ++++ calculate the heights +++++ */
		int topHeader = 30;
		int btnTopArea = 30;
		int winTitle = 25;

		secRolegroupWindow.setHeight((UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader) + "px");

		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader - btnTopArea - winTitle);
		countRowsSecRole = 20;
		countRowsSecRolegroup = 20;

		/* set the PageSize */
		paging_ListBoxSecRole.setPageSize(countRowsSecRole);
		paging_ListBoxSecRole.setDetailed(true);

		paging_ListBoxSecRolegroup.setPageSize(countRowsSecRolegroup);
		paging_ListBoxSecRolegroup.setDetailed(true);

		// main borderlayout height = window.height - (Panels Top)
		borderlayoutSecRolegroup.setHeight(String.valueOf(maxListBoxHeight) + "px");
		borderLayout_Roles.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");
		borderLayout_Groups.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");

		listBoxSecRole.setHeight(String.valueOf(maxListBoxHeight - 150) + "px");
		listBoxSecRolegroup.setHeight(String.valueOf(maxListBoxHeight - 150) + "px");

		/* Tab Details */
		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRole(new HibernateSearchObject(SecRole.class));
		getSearchObjSecRole().addSort("rolShortdescription", false);

		listBoxSecRole.setModel(new PagedListWrapper<SecRole>(listBoxSecRole, paging_ListBoxSecRole, getTestService().getSRBySearchObject(
				getSearchObjSecRole(), 0, countRowsSecRole), getSearchObjSecRole()));

		// listBoxSecRole.setModel(new
		// ListModelList(getSecurityService().getAllRoles()));
		listBoxSecRole.setItemRenderer(new SecRolegroupRoleListModelItemRenderer());

		// Before we set the ItemRenderer we select the first Item
		// for init the rendering of the rights
		setSelectedRole((SecRole) listBoxSecRole.getModel().getElementAt(0));
		listBoxSecRole.setSelectedIndex(0);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRolegroup(new HibernateSearchObject(SecGroup.class));
		getSearchObjSecRolegroup().addSort("grpShortdescription", false);

		listBoxSecRolegroup.setModel(new PagedListWrapper<SecRolegroup>(listBoxSecRolegroup, paging_ListBoxSecRolegroup, getTestService()
				.getSRBySearchObject(getSearchObjSecRolegroup(), 0, countRowsSecRolegroup), getSearchObjSecRolegroup()));

		// listBoxSecRolegroup.setModel(new
		// ListModelList(getSecurityService().getAllGroups()));
		listBoxSecRolegroup.setItemRenderer(new SecRolegroupGroupListModelItemRenderer());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		// Assign the Comparator for sorting listBoxSecRole
		listheader_SecRoleGroup_Rolename.setSortAscending(new FieldComparator("rolShortdescription", true));
		listheader_SecRoleGroup_Rolename.setSortDescending(new FieldComparator("rolShortdescription", false));

		// Assign the Comparator for sorting listBoxRoleGroups
		listheader_SecRoleGroup_GroupName.setSortAscending(new FieldComparator("grpShortdescription", true));
		listheader_SecRoleGroup_GroupName.setSortDescending(new FieldComparator("grpShortdescription", false));

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
		secRolegroupWindow.onClose();
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

		List<Listitem> li = listBoxSecRolegroup.getItems();

		for (Listitem listitem : li) {

			Listcell lc = (Listcell) listitem.getFirstChild();
			Checkbox cb = (Checkbox) lc.getFirstChild();

			if (cb != null) {

				if (cb.isChecked() == true) {

					// Get the group object by casting
					SecGroup group = (SecGroup) listitem.getAttribute("data");
					// get the role
					SecRole role = getSelectedRole();

					// check if the item is newly checked. If so we cannot found
					// it in the SecGroupRight-table
					SecRolegroup roleGroup = getSecurityService().getRolegroupByRoleAndGroup(role, group);

					// if new, we make a newly Object
					if (roleGroup == null) {
						roleGroup = getSecurityService().getNewSecRolegroup();
						roleGroup.setSecGroup(group);
						roleGroup.setSecRole(role);
					}

					try {
						// save to DB
						getSecurityService().saveOrUpdate(roleGroup);
					} catch (Exception e) {
						String message = e.getMessage();
						// String message = e.getCause().getMessage();
						String title = Labels.getLabel("message_Error");
						MultiLineMessageBox.doSetTemplate();
						MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "ERROR", true);
					}

				} else if (cb.isChecked() == false) {

					// Get the group object by casting
					SecGroup group = (SecGroup) listitem.getAttribute("data");
					// get the role
					SecRole role = getSelectedRole();

					// check if the item is newly unChecked. If so we must
					// found it in the SecRolegroup-table
					SecRolegroup roleGroup = getSecurityService().getRolegroupByRoleAndGroup(role, group);

					if (roleGroup != null) {
						// delete from DB
						getSecurityService().delete(roleGroup);
					}
				}
			}
		}

	}

	public void onRoleItemClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecRole.getSelectedItem();

		// Casting
		SecRole role = (SecRole) item.getAttribute("data");
		setSelectedRole(role);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRolegroup(new HibernateSearchObject(SecGroup.class));
		getSearchObjSecRolegroup().addSort("grpShortdescription", false);

		listBoxSecRolegroup.setModel(new PagedListWrapper<SecGroup>(listBoxSecRolegroup, paging_ListBoxSecRolegroup, getTestService()
				.getSRBySearchObject(getSearchObjSecRolegroup(), 0, countRowsSecRolegroup), getSearchObjSecRolegroup()));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setSelectedRole(SecRole role) {
		this.selectedRole = role;
	}

	public static SecRole getSelectedRole() {
		return selectedRole;
	}

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

	public void setSearchObjSecRole(HibernateSearchObject<SecRole> searchObjSecRole) {
		this.searchObjSecRole = searchObjSecRole;
	}

	public HibernateSearchObject<SecRole> getSearchObjSecRole() {
		return searchObjSecRole;
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

	public void setSearchObjSecRolegroup(HibernateSearchObject<SecRolegroup> searchObjSecRolegroup) {
		this.searchObjSecRolegroup = searchObjSecRolegroup;
	}

	public HibernateSearchObject<SecRolegroup> getSearchObjSecRolegroup() {
		return searchObjSecRolegroup;
	}

}