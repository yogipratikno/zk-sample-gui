package de.forsthaus.zksample.webui.security.groupright;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecGroupright;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.webui.security.right.model.SecRightListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the addGrouprightDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class AddGrouprightDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 2146221197789582858L;
	private transient final static Logger logger = Logger.getLogger(AddGrouprightDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window addGrouprightDialogWindow; // autowired
	protected transient Textbox textbox_AddGroupRightDialog_GroupName; // autowired
	protected transient Textbox textbox_AddGroupRightDialog_RightName; // autowired

	// Bandbox search/select right
	protected transient Bandbox bandbox_AddGroupRightDialog_SearchRight; // autowired
	protected transient Textbox textbox_bboxAddGroupRightDialog_rightName; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_All; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_Pages; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_Tabs; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_MenuCat; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_MenuItems; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_Methods; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_Domain; // autowired
	protected transient Checkbox checkbox_bbox_AddGroupRightDialog_Components; // autowired

	// Listbox bandbox Search
	protected transient Paging paging_ListBoxSingleRightSearch; // autowired
	protected transient Listbox listBoxSingleRightSearch; // autowired
	protected transient Listheader listheader_bbox_AddGroupRightDialog_RightName; // autowired
	protected transient Listheader listheader_bbox_AddGroupRightDialog_RightType; // autowired

	private transient Listbox listBoxSecGroupRight_Details; // overhanded
	private transient SecGrouprightCtrl secGrouprightCtrl; // overhanded

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_AddGrouprightDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	// search objects
	private transient HibernateSearchObject<SecRight> searchObjSecRightSearch;

	// row count for listbox
	private transient int countRowsSecRight;

	// ServiceDAOs / Domain classes
	private transient SecGroup group;
	private transient SecRight right;
	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public AddGrouprightDialogCtrl() {
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
	public void onCreate$addGrouprightDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		/* +++ autowire the vars +++ */
		doOnCreateCommon(addGrouprightDialogWindow, event);

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("group")) {
			group = (SecGroup) args.get("group");
			setGroup(group);
		} else {
			setGroup(null);
		}

		// we get the listBox Object for the branch list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete branches here.
		if (args.containsKey("listBoxSecGroupRight_Details")) {
			listBoxSecGroupRight_Details = (Listbox) args.get("listBoxSecGroupRight_Details");
		} else {
			listBoxSecGroupRight_Details = null;
		}

		if (args.containsKey("secGrouprightCtrl")) {
			secGrouprightCtrl = (SecGrouprightCtrl) args.get("secGrouprightCtrl");
		} else {
			secGrouprightCtrl = null;
		}

		countRowsSecRight = 20;

		paging_ListBoxSingleRightSearch.setPageSize(countRowsSecRight);
		paging_ListBoxSingleRightSearch.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_bbox_AddGroupRightDialog_RightName.setSortAscending(new FieldComparator("rigName", true));
		listheader_bbox_AddGroupRightDialog_RightName.setSortDescending(new FieldComparator("rigName", false));
		listheader_bbox_AddGroupRightDialog_RightType.setSortAscending(new FieldComparator("rigType", true));
		listheader_bbox_AddGroupRightDialog_RightType.setSortDescending(new FieldComparator("rigType", false));

		// temporary, cause the security is not implemented for this controller
		btnEdit.setVisible(false);
		btnDelete.setVisible(false);

		doShowDialog(getGroup());

	}

	/**
	 * opens the dialog window in modal mode.
	 */
	public void doShowDialog(SecGroup group) throws InterruptedException {

		try {
			textbox_AddGroupRightDialog_GroupName.setValue(group.getGrpShortdescription());
			textbox_AddGroupRightDialog_RightName.setValue("");

			btnCtrl.setInitNew();

			addGrouprightDialogWindow.doModal(); // open the dialog in modal
			// mode
		} catch (Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$addGrouprightDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doClose();
	}

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
	 * when the "edit" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doEdit();
	}

	private void doEdit() {
		// TODO Auto-generated method stub

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
	 * when the "new" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doNew();
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

		try {
			doClose();
		} catch (Exception e) {
			// close anyway
			addGrouprightDialogWindow.onClose();
			// Messagebox.show(e.toString());
		}
	}

	private void doClose() {

		addGrouprightDialogWindow.onClose();
	}

	private void doSave() throws InterruptedException {

		if (textbox_AddGroupRightDialog_GroupName.getValue().isEmpty()) {
			return;
		} else if (textbox_AddGroupRightDialog_RightName.getValue().isEmpty()) {
			return;
		}

		SecGroupright groupRight = getSecurityService().getNewSecGroupright();
		groupRight.setSecGroup(getGroup());
		groupRight.setSecRight(getRight());

		/* check if already in table */
		SecGroupright gr = getSecurityService().getGroupRightByGroupAndRight(getGroup(), getRight());

		if (gr == null) {

			// save it to database
			try {
				getSecurityService().saveOrUpdate(groupRight);
			} catch (Exception e) {
				String message = e.getMessage();
				// String message = e.getCause().getMessage();
				String title = Labels.getLabel("message_Error");
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "ERROR", true);

				btnCtrl.setBtnStatus_Save();
			}

		}

		btnCtrl.setBtnStatus_Save();

	}

	private void doNew() {
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Bandbox events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the bandpopup is opened. <br>
	 * 
	 * @param event
	 */
	public void onOpen$bpop_AddGroupRightDialog_SearchRight(Event event) throws InterruptedException {

		checkbox_bbox_AddGroupRightDialog_All.setChecked(true);
	}

	/**
	 * when the "search" button on the bandpopup is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_AddGroupRightDialog_Search(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSearch();
	}

	/**
	 * when the "close" button on the bandpopup is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_AddGroupRightDialog_Close(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doCloseBandbox();
	}

	private void doSearch() {

		filterTypeForShowingRights();

	}

	public void onCheck$checkbox_bbox_AddGroupRightDialog_All(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		textbox_bboxAddGroupRightDialog_rightName.setValue("");
		checkbox_bbox_AddGroupRightDialog_Pages.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_Tabs.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_MenuCat.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_MenuItems.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_Methods.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_Domain.setChecked(false);
		checkbox_bbox_AddGroupRightDialog_Components.setChecked(false);
	}

	/**
	 * when the checkBox 'Pages' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_Pages(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Tabs' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_Tabs(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Menu Categories' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_MenuCat(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Menu Items' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_MenuItems(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Methods' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_Methods(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Domain/Classes' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_Domain(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * when the checkBox 'Components' for filtering is checked.<br>
	 * Disables the 'all' checkBox.
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_bbox_AddGroupRightDialog_Components(Event event) {
		checkbox_bbox_AddGroupRightDialog_All.setChecked(false);
	}

	/**
	 * Analyze wich right type for filtering is checked <br>
	 * to the DAO where with the values in the types-list <br>
	 * the select statement is dynamically build.<br>
	 */
	private void filterTypeForShowingRights() {

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRightSearch(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRightSearch().addSort("rigName", false);

		Filter f = Filter.or();

		if (checkbox_bbox_AddGroupRightDialog_All.isChecked()) {
			// nothing todo
		}

		if (checkbox_bbox_AddGroupRightDialog_Pages.isChecked()) {
			f.add(Filter.equal("rigType", 0));
		}

		if (checkbox_bbox_AddGroupRightDialog_Tabs.isChecked()) {
			f.add(Filter.equal("rigType", 5));
		}

		if (checkbox_bbox_AddGroupRightDialog_MenuCat.isChecked()) {
			f.add(Filter.equal("rigType", 1));
		}

		if (checkbox_bbox_AddGroupRightDialog_MenuItems.isChecked()) {
			f.add(Filter.equal("rigType", 2));
		}

		if (checkbox_bbox_AddGroupRightDialog_Methods.isChecked()) {
			f.add(Filter.equal("rigType", 3));
		}

		if (checkbox_bbox_AddGroupRightDialog_Domain.isChecked()) {
			f.add(Filter.equal("rigType", 4));
		}

		if (checkbox_bbox_AddGroupRightDialog_Components.isChecked()) {
			f.add(Filter.equal("rigType", 6));
		}

		if (textbox_bboxAddGroupRightDialog_rightName.getValue().isEmpty()) {
			getSearchObjSecRightSearch().addFilter(f);

			listBoxSingleRightSearch
					.setModel(new PagedListWrapper<SecRight>(listBoxSingleRightSearch, paging_ListBoxSingleRightSearch, getTestService()
							.getSRBySearchObject(getSearchObjSecRightSearch(), 0, countRowsSecRight), getSearchObjSecRightSearch()));

		} else if (!textbox_bboxAddGroupRightDialog_rightName.getValue().isEmpty()) {
			getSearchObjSecRightSearch().addFilter(f);

			Filter f1 = Filter.and();

			f1.add(Filter.ilike("rigName", "%" + textbox_bboxAddGroupRightDialog_rightName.getValue() + "%"));
			getSearchObjSecRightSearch().addFilter(f1);

			listBoxSingleRightSearch
					.setModel(new PagedListWrapper<SecRight>(listBoxSingleRightSearch, paging_ListBoxSingleRightSearch, getTestService()
							.getSRBySearchObject(getSearchObjSecRightSearch(), 0, countRowsSecRight), getSearchObjSecRightSearch()));

		}

		listBoxSingleRightSearch.setItemRenderer(new SecRightListModelItemRenderer());
	}

	/**
	 * when doubleClicked on a item in the rights listBox. <br>
	 * 
	 * @param event
	 */
	public void onDoubleClickedRightItem() {

		Listitem item = listBoxSingleRightSearch.getSelectedItem();

		if (item != null) {
			SecRight right = (SecRight) item.getAttribute("data");

			/* store the selected right */
			setRight(right);

			textbox_AddGroupRightDialog_RightName.setValue(right.getRigName());
		}

		doCloseBandbox();

	}

	private void doCloseBandbox() {
		bandbox_AddGroupRightDialog_SearchRight.close();
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

	public void setGroup(SecGroup group) {
		this.group = group;
	}

	public SecGroup getGroup() {
		return group;
	}

	public void setRight(SecRight right) {
		this.right = right;
	}

	public SecRight getRight() {
		return right;
	}

	public void setSearchObjSecRightSearch(HibernateSearchObject<SecRight> searchObjSecRightSearch) {
		this.searchObjSecRightSearch = searchObjSecRightSearch;
	}

	public HibernateSearchObject<SecRight> getSearchObjSecRightSearch() {
		return searchObjSecRightSearch;
	}

}