package de.forsthaus.zksample.webui.security.group;

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

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.group.model.SecGroupListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secGroupList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */

public class SecGroupListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private transient final static Logger logger = Logger.getLogger(SecGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secGroupListWindow; // autowired

	// filter components
	protected transient Checkbox checkbox_SecGroupList_ShowAll; // autowired
	protected transient Textbox tb_SecGroup_GroupName; // autowired

	// listbox secGroupList
	protected transient Borderlayout borderLayout_secGroupsList; // autowired
	protected transient Paging paging_SecGroupList; // aurowired
	protected transient Listbox listBoxSecGroups; // aurowired
	protected transient Listheader listheader_SecGroupList_grpShortdescription; // autowired
	protected transient Listheader listheader_SecGroupList_grpLongdescription; // autowired

	private transient HibernateSearchObject<SecGroup> searchObjSecGroup;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecGroupListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$secGroupListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(secGroupListWindow);

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 160);
		countRows = Math.round(maxListBoxHeight / 16);
		// listBoxSecGroups.setPageSize(countRows);

		borderLayout_secGroupsList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecGroupList_ShowAll.setChecked(true);

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecGroup(new HibernateSearchObject(SecGroup.class));
		getSearchObjSecGroup().addSort("grpShortdescription", false);
		setSearchObjSecGroup(searchObjSecGroup);

		// set the paging params
		paging_SecGroupList.setPageSize(countRows);
		paging_SecGroupList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecGroupList_grpShortdescription.setSortAscending(new FieldComparator("grpShortdescription", true));
		listheader_SecGroupList_grpShortdescription.setSortDescending(new FieldComparator("grpShortdescription", false));
		listheader_SecGroupList_grpLongdescription.setSortAscending("");
		listheader_SecGroupList_grpLongdescription.setSortDescending("");

		listBoxSecGroups.setModel(new PagedListWrapper<SecGroup>(listBoxSecGroups, paging_SecGroupList, getTestService()
				.getSRBySearchObject(searchObjSecGroup, 0, countRows), searchObjSecGroup));

		listBoxSecGroups.setItemRenderer(new SecGroupListModelItemRenderer());

	}

	@SuppressWarnings("unchecked")
	public void onDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecGroups.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			SecGroup group = (SecGroup) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + group.getGrpShortdescription());
			}

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
			map.put("listBoxSecGroups", listBoxSecGroups);
			map.put("secGroupListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/sec_group/secGroupDialog.zul", null, map);
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
	public void onClick$button_SecGroupList_NewGroup(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		SecGroup group = getSecurityService().getNewSecGroup();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("group", group);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecGroups", listBoxSecGroups);
		map.put("secGroupListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/sec_group/secGroupDialog.zul", null, map);
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
	public void onCheck$checkbox_SecGroupList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecGroup_GroupName.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecGroup(new HibernateSearchObject(SecGroup.class));
		getSearchObjSecGroup().addSort("grpShortdescription", false);
		setSearchObjSecGroup(searchObjSecGroup);

		listBoxSecGroups.setModel(new PagedListWrapper<SecGroup>(listBoxSecGroups, paging_SecGroupList, getTestService()
				.getSRBySearchObject(getSearchObjSecGroup(), 0, countRows), getSearchObjSecGroup()));

	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecGroupList_PrintGroupList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Filter the group list with 'like GroupName'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecGroupList_SearchGroupName(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecGroup_GroupName.getValue().isEmpty()) {
			checkbox_SecGroupList_ShowAll.setChecked(false); // unCheck

			// ++ create the searchObject and init sorting ++//
			setSearchObjSecGroup(new HibernateSearchObject(SecGroup.class));
			getSearchObjSecGroup().addSort("grpShortdescription", false);
			setSearchObjSecGroup(searchObjSecGroup);

			getSearchObjSecGroup().addFilter(
					new Filter("grpShortdescription", "%" + tb_SecGroup_GroupName.getValue() + "%", Filter.OP_ILIKE));

			listBoxSecGroups.setModel(new PagedListWrapper<SecGroup>(listBoxSecGroups, paging_SecGroupList, getTestService()
					.getSRBySearchObject(getSearchObjSecGroup(), 0, countRows), getSearchObjSecGroup()));

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

	public void setSearchObjSecGroup(HibernateSearchObject<SecGroup> searchObjSecGroup) {
		this.searchObjSecGroup = searchObjSecGroup;
	}

	public HibernateSearchObject<SecGroup> getSearchObjSecGroup() {
		return searchObjSecGroup;
	}

}
