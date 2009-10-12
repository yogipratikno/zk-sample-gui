package de.forsthaus.zksample.webui.customer;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.customer.model.CustomerListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the customerList.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 */
public class CustomerListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 6787508590585436872L;
	private transient final static Logger logger = Logger.getLogger(CustomerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_customerList; // autowired

	// listbox customerList
	protected transient Borderlayout borderLayout_customerList; // autowired
	protected transient Paging pagingCustomerList; // autowired
	protected transient Listbox listBoxCustomer; // autowired
	protected transient Listheader listheader_CustNo; // autowired
	protected transient Listheader listheader_CustMatchcode; // autowired
	protected transient Listheader listheader_CustName1; // autowired
	protected transient Listheader listheader_CustName2; // autowired
	protected transient Listheader listheader_CustCity; // autowired

	// searchPanel
	protected transient Panel customerSeekPanel; // autowired
	protected transient Panel customerListPanel; // autowired

	// checkRights
	protected transient Button btnHelp;
	protected transient Button button_CustomerList_NewCustomer;
	protected transient Button button_CustomerList_CustomerFindDialog;
	protected transient Button button_CustomerList_PrintList;

	// SearchDialogWindow
	protected transient HibernateSearchObject<Kunde> searchObj;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient KundeService kundeService;
	private transient BrancheService brancheService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public CustomerListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$window_customerList(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps the vars */
		doOnCreateCommon(window_customerList, event);

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 135);
		countRows = Math.round(maxListBoxHeight / 21);

		borderLayout_customerList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject(Kunde.class);
		searchObj.addSort("kunName1", false);
		setSearchObj(searchObj);

		// set the paging params
		pagingCustomerList.setPageSize(countRows);
		pagingCustomerList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_CustNo.setSortAscending(new FieldComparator("kunNr", true));
		listheader_CustNo.setSortDescending(new FieldComparator("kunNr", false));
		listheader_CustMatchcode.setSortAscending(new FieldComparator("kunMatchcode", true));
		listheader_CustMatchcode.setSortDescending(new FieldComparator("kunMatchcode", false));
		listheader_CustName1.setSortAscending(new FieldComparator("kunName1", true));
		listheader_CustName1.setSortDescending(new FieldComparator("kunName1", false));
		listheader_CustName2.setSortAscending(new FieldComparator("kunName2", true));
		listheader_CustName2.setSortDescending(new FieldComparator("kunName2", false));
		listheader_CustCity.setSortAscending(new FieldComparator("kunOrt", true));
		listheader_CustCity.setSortDescending(new FieldComparator("kunOrt", false));

		listBoxCustomer.setModel(new PagedListWrapper<Kunde>(listBoxCustomer, pagingCustomerList, getTestService().getSRBySearchObject(
				searchObj, 0, countRows), searchObj));

		listBoxCustomer.setItemRenderer(new CustomerListModelItemRenderer());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_customerList.setVisible(workspace.isAllowed("window_customerList"));
		btnHelp.setVisible(workspace.isAllowed("button_CustomerList_btnHelp"));
		button_CustomerList_NewCustomer.setVisible(workspace.isAllowed("button_CustomerList_NewCustomer"));
		button_CustomerList_CustomerFindDialog.setVisible(workspace.isAllowed("button_CustomerList_CustomerFindDialog"));
		button_CustomerList_PrintList.setVisible(workspace.isAllowed("button_CustomerList_PrintList"));

	}

	@SuppressWarnings("unchecked")
	public void onCustomerItemDoubleClicked(Event event) throws Exception {

		if (!UserWorkspace.getInstance().isAllowed("CustomerList_listBoxCustomer.onDoubleClick")) {
			return;
		}

		// get the selected customer object
		Listitem item = listBoxCustomer.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			Kunde kunde = (Kunde) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + kunde.getKunMatchcode());
			}
			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("kunde", kunde);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("lbCustomer", listBoxCustomer);
			map.put("customerCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/customer/customerDialog.zul", null, map);
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

	/*
	 * call the customer dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_CustomerList_NewCustomer(Event event) throws Exception {

		// create a new customer object
		Kunde kunde = getKundeService().getNewKunde();
		// kunde.setBranche(Workspace.getBranche()); // init
		kunde.setBranche(getBrancheService().getBrancheById(new Integer(1033).longValue())); // init
		kunde.setKunMahnsperre(false); // init

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("kunde", kunde);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("lbCustomer", listBoxCustomer);
		map.put("customerCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/customer/customerDialog.zul", null, map);
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
	 * call the customer dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_CustomerList_CustomerFindDialog(Event event) throws Exception {

		/*
		 * we can call our customerDialog zul-file with parameters. So we can
		 * call them with a object of the selected customer. For handed over
		 * these parameter only a Map is accepted. So we put the customer object
		 * in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("customerCtrl", this);
		map.put("searchObject", searchObj);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/customer/customerSearchDialog.zul", null, map);
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
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerList_PrintList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
		}
		return testService;
	}

	public BrancheService getBrancheService() {
		if (brancheService == null) {
			brancheService = (BrancheService) SpringUtil.getBean("brancheService");
			setBrancheService(brancheService);
		}
		return brancheService;
	}

	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	private void setKundeService(KundeService kundeService) {
		this.kundeService = kundeService;
	}

	public KundeService getKundeService() {
		if (kundeService == null) {
			kundeService = (KundeService) SpringUtil.getBean("kundeService");
			setKundeService(kundeService);
		}
		return kundeService;
	}

	public HibernateSearchObject<Kunde> getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(HibernateSearchObject<Kunde> searchObj) {
		this.searchObj = searchObj;
	}

}