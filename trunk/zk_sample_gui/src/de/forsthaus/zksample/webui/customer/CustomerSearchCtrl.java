package de.forsthaus.zksample.webui.customer;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.webui.customer.model.CustomerBrancheListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;
import de.forsthaus.zksample.webui.util.searching.SearchOperatorListModelItemRenderer;
import de.forsthaus.zksample.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the customerSearchDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering
 * 
 */
public class CustomerSearchCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -6320398861070378344L;
	private transient final static Logger logger = Logger.getLogger(CustomerSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window customerSearchWindow; // autowired
	protected transient Listbox sortOperator_kunNr; // autowired
	protected transient Textbox kunNr; // autowired
	protected transient Listbox sortOperator_kunMatchcode; // autowired
	protected transient Textbox kunMatchcode; // autowired
	protected transient Listbox sortOperator_kunName1; // autowired
	protected transient Textbox kunName1; // autowired
	protected transient Listbox sortOperator_kunName2; // autowired
	protected transient Textbox kunName2; // autowired
	protected transient Listbox sortOperator_kunOrt; // autowired
	protected transient Textbox kunOrt; // autowired
	protected transient Listbox sortOperator_kunBranch; // autowired
	protected transient Listbox kunBranche; // autowired

	// overhanded vars per params
	private transient CustomerListCtrl customerCtrl; // overhanded

	private transient HibernateSearchObject<Kunde> searchObj;
	private transient HibernateSearchObject<Branche> searchObjBranche;

	// ServiceDAOs / Domain Classes
	private transient BrancheService brancheService;
	private transient KundeService kundeService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public CustomerSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$customerSearchWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(customerSearchWindow, event); // autowire the vars

		if (args.containsKey("customerCtrl")) {
			customerCtrl = (CustomerListCtrl) args.get("customerCtrl");
		} else {
			customerCtrl = null;
		}

		if (args.containsKey("searchObject")) {
			searchObj = (HibernateSearchObject<Kunde>) args.get("searchObject");
		} else {
			searchObj = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		// set listModel and itemRenderer for the Branch dropdown listbox
		searchObjBranche = new HibernateSearchObject(Branche.class);
		searchObjBranche.addSort("braBezeichnung", false);

		kunBranche.setModel(new ListModelList(getTestService().getBySearchObject(searchObjBranche, 0, Integer.MAX_VALUE)));
		kunBranche.setItemRenderer(new CustomerBrancheListModelItemRenderer());

		// kunBranche.setModel(new
		// ListModelList(getBrancheService().getAlleBranche()));
		// kunBranche.setItemRenderer(new
		// CustomerBrancheListModelItemRenderer());

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		// set listModel and itemRenderer for the search operator type listboxes
		sortOperator_kunNr.setModel(new ListModelList(new SearchOperators().getAllOperators(), true));
		sortOperator_kunNr.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunMatchcode.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunMatchcode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunName1.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunName1.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunName2.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunName2.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunOrt.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunOrt.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunBranch.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (searchObj != null) {

			// get the filters from the searchObject
			List<Filter> ft = searchObj.getFilters();

			for (Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("kunNr")) {
					restoreOperator(sortOperator_kunNr, filter);
					kunNr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunMatchcode")) {
					restoreOperator(sortOperator_kunMatchcode, filter);
					kunMatchcode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunName1")) {
					restoreOperator(sortOperator_kunName1, filter);
					kunName1.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunName2")) {
					restoreOperator(sortOperator_kunName2, filter);
					kunName2.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunOrt")) {
					restoreOperator(sortOperator_kunOrt, filter);
					kunOrt.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branche")) {
					restoreOperator(sortOperator_kunBranch, filter);
					ListModelList lml = (ListModelList) kunBranche.getModel();
					// get and select the customers branch
					Branche branche = (Branche) filter.getValue();
					kunBranche.setSelectedIndex(lml.indexOf(branche));
				}
			}
		}

		showCustomerSeekDialog();

	}

	/**
	 * Restore the operator sign in the operator listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox
	 *            Listbox that shows the operator signs.
	 * @param filter
	 *            Filter that corresponds to the operator listbox.
	 */
	private void restoreOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(2);
		} else if (filter.getOperator() == Filter.OP_LESS_THAN) {
			listbox.setSelectedIndex(3);
		} else if (filter.getOperator() == Filter.OP_GREATER_THAN) {
			listbox.setSelectedIndex(4);
		} else if (filter.getOperator() == Filter.OP_LESS_OR_EQUAL) {
			listbox.setSelectedIndex(5);
		} else if (filter.getOperator() == Filter.OP_GREATER_OR_EQUAL) {
			listbox.setSelectedIndex(6);
		} else if (filter.getOperator() == Filter.OP_ILIKE) {
			// Delete used '%' signs if the operator is like or iLike
			String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(7);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSearch();
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
		customerSearchWindow.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			customerSearchWindow.doModal();
		} catch (Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {

		HibernateSearchObject<Kunde> so = new HibernateSearchObject(Kunde.class);

		if (StringUtils.isNotEmpty(kunNr.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunNr.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunNr", "%" + kunNr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunNr", kunNr.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunMatchcode.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunMatchcode.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunMatchcode", "%" + kunMatchcode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunMatchcode", kunMatchcode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunName1.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunName1.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunName1", "%" + kunName1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunName1", kunName1.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunName2.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunName2.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunName2", "%" + kunName2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunName2", kunName2.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunOrt.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunOrt.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunOrt", "%" + kunOrt.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunOrt", kunOrt.getValue(), searchOpId));
				}
			}
		}

		if (kunBranche.getSelectedCount() > 0) {

			// check if it the default empty item
			Listitem itemB = kunBranche.getSelectedItem();
			Branche branche = (Branche) itemB.getAttribute("data");

			if (!branche.getBraNr().equalsIgnoreCase("000")) {

				// get the search operator
				Listitem item = sortOperator_kunBranch.getSelectedItem();

				if (item != null) {
					int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

					if (searchOpId == Filter.OP_ILIKE) {
						so.addFilter(new Filter("branche", branche, searchOpId));
					} else if (searchOpId == -1) {
						// do nothing
					} else {
						so.addFilter(new Filter("branche", branche, searchOpId));
					}
				}
			}
		}

		if (logger.isDebugEnabled()) {
			List<Filter> lf = so.getFilters();
			for (Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		customerCtrl.setSearchObj(so);

		Listbox listBox = customerCtrl.listBoxCustomer;
		Paging paging = customerCtrl.pagingCustomerList;
		int ps = customerCtrl.pagingCustomerList.getPageSize();

		// set the model to the listbox with the initial resultset get by the
		// DAO method.
		listBox.setModel(new PagedListWrapper<Kunde>(listBox, paging, customerCtrl.getTestService().getSRBySearchObject(so, 0, ps), so));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	private TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
		}
		return testService;
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

}
