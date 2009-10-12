package de.forsthaus.zksample.webui.order;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.AuftragService;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.order.model.OrderListModelItemRenderer;
import de.forsthaus.zksample.webui.order.model.OrderSearchCustomerListModelItemRenderer;
import de.forsthaus.zksample.webui.orderposition.model.OrderpositionListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the orderList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class OrderListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 5710086946825179284L;
	private transient final static Logger logger = Logger.getLogger(OrderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window orderListWindow; // autowired

	// Listbox orders
	protected transient Paging paging_OrderList; // autowired
	protected transient Listbox listBoxOrder; // autowired
	protected transient Listheader listheader_OrderList_OrderNo; // autowired
	protected transient Listheader listheader_OrderList_OderDescr; // autowired

	// Listbox orderPositions
	protected transient Paging paging_OrderArticleList; // autowire
	protected transient Listbox listBoxOrderArticle; // autowired
	protected transient Listheader listheader_OrderPosList_Orderpos_No; // autowired
	protected transient Listheader listheader_OrderPosList_Shorttext; // autowired
	protected transient Listheader listheader_OrderPosList_Count; // autowired
	protected transient Listheader listheader_OrderPosList_SinglePrice; // autowired
	protected transient Listheader listheader_OrderPosList_WholePrice; // autowired

	protected transient Hbox hBoxCustomerSearch; // autowired

	// bandbox searchCustomer
	protected transient Bandbox bandbox_OrderList_CustomerSearch;
	protected transient Textbox tb_Orders_SearchCustNo; // autowired
	protected transient Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected transient Textbox tb_Orders_SearchCustName1; // autowired
	protected transient Textbox tb_Orders_SearchCustCity; // autowired

	// listbox searchCustomer
	protected transient Paging paging_OrderList_CustomerSearchList; // autowired
	protected transient Listbox listBoxCustomerSearch; // autowired
	protected transient Listheader listheader_CustNo; // autowired
	protected transient Listheader listheader_CustMatchcode; // autowired
	protected transient Listheader listheader_CustName1; // autowired
	protected transient Listheader listheader_CustCity; // autowired

	// checkRights
	protected transient Button btnHelp; // autowired
	protected transient Button button_OrderList_NewOrder; // autowired

	// SearchObject
	private transient HibernateSearchObject<Auftrag> searchObjAuftrag;
	private transient HibernateSearchObject<Auftragposition> searchObjOrderPosition;
	private transient HibernateSearchObject<Kunde> searchObjCustomer;

	private transient int pageSizeOrders;
	private transient int pageSizeOrderPositions;

	// ServiceDAOs / Domain Classes
	private transient Kunde kunde;
	private transient Auftrag auftrag;
	private transient AuftragService auftragService;
	private transient KundeService kundeService;
	private transient BrancheService brancheService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public OrderListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$orderListWindow(Event event) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps the vars */
		doOnCreateCommon(orderListWindow, event);

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// check if the orderList is called with a customer param
		if (args.containsKey("customerDialogCtrl")) {
			hBoxCustomerSearch.setVisible(false);
		} else {
			hBoxCustomerSearch.setVisible(true);
		}

		// check if the orderList is called with a customer param
		if (args.containsKey("kunde")) {
			kunde = (Kunde) args.get("kunde");
			setKunde(kunde);
		} else {
			setKunde(null);
		}

		// check if the orderList is called from the Customer Dialog
		// and set the pageSizes
		if (args.containsKey("rowSizeOrders")) {
			int rowSize = (Integer) args.get("rowSizeOrders");
			setPageSizeOrders(rowSize);
		} else {
			setPageSizeOrders(15);
		}
		if (args.containsKey("rowSizeOrderPositions")) {
			int rowSize = (Integer) args.get("rowSizeOrderPositions");
			setPageSizeOrderPositions(rowSize);
		} else {
			setPageSizeOrderPositions(15);
		}

		kunde = getKunde();

		// set the bandbox to readonly
		bandbox_OrderList_CustomerSearch.setReadonly(true);

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		setSearchObjAuftrag(new HibernateSearchObject(Auftrag.class));
		getSearchObjAuftrag().addSort("aufNr", false);

		// set the paging params
		paging_OrderList.setPageSize(getPageSizeOrders());
		paging_OrderList.setDetailed(true);

		paging_OrderArticleList.setPageSize(getPageSizeOrderPositions());
		paging_OrderArticleList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderList_OrderNo.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderList_OrderNo.setSortDescending(new FieldComparator("aufNr", false));
		listheader_OrderList_OderDescr.setSortAscending(new FieldComparator("aufBezeichnung", true));
		listheader_OrderList_OderDescr.setSortDescending(new FieldComparator("aufBezeichnung", false));

		// Set the ListModel for the orders.
		if (kunde == null) {
			listBoxOrder.setModel(new PagedListWrapper<Auftrag>(listBoxOrder, paging_OrderList, getTestService().getSRBySearchObject(
					getSearchObjAuftrag(), 0, getPageSizeOrders()), getSearchObjAuftrag()));
		} else {
			searchObjAuftrag.addFilter(new Filter("kunde", kunde, Filter.OP_EQUAL));

			listBoxOrder.setModel(new PagedListWrapper<Auftrag>(listBoxOrder, paging_OrderList, getTestService().getSRBySearchObject(
					getSearchObjAuftrag(), 0, getPageSizeOrders()), getSearchObjAuftrag()));
		}
		listBoxOrder.setItemRenderer(new OrderListModelItemRenderer());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderPosList_Orderpos_No.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderPosList_Orderpos_No.setSortDescending(new FieldComparator("aufNr", false));
		listheader_OrderPosList_Shorttext.setSortAscending(new FieldComparator("aufBezeichnung", true));
		listheader_OrderPosList_Shorttext.setSortDescending(new FieldComparator("aufBezeichnung", false));
		listheader_OrderPosList_Count.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderPosList_Count.setSortDescending(new FieldComparator("aufNr", false));
		listheader_OrderPosList_SinglePrice.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderPosList_SinglePrice.setSortDescending(new FieldComparator("aufNr", false));
		listheader_OrderPosList_WholePrice.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderPosList_WholePrice.setSortDescending(new FieldComparator("aufNr", false));

		listBoxOrderArticle.setItemRenderer(new OrderpositionListModelItemRenderer());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		orderListWindow.setVisible(workspace.isAllowed("orderListWindow"));

		// TODO we must check wich TabPanel is the first so we can FORCE
		// a click that it become to front.
		btnHelp.setVisible(workspace.isAllowed("button_OrderList_btnHelp"));
		button_OrderList_NewOrder.setVisible(workspace.isAllowed("button_OrderList_NewOrder"));

	}

	public void onSelect$listBoxOrder(Event event) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		Listitem item = listBoxOrder.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			Auftrag auftrag = (Auftrag) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + auftrag.getAufBezeichnung());
			}

			if (auftrag != null) {
				// Set the ListModel and the itemRenderer for the order
				// articles.g

				setSearchObjOrderPosition(new HibernateSearchObject(Auftragposition.class));
				getSearchObjOrderPosition().addFilter(new Filter("auftrag", auftrag, Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy
				// loading problem.
				getSearchObjOrderPosition().addFetch("artikel");

				listBoxOrderArticle.setModel(new PagedListWrapper<Auftragposition>(listBoxOrderArticle, paging_OrderArticleList,
						getTestService().getSRBySearchObject(getSearchObjOrderPosition(), 0, getPageSizeOrderPositions()),
						getSearchObjOrderPosition()));

				listBoxOrderArticle.setItemRenderer(new OrderpositionListModelItemRenderer());

			}

		}

	}

	@SuppressWarnings("unchecked")
	public void onDoubleClickedOrderItem(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxOrder.getSelectedItem();

		if (item != null) {
			// store the selected order object
			Auftrag auftrag = (Auftrag) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + auftrag.getAufBezeichnung());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("auftrag", auftrag);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxOrder", listBoxOrder);
			map.put("orderListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/order/orderDialog.zul", null, map);
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
	 * call the order dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_OrderList_NewOrder(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new order object
		Auftrag auftrag = getAuftragService().getNewAuftrag();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("auftrag", auftrag);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxOrder", listBoxOrder);
		map.put("orderListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/order/orderDialog.zul", null, map);
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
	 * when the "Order search" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_OrderList_OrderNameSearch(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
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
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button of the search bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Close(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		bandbox_OrderList_CustomerSearch.close();
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Search(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSearch();
	}

	public void onOpen$bandbox_OrderList_CustomerSearch(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++ //
		if (getSearchObjCustomer() == null) {
			setSearchObjCustomer(new HibernateSearchObject(Kunde.class));
			getSearchObjCustomer().addSort("kunMatchcode", false);
			setSearchObjCustomer(searchObjCustomer);
		}

		// set the paging params
		int pageSize = 20;
		paging_OrderList_CustomerSearchList.setPageSize(pageSize);
		paging_OrderList_CustomerSearchList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_CustNo.setSortAscending(new FieldComparator("kunNr", true));
		listheader_CustNo.setSortDescending(new FieldComparator("kunNr", false));
		listheader_CustMatchcode.setSortAscending(new FieldComparator("kunMatchcode", true));
		listheader_CustMatchcode.setSortDescending(new FieldComparator("kunMatchcode", false));
		listheader_CustName1.setSortAscending(new FieldComparator("kunName1", true));
		listheader_CustName1.setSortDescending(new FieldComparator("kunName1", false));
		listheader_CustCity.setSortAscending(new FieldComparator("kunOrt", true));
		listheader_CustCity.setSortDescending(new FieldComparator("kunOrt", false));

		listBoxCustomerSearch.setModel(new PagedListWrapper<Kunde>(listBoxCustomerSearch, paging_OrderList_CustomerSearchList,
				getTestService().getSRBySearchObject(getSearchObjCustomer(), 0, pageSize), getSearchObjCustomer()));

		// listBoxCustomerSearch.setModel(new
		// ListModelList(getKundeService().getAlleKunden()));
		listBoxCustomerSearch.setItemRenderer(new OrderSearchCustomerListModelItemRenderer());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	private void doSearch() {

		searchObjCustomer = new HibernateSearchObject(Kunde.class);

		// check which field have input
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustNo.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunNr", tb_Orders_SearchCustNo.getValue(), Filter.OP_EQUAL));
		}

		if (StringUtils.isNotEmpty(tb_Orders_CustSearchMatchcode.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunMatchcode", "%" + tb_Orders_CustSearchMatchcode.getValue().toUpperCase() + "%",
					Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustName1.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunName1", "%" + tb_Orders_SearchCustName1.getValue() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustCity.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunOrt", "%" + tb_Orders_SearchCustCity.getValue() + "%", Filter.OP_ILIKE));
		}

		setSearchObjCustomer(searchObjCustomer);

		listBoxCustomerSearch.setModel(new PagedListWrapper<Kunde>(listBoxCustomerSearch, paging_OrderList_CustomerSearchList,
				getTestService().getSRBySearchObject(getSearchObjCustomer(), 0, paging_OrderList_CustomerSearchList.getPageSize()),
				getSearchObjCustomer()));

	}

	/**
	 * when doubleClick on a item in the customerSearch listbox.<br>
	 * <br>
	 * Select the customer and search all orders for him.
	 * 
	 * @param event
	 */
	public void onDoubleClickedCustomerItem(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the customer
		Listitem item = listBoxCustomerSearch.getSelectedItem();
		if (item != null) {

			/* clear the listboxes from older stuff */
			if ((ListModelList) listBoxOrder.getModel() != null) {
				((ListModelList) listBoxOrder.getModel()).clear();
			}
			if ((ListModelList) listBoxOrderArticle.getModel() != null) {
				((ListModelList) listBoxOrderArticle.getModel()).clear();
			}

			Kunde kunde = (Kunde) item.getAttribute("data");

			bandbox_OrderList_CustomerSearch.setValue(kunde.getKunName1() + ", " + kunde.getKunOrt());

			// get all orders for the selected customer
			setSearchObjAuftrag(new HibernateSearchObject(Auftrag.class));
			getSearchObjAuftrag().addSort("aufNr", false);
			getSearchObjAuftrag().addFilter(new Filter("kunde", kunde, Filter.OP_EQUAL));

			listBoxOrder.setModel(new PagedListWrapper<Auftrag>(listBoxOrder, paging_OrderList, getTestService().getSRBySearchObject(
					getSearchObjAuftrag(), 0, getPageSizeOrders()), getSearchObjAuftrag()));

			// get the first object and poll and show the orderpositions
			ListModelList lml = (ListModelList) listBoxOrder.getModel();

			if (lml.size() > 0) {

				Auftrag auftrag = (Auftrag) lml.get(0);

				if (auftrag != null) {
					setSearchObjOrderPosition(new HibernateSearchObject(Auftragposition.class));
					getSearchObjOrderPosition().addFilter(new Filter("auftrag", auftrag, Filter.OP_EQUAL));
					// deeper loading of the relation to prevent the lazy
					// loading problem.
					getSearchObjOrderPosition().addFetch("artikel");

					listBoxOrderArticle.setModel(new PagedListWrapper<Auftragposition>(listBoxOrderArticle, paging_OrderArticleList,
							getTestService().getSRBySearchObject(getSearchObjOrderPosition(), 0, getPageSizeOrderPositions()),
							getSearchObjOrderPosition()));
				}
			} else {
				// get a new Order for searching that the resultList is cleared
				Auftrag auftrag = getAuftragService().getNewAuftrag();
				setSearchObjOrderPosition(new HibernateSearchObject(Auftragposition.class));
				getSearchObjOrderPosition().addFilter(new Filter("auftrag", auftrag, Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy
				// loading problem.
				getSearchObjOrderPosition().addFetch("artikel");

				listBoxOrderArticle.setModel(new PagedListWrapper<Auftragposition>(listBoxOrderArticle, paging_OrderArticleList,
						getTestService().getSRBySearchObject(getSearchObjOrderPosition(), 0, getPageSizeOrderPositions()),
						getSearchObjOrderPosition()));
			}
		}

		// close the bandbox
		bandbox_OrderList_CustomerSearch.close();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSearchObjAuftrag(HibernateSearchObject<Auftrag> searchObjAuftrag) {
		this.searchObjAuftrag = searchObjAuftrag;
	}

	public HibernateSearchObject<Auftrag> getSearchObjAuftrag() {
		return searchObjAuftrag;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
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

	public void setKundeService(KundeService kundeService) {
		this.kundeService = kundeService;
	}

	public KundeService getKundeService() {
		if (kundeService == null) {
			kundeService = (KundeService) SpringUtil.getBean("kundeService");
			setKundeService(kundeService);
		}
		return kundeService;
	}

	private void setAuftragService(AuftragService auftragService) {
		this.auftragService = auftragService;
	}

	public AuftragService getAuftragService() {
		if (auftragService == null) {
			auftragService = (AuftragService) SpringUtil.getBean("auftragService");
			setAuftragService(auftragService);
		}
		return auftragService;
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

	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
	}

	public Auftrag getAuftrag() {
		return auftrag;
	}

	public void setSearchObjCustomer(HibernateSearchObject<Kunde> searchObjCustomer) {
		this.searchObjCustomer = searchObjCustomer;
	}

	public HibernateSearchObject<Kunde> getSearchObjCustomer() {
		return searchObjCustomer;
	}

	public void setSearchObjOrderPosition(HibernateSearchObject<Auftragposition> searchObjOrderPosition) {
		this.searchObjOrderPosition = searchObjOrderPosition;
	}

	public HibernateSearchObject<Auftragposition> getSearchObjOrderPosition() {
		return searchObjOrderPosition;
	}

	public void setPageSizeOrders(int pageSizeOrders) {
		this.pageSizeOrders = pageSizeOrders;
	}

	public int getPageSizeOrders() {
		return pageSizeOrders;
	}

	public void setPageSizeOrderPositions(int pageSizeOrderPositions) {
		this.pageSizeOrderPositions = pageSizeOrderPositions;
	}

	public int getPageSizeOrderPositions() {
		return pageSizeOrderPositions;
	}

}
