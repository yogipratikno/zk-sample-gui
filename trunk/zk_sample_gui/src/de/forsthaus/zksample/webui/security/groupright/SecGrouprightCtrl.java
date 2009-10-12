package de.forsthaus.zksample.webui.security.groupright;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecGroupright;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.groupright.model.SecGrouprightDialogGroupListModelItemRenderer;
import de.forsthaus.zksample.webui.security.groupright.model.SecGrouprightRightListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secGroupright.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * This is the controller class for the Security GroupRight Page described in
 * the secGroupright.zul file. <br>
 * <br>
 * This page allows the visual editing of three tables that represents<br>
 * rights that belongs to groups. <br>
 * <br>
 * 1. Groups (table: secGroup) <br>
 * 2. Rights (table: secRight) <br>
 * 3. Group-Rights (table: secGroupright - The intersection of the two tables <br>
 * <br>
 * The right listbox [Granted Rights] shows the scurity rights(Table secRights)<br>
 * and the granted rights (Table secGroupright). <br>
 * By rendering all rights in the ItemRenderer class we look for each item
 * (right) is there a corresponding record in the database with the
 * <b>right-id</b> and <b>group-id</b><br>
 * <br>
 * In the listBox for the Granted Rights we can limited the showing entries by
 * checking the various Filter Checkboxes for the secure type.
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class SecGrouprightCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(SecGrouprightCtrl.class);

	public transient static SecGroup selectedGroup;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secGroupRightWindow; // autowired
	protected transient Borderlayout borderlayoutSecGroupRight; // autowired

	// listBox secGroup
	protected transient Borderlayout borderLayout_Groups; // autowired
	protected transient Paging paging_ListBoxSecGroup;// autowired
	protected transient Listbox listBoxSecGroup;// autowired
	protected transient Listheader listheader_SecGroupRight_grpShortdescription; // autowired

	// area secGroupRights
	protected transient Checkbox checkbox_SecGroupRight_All; // autowired
	protected transient Checkbox checkbox_SecGroupRight_Pages; // autowired
	protected transient Checkbox checkbox_SecGroupRight_Tabs; // autowired
	protected transient Checkbox checkbox_SecGroupRight_MenuCat; // autowired
	protected transient Checkbox checkbox_SecGroupRight_MenuItems; // autowired
	protected transient Checkbox checkbox_SecGroupRight_Methods; // autowired
	protected transient Checkbox checkbox_SecGroupRight_Domain; // autowired
	protected transient Checkbox checkbox_SecGroupRight_Components; // autowired

	// listBox secGroupRights
	protected transient Tab tab_SecGroupRight_AllRights; // autowired
	protected transient Borderlayout borderLayout_Rights;
	protected transient Paging paging_ListBoxSecGroupRight; // autowired
	protected transient Listbox listBoxSecGroupRight;// autowired
	protected transient Listheader listheader_SecGroupRight_GrantedRight; // autowired
	protected transient Listheader listheader_SecGroupRight_RightName; // autowired
	protected transient Listheader listheader_SecGroupRight_Type; // autowired

	// listBox secGroupRights on Tab Details
	protected transient Tab tab_SecGroupRight_Details; // autowired
	protected transient Borderlayout borderLayout_Rights_TabDetails; // autowired
	protected transient Button button_GroupRight_NewGroupRight;// autowired
	protected transient Paging paging_ListBoxSecGroupRight_Details;// autowired
	protected transient Listbox listBoxSecGroupRight_Details; // autowired
	protected transient Listheader listheader_SecGroupRight_Details_GrantedRight; // autowired
	protected transient Listheader listheader_SecGroupRight_Details_RightName; // autowired
	protected transient Listheader listheader_SecGroupRight_Details_Type; // autowired

	// default CRUD buttons
	protected transient Button btnSave;
	protected transient Button btnClose;

	// search objects
	private transient HibernateSearchObject<SecGroup> searchObjSecGroup;
	private transient HibernateSearchObject<SecRight> searchObjSecRight;
	private transient HibernateSearchObject<SecRight> searchObjSecRightDetails;

	// row count for listbox
	private transient int countRowsGroup;
	private transient int countRowsRight;
	private transient int countRowsRightDetails;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecGrouprightCtrl() {
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
	public void onCreate$secGroupRightWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(secGroupRightWindow, event);

		/* ++++ calculate the heights +++++ */
		int topHeader = 30;
		int btnTopArea = 30;
		int winTitle = 25;

		secGroupRightWindow.setHeight((UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader) + "px");

		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - topHeader - btnTopArea - winTitle);
		// countRowsGroup = Math.round(maxListBoxHeight / 16);
		// countRowsRight = Math.round(maxListBoxHeight / 17);
		countRowsGroup = 20;
		countRowsRight = 20;
		countRowsRightDetails = 15;

		/* set the PageSize */
		paging_ListBoxSecGroup.setPageSize(countRowsGroup);
		paging_ListBoxSecGroup.setDetailed(true);

		paging_ListBoxSecGroupRight.setPageSize(countRowsRight);
		paging_ListBoxSecGroupRight.setDetailed(true);

		paging_ListBoxSecGroupRight_Details.setPageSize(countRowsRightDetails);
		paging_ListBoxSecGroupRight_Details.setDetailed(true);

		// main borderlayout height = window.height - (Panels Top)
		borderlayoutSecGroupRight.setHeight(String.valueOf(maxListBoxHeight) + "px");
		borderLayout_Groups.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");
		borderLayout_Rights.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");
		borderLayout_Rights_TabDetails.setHeight(String.valueOf(maxListBoxHeight - 5) + "px");

		listBoxSecGroup.setHeight(String.valueOf(maxListBoxHeight - 98) + "px");
		listBoxSecGroupRight.setHeight(String.valueOf(maxListBoxHeight - 125) + "px");
		listBoxSecGroupRight_Details.setHeight(String.valueOf(maxListBoxHeight - 133) + "px");

		/* Tab All */
		// ++ create the searchObject and init sorting ++//
		setSearchObjSecGroup(new HibernateSearchObject(SecGroup.class));
		getSearchObjSecGroup().addSort("grpShortdescription", false);

		listBoxSecGroup.setModel(new PagedListWrapper<SecGroup>(listBoxSecGroup, paging_ListBoxSecGroup, getTestService()
				.getSRBySearchObject(getSearchObjSecGroup(), 0, countRowsGroup), getSearchObjSecGroup()));

		listBoxSecGroup.setItemRenderer(new SecGrouprightDialogGroupListModelItemRenderer());

		// Before we set the ItemRenderer we select the first Item
		// for init the rendering of the rights
		setSelectedGroup((SecGroup) listBoxSecGroup.getModel().getElementAt(0));
		listBoxSecGroup.setSelectedIndex(0);

		// init, for displaying all rights entries in the table
		// sets the ListModel for the 'listBoxSecGroupRight'
		checkbox_SecGroupRight_All.setChecked(true);
		filterTypeForShowingRights();

		listBoxSecGroupRight.setItemRenderer(new SecGrouprightRightListModelItemRenderer());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecGroupRight_grpShortdescription.setSortAscending(new FieldComparator("grpShortdescription", true));
		listheader_SecGroupRight_grpShortdescription.setSortDescending(new FieldComparator("grpShortdescription", false));

		// Assign the Comparator for sorting GroupRights Listbox
		listheader_SecGroupRight_RightName.setSortAscending(new FieldComparator("rigName", true));
		listheader_SecGroupRight_RightName.setSortDescending(new FieldComparator("rigName", false));
		listheader_SecGroupRight_Type.setSortAscending(new FieldComparator("rigType", true));
		listheader_SecGroupRight_Type.setSortDescending(new FieldComparator("rigType", false));

		// Assign the Comparator for sorting GroupRights Listbox on Tab
		// "only granted rights"
		listheader_SecGroupRight_Details_GrantedRight.setSortAscending("");
		listheader_SecGroupRight_Details_GrantedRight.setSortDescending("");
		listheader_SecGroupRight_Details_RightName.setSortAscending(new FieldComparator("rigName", true));
		listheader_SecGroupRight_Details_RightName.setSortDescending(new FieldComparator("rigName", false));
		listheader_SecGroupRight_Details_Type.setSortAscending(new FieldComparator("rigType", true));
		listheader_SecGroupRight_Details_Type.setSortDescending(new FieldComparator("rigType", false));

		// tab_SecGroupRight_Details.setSelected(true);
		// onSelect$tab_SecGroupRight_Details(new Event("onSelect",
		// tab_SecGroupRight_Details));
	}

	/**
	 * Analyze wich type for filtering is checked and send the result list<br>
	 * to the DAO where with the values in the types-list <br>
	 * the select statement is dynamically build.<br>
	 */
	private void filterTypeForShowingRights() {

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRight(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRight().addSort("rigName", false);

		if (checkbox_SecGroupRight_All.isChecked()) {
			// nothing todo
		}
		if (checkbox_SecGroupRight_Pages.isChecked()) {
			// list.add(0);
			getSearchObjSecRight().addFilter(new Filter("rigType", 0, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_Tabs.isChecked()) {
			// list.add(5);
			getSearchObjSecRight().addFilter(new Filter("rigType", 5, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_MenuCat.isChecked()) {
			// list.add(1);
			getSearchObjSecRight().addFilter(new Filter("rigType", 1, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_MenuItems.isChecked()) {
			// list.add(2);
			getSearchObjSecRight().addFilter(new Filter("rigType", 2, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_Methods.isChecked()) {
			// list.add(3);
			getSearchObjSecRight().addFilter(new Filter("rigType", 3, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_Domain.isChecked()) {
			// list.add(4);
			getSearchObjSecRight().addFilter(new Filter("rigType", 4, Filter.OP_EQUAL));
		}
		if (checkbox_SecGroupRight_Components.isChecked()) {
			// list.add(6);
			getSearchObjSecRight().addFilter(new Filter("rigType", 6, Filter.OP_EQUAL));
		}

		listBoxSecGroupRight.setModel(new PagedListWrapper<SecRight>(listBoxSecGroupRight, paging_ListBoxSecGroupRight, getTestService()
				.getSRBySearchObject(getSearchObjSecRight(), 0, countRowsRight), getSearchObjSecRight()));

		// listBoxSecGroupRight.setModel(new
		// ListModelList(getSecurityService().getAllRights(list)));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void onCheck$checkbox_SecGroupRight_All(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		checkbox_SecGroupRight_Pages.setChecked(false);
		checkbox_SecGroupRight_Tabs.setChecked(false);
		checkbox_SecGroupRight_MenuCat.setChecked(false);
		checkbox_SecGroupRight_MenuItems.setChecked(false);
		checkbox_SecGroupRight_Methods.setChecked(false);
		checkbox_SecGroupRight_Domain.setChecked(false);
		checkbox_SecGroupRight_Components.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Pages' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_Pages(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Tabs' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_Tabs(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Menu Categories' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_MenuCat(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Menu Items' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_MenuItems(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Methods' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_Methods(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Domain/Properties' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_Domain(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the checkBox 'Domain/Properties' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupRight_Components(Event event) {
		checkbox_SecGroupRight_All.setChecked(false);

		filterTypeForShowingRights();
	}

	/**
	 * when the "save" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {

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
		secGroupRightWindow.onClose();
	}

	/**
	 * if user select the tab for showing only rights for the group. <br>
	 * 
	 * @param event
	 */
	public void onSelect$tab_SecGroupRight_Details(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRightDetails(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRightDetails().addSort("rigName", false);
		getSearchObjSecRightDetails().addFetch("secGrouprights");
		getSearchObjSecRightDetails().addFilter(new Filter("secGrouprights.secGroup", getSelectedGroup(), Filter.OP_EQUAL));

		listBoxSecGroupRight_Details.setModel(new PagedListWrapper<SecGroupright>(listBoxSecGroupRight_Details,
				paging_ListBoxSecGroupRight_Details, getTestService().getSRBySearchObject(getSearchObjSecRightDetails(), 0,
						countRowsRightDetails), getSearchObjSecRightDetails()));

		listBoxSecGroupRight_Details.setItemRenderer(new SecGrouprightRightListModelItemRenderer());

	}

	/**
	 * if user select the tab for showing all rights and a <br>
	 * checkBox if the right is in the group. <br>
	 * 
	 * @param event
	 */
	public void onSelect$tab_SecGroupRight_AllRights(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		filterTypeForShowingRights();
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
	 */
	public void doSave() {

		List<Listitem> li = null;

		if (tab_SecGroupRight_AllRights.isSelected()) {
			li = listBoxSecGroupRight.getItems();

		} else if (tab_SecGroupRight_Details.isSelected()) {
			li = listBoxSecGroupRight_Details.getItems();

		}

		for (Listitem listitem : li) {

			Listcell lc = (Listcell) listitem.getFirstChild();
			Checkbox cb = (Checkbox) lc.getFirstChild();

			if (cb != null) {

				if (cb.isChecked() == true) {

					// Get the object by casting
					SecRight right = (SecRight) listitem.getAttribute("data");
					// get the group
					SecGroup group = getSelectedGroup();

					// check if the item is newly checked. If so we cannot found
					// it in the SecGroupRight-table
					SecGroupright groupRight = getSecurityService().getGroupRightByGroupAndRight(group, right);

					// if new, we make a newly Object
					if (groupRight == null) {
						groupRight = getSecurityService().getNewSecGroupright();
						groupRight.setSecGroup(group);
						groupRight.setSecRight(right);
					}

					// save to DB
					getSecurityService().saveOrUpdate(groupRight);

				} else if (cb.isChecked() == false) {

					// Get the object by casting
					SecRight right = (SecRight) listitem.getAttribute("data");
					// get the group
					SecGroup group = getSelectedGroup();

					// check if the item is newly unChecked. If so we must found
					// it in the SecGroupRight-table
					SecGroupright groupRight = getSecurityService().getGroupRightByGroupAndRight(group, right);

					if (groupRight != null) {
						// delete from DB
						getSecurityService().delete(groupRight);
					}
				}
			}
		}

	}

	public void onGroupItemClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecGroup.getSelectedItem();

		// Casting
		SecGroup group = (SecGroup) item.getAttribute("data");
		setSelectedGroup(group);

		if (tab_SecGroupRight_AllRights.isSelected()) {
			filterTypeForShowingRights();
		} else if (tab_SecGroupRight_Details.isSelected()) {

			// ++ create the searchObject and init sorting ++//
			setSearchObjSecRightDetails(new HibernateSearchObject(SecRight.class));
			getSearchObjSecRightDetails().addSort("rigName", false);
			getSearchObjSecRightDetails().addFetch("secGrouprights");
			getSearchObjSecRightDetails().addFilter(new Filter("secGrouprights.secGroup", getSelectedGroup(), Filter.OP_EQUAL));

			listBoxSecGroupRight_Details.setModel(new PagedListWrapper<SecGroupright>(listBoxSecGroupRight_Details,
					paging_ListBoxSecGroupRight_Details, getTestService().getSRBySearchObject(getSearchObjSecRightDetails(), 0,
							countRowsRightDetails), getSearchObjSecRightDetails()));

			listBoxSecGroupRight_Details.setItemRenderer(new SecGrouprightRightListModelItemRenderer());

			// listBoxSecGroupRight_Details.setModel(new
			// ListModelList(getSecurityService
			// ().getGroupRightsByGroup(getSelectedGroup())));
			// listBoxSecGroupRight_Details.setItemRenderer(new
			// SecGrouprightRightListModelItemRenderer());
		}
	}

	public void onClick$button_GroupRight_NewGroupRight(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listBoxSecGroup.getSelectedItem();

		// Casting
		SecGroup group = (SecGroup) item.getAttribute("data");
		setSelectedGroup(group);

		if (group != null) {
			// store the selected customer object

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("group", group);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxSecGroupRight_Details", listBoxSecGroupRight_Details);
			map.put("secGrouprightCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/sec_groupright/addGrouprightDialog.zul", null, map);
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setSelectedGroup(SecGroup group) {
		this.selectedGroup = group;
	}

	public static SecGroup getSelectedGroup() {
		return selectedGroup;
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

	public void setSearchObjSecGroup(HibernateSearchObject<SecGroup> searchObjSecGroup) {
		this.searchObjSecGroup = searchObjSecGroup;
	}

	public HibernateSearchObject<SecGroup> getSearchObjSecGroup() {
		return searchObjSecGroup;
	}

	public void setSearchObjSecRight(HibernateSearchObject<SecRight> searchObjSecRight) {
		this.searchObjSecRight = searchObjSecRight;
	}

	public HibernateSearchObject<SecRight> getSearchObjSecRight() {
		return searchObjSecRight;
	}

	public void setSearchObjSecRightDetails(HibernateSearchObject<SecRight> searchObjSecRightDetails) {
		this.searchObjSecRightDetails = searchObjSecRightDetails;
	}

	public HibernateSearchObject<SecRight> getSearchObjSecRightDetails() {
		return searchObjSecRightDetails;
	}

}