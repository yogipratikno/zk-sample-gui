package de.forsthaus.zksample.webui.security.right;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.security.right.model.SecRightListModelItemRenderer;
import de.forsthaus.zksample.webui.security.right.model.SecRightSecTypListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secRightList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class SecRightListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private transient final static Logger logger = Logger.getLogger(SecRightListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secRightListWindow; // autowired

	// filter components
	protected transient Checkbox checkbox_SecRightList_ShowAll; // autowired
	protected transient Textbox tb_SecRightList_rigName; // aurowired
	protected transient Listbox lb_secRight_RightType; // aurowired

	// listbox secRightList
	protected transient Borderlayout borderLayout_secRightsList; // autowired
	protected transient Paging paging_SecRightList; // aurowired
	protected transient Listbox listBoxSecRights; // aurowired
	protected transient Listheader listheader_SecRightList_rigName; // autowired
	protected transient Listheader listheader_SecRightList_rigType; // autowired

	private transient HibernateSearchObject<SecRight> searchObjSecRight;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public SecRightListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$secRightListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(secRightListWindow);

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 158);
		countRows = Math.round(maxListBoxHeight / 18);
		// listBoxSecRights.setPageSize(countRows);

		borderLayout_secRightsList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecRightList_ShowAll.setChecked(true);

		// ++++++++++++++ DropDown ListBox ++++++++++++++++++ //
		// set listModel and itemRenderer for the dropdown listbox
		lb_secRight_RightType.setModel(new ListModelList(getSecurityService().getAllTypes()));
		lb_secRight_RightType.setItemRenderer(new SecRightSecTypListModelItemRenderer());

		ListModelList lml = (ListModelList) lb_secRight_RightType.getModel();
		// we added an empty SecType simulated the (-1) for all.
		lml.add(0, new SecTyp(-1, ""));

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRight(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRight().addSort("rigName", false);
		setSearchObjSecRight(getSearchObjSecRight());

		// set the paging params
		paging_SecRightList.setPageSize(countRows);
		paging_SecRightList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecRightList_rigName.setSortAscending(new FieldComparator("rigName", true));
		listheader_SecRightList_rigName.setSortDescending(new FieldComparator("rigName", false));
		listheader_SecRightList_rigType.setSortAscending(new FieldComparator("rigType", true));
		listheader_SecRightList_rigType.setSortDescending(new FieldComparator("rigType", false));

		listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
				.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));

		listBoxSecRights.setItemRenderer(new SecRightListModelItemRenderer());

	}

	@SuppressWarnings("unchecked")
	public void onDoubleClickedRightItem(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecRights.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			SecRight right = (SecRight) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + right.getRigName());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("right", right);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxSecRights", listBoxSecRights);
			map.put("secRightListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/sec_right/secRightDialog.zul", null, map);
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
	public void onClick$button_SecRightList_NewRight(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new right object
		SecRight right = getSecurityService().getNewSecRight();
		right.setRigType(1);

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("right", right);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecRights", listBoxSecRights);
		map.put("secRightListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/sec_right/secRightDialog.zul", null, map);
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
	 * call the binding new.
	 */
	public void doBindNew() {
		binder.loadAll();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecRightList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecRightList_rigName.setValue(""); // clear
		lb_secRight_RightType.clearSelection(); // clear

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRight(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRight().addSort("rigName", false);
		setSearchObjSecRight(getSearchObjSecRight());

		listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
				.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));

		// listBoxSecRights.setModel(new
		// ListModelList(getSecurityService().getAllRights(-1)));
	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecRightList_PrintRightList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Filter the rights list with 'like RightName'. <br>
	 * We check additionally if something is selected in the right type listbox <br>
	 * for including in the search statement.<br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecRightList_SearchRightName(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRight(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRight().addSort("rigName", false);
		setSearchObjSecRight(getSearchObjSecRight());

		// if not empty
		if (!tb_SecRightList_rigName.getValue().isEmpty()) {
			checkbox_SecRightList_ShowAll.setChecked(false); // clear

			getSearchObjSecRight().addFilter(new Filter("rigName", "%" + tb_SecRightList_rigName.getValue() + "%", Filter.OP_ILIKE));

			// check if we must include a selected RightType item
			Listitem item = lb_secRight_RightType.getSelectedItem();

			if (item != null) {

				// casting to the needed object
				SecTyp type = (SecTyp) item.getAttribute("data");

				if (type.getStpId() > -1) {
					getSearchObjSecRight().addFilter(new Filter("rigType", type.getStpId(), Filter.OP_ILIKE));
				}

				listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
						.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));

			} else {
				listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
						.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));
			}

		}

	}

	/**
	 * Filter the rights list with 'like RightType'. <br>
	 * We check additionally if something is standing in the textbox <br>
	 * for including in the search statement.
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_SecRightList_SearchRightType(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++//
		setSearchObjSecRight(new HibernateSearchObject(SecRight.class));
		getSearchObjSecRight().addSort("rigName", false);
		setSearchObjSecRight(getSearchObjSecRight());

		// get the selected item
		Listitem item = lb_secRight_RightType.getSelectedItem();

		if (item != null) {
			checkbox_SecRightList_ShowAll.setChecked(false); // clear
			// casting to the needed object
			SecTyp type = (SecTyp) item.getAttribute("data");

			if (type.getStpId() > -1) {

				getSearchObjSecRight().addFilter(new Filter("rigType", type.getStpId(), Filter.OP_ILIKE));

				if (!tb_SecRightList_rigName.getValue().isEmpty()) {

					// mixed search statement -> like RightName + RightType
					getSearchObjSecRight()
							.addFilter(new Filter("rigName", "%" + tb_SecRightList_rigName.getValue() + "%", Filter.OP_ILIKE));
				}
				listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
						.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));

			}

		} else {
			listBoxSecRights.setModel(new PagedListWrapper<SecRight>(listBoxSecRights, paging_SecRightList, getTestService()
					.getSRBySearchObject(getSearchObjSecRight(), 0, countRows), getSearchObjSecRight()));

		}

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Getter / Setter +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

	public void setSearchObjSecRight(HibernateSearchObject<SecRight> searchObjSecRight) {
		this.searchObjSecRight = searchObjSecRight;
	}

	public HibernateSearchObject<SecRight> getSearchObjSecRight() {
		return searchObjSecRight;
	}

}
