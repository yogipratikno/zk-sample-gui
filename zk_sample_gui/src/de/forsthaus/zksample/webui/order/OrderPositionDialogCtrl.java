package de.forsthaus.zksample.webui.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Artikel;
import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.AuftragService;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.order.model.SearchArticleListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the orderPositionDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class OrderPositionDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private transient final static Logger logger = Logger.getLogger(OrderPositionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window orderPositionDialogWindow; // autowired

	// input area
	protected transient Textbox artNr; // autowired
	protected transient Textbox artKurzbezeichnung; // autowired
	protected transient Decimalbox aupMenge; // autowired
	protected transient Decimalbox aupEinzelwert; // autowired
	protected transient Decimalbox aupGesamtwert; // autowired

	// bandbock searchArticle
	protected transient Bandbox bandbox_OrderPositionDialog_ArticleSearch; // autowired
	protected transient Textbox tb_OrderPosition_SearchArticlelNo; // autowired
	protected transient Textbox tb_OrderPosition_SearchArticleDesc; // autowired

	// listbox articlesearch in bandbox
	private transient int pageSizeArticleSearch;
	protected transient Paging paging_ListBoxArticleSearch; // autowired
	protected transient Listbox listBoxArticleSearch; // autowired
	protected transient Listheader listheader_ArticleSearch_artNr; // autowired
	protected transient Listheader listheader_ArticleSearch_artKurzbezeichnung; // autowired
	protected transient Listheader listheader_ArticleSearch_aupEinzelwert; // autowired

	// search bandbox customer
	protected transient Bandbox bbox_Orders_CustomerSearch; // autowired
	protected transient Textbox tb_Orders_SearchCustNo; // autowired
	protected transient Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected transient Textbox tb_Orders_SearchCustName1; // autowired
	protected transient Textbox tb_Orders_SearchCustCity; // autowired

	// overhanded vars from parent controller
	private transient Listbox listBoxOrderOrderPositions; // overhanded
	private transient Auftrag auftrag; // overhanded
	private transient Auftragposition auftragposition; // overhanded
	private transient OrderDialogCtrl orderDialogCtrl; // overhanded
	private transient OrderListCtrl orderListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_artNr;
	private transient String oldVar_artKurzbezeichnung;
	private transient BigDecimal oldVar_aupMenge;
	private transient BigDecimal oldVar_aupEinzelwert;
	private transient BigDecimal oldVar_aupGesamtwert;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_RightPrefix = "button_OrderPositionDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	protected transient Button btnHelp; // autowire
	protected transient Button button_OrderPositionDialog_PrintOrderPositions; // autowire

	private transient HibernateSearchObject<Artikel> searchObjArticle;
	private transient HibernateSearchObject<Artikel> searchObjArticleSearch;

	// ServiceDAOs / Domain Classes
	private transient Kunde kunde;
	private transient Artikel artikel;
	private transient AuftragService auftragService;
	private transient KundeService kundeService;
	private transient BrancheService brancheService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public OrderPositionDialogCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected user object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$orderPositionDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(orderPositionDialogWindow, event); // autowire the vars

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl("button_OrderPositionDialog_", btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("auftrag")) {
			auftrag = (Auftrag) args.get("auftrag");
		} else {
			setAuftrag(null);
		}

		if (args.containsKey("auftragposition")) {
			auftragposition = (Auftragposition) args.get("auftragposition");
			// we must addionally check if there is NO order object in the
			// orderPosition, so its new.
			if (auftragposition.getAuftrag() != null) {
				setAuftragposition(auftragposition);
				setAuftrag(auftrag);
				setArtikel(auftragposition.getArtikel());
				setKunde(auftrag.getKunde());
			}
		} else {
			setAuftragposition(null);
		}

		// we get the listBox Object for the offices list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete offices here.
		if (args.containsKey("listBoxOrderOrderPositions")) {
			listBoxOrderOrderPositions = (Listbox) args.get("listBoxOrderOrderPositions");
		} else {
			listBoxOrderOrderPositions = null;
		}

		if (args.containsKey("orderDialogCtrl")) {
			orderDialogCtrl = (OrderDialogCtrl) args.get("orderDialogCtrl");
		} else {
			orderDialogCtrl = null;
		}

		if (args.containsKey("orderListCtrl")) {
			orderListCtrl = (OrderListCtrl) args.get("orderListCtrl");
		} else {
			orderListCtrl = null;
		}

		setPageSizeArticleSearch(20);
		paging_ListBoxArticleSearch.setPageSize(getPageSizeArticleSearch());
		paging_ListBoxArticleSearch.setDetailed(true);

		/* Sorting Comparator for search bandbox article list */
		listheader_ArticleSearch_artNr.setSortAscending(new FieldComparator("artNr", true));
		listheader_ArticleSearch_artNr.setSortDescending(new FieldComparator("artNr", true));

		listheader_ArticleSearch_artKurzbezeichnung.setSortAscending(new FieldComparator("artKurzbezeichnung", true));
		listheader_ArticleSearch_artKurzbezeichnung.setSortDescending(new FieldComparator("artKurzbezeichnung", true));

		listBoxArticleSearch.setItemRenderer(new SearchArticleListModelItemRenderer());

		doShowDialog(getAuftragposition());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		orderPositionDialogWindow.setVisible(workspace.isAllowed("orderPositionDialogWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnClose"));

		button_OrderPositionDialog_PrintOrderPositions.setVisible(workspace.isAllowed("button_OrderPositionDialog_PrintOrderPositions"));

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
	public void onClose$orderPositionDialogWindow(Event event) throws Exception {

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
	 * when the "delete" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doDelete();
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
			orderPositionDialogWindow.onClose();
			// Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 */
	private void doClose() throws Exception {

		if (isDataChanged()) {

			// Show a confirm box
			String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			String title = Labels.getLabel("message_Information");

			if (Messagebox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case Messagebox.YES:
						try {
							doSave();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					case Messagebox.NO:
						break; // 
					}
				}
			}

			) == Messagebox.YES) {
			}
		}

		orderPositionDialogWindow.onClose();
	}

	/**
	 * opens the dialog window modal
	 */
	public void doShowDialog(Auftragposition auftragposition) throws InterruptedException {

		// if customer == null then we opened the customerDialog.zul without
		// args for a given customer, so we do a new filiale()
		if (auftragposition == null) {
			auftragposition = getAuftragService().getNewAuftragposition();
		}

		try {
			// fill the components with the data
			if (auftragposition.getAuftrag() != null) {

				artNr.setValue(auftragposition.getArtikel().getArtNr());
				artKurzbezeichnung.setValue(auftragposition.getArtikel().getArtKurzbezeichnung());
				aupMenge.setValue(auftragposition.getAupMenge());
				aupEinzelwert.setValue(auftragposition.getAupEinzelwert());
				aupGesamtwert.setValue(auftragposition.getAupGesamtwert());
			}

			// set Readonly mode accordingly if the object is new or not.
			if (auftragposition.isNew()) {
				btnCtrl.setInitNew();
				doEdit();
			} else {
				btnCtrl.setInitEdit();
				doReadOnly();
			}

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			orderPositionDialogWindow.doModal(); // open the dialog in modal
			// mode
		} catch (Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ helpers +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the old values
	 */
	private void doStoreInitValues() {
		oldVar_artNr = artNr.getValue();
		oldVar_artKurzbezeichnung = artKurzbezeichnung.getValue();
		oldVar_aupMenge = aupMenge.getValue();
		oldVar_aupEinzelwert = aupEinzelwert.getValue();
		oldVar_aupGesamtwert = aupGesamtwert.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		artNr.setValue(oldVar_artNr);
		artKurzbezeichnung.setValue(oldVar_artKurzbezeichnung);
		aupMenge.setValue(oldVar_aupMenge);
		aupEinzelwert.setValue(oldVar_aupEinzelwert);
		aupGesamtwert.setValue(oldVar_aupGesamtwert);
	}

	/**
	 * Calculates all necessary values new.
	 */
	private void doCalculate() {

		if ((!(aupMenge.getValue() == null)) && (!(aupEinzelwert.getValue() == null))) {
			if ((!aupMenge.getValue().equals(new BigDecimal(0))) && (!aupEinzelwert.getValue().equals(new BigDecimal(0)))) {

				BigDecimal count = aupMenge.getValue();
				BigDecimal singlePrice = aupEinzelwert.getValue();
				BigDecimal amount = count.multiply(singlePrice);

				aupGesamtwert.setValue(amount);
			}

		}

	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_artNr != artNr.getValue()) {
			changed = true;
		}
		if (oldVar_artKurzbezeichnung != artKurzbezeichnung.getValue()) {
			changed = true;
		}

		if (oldVar_aupMenge != aupMenge.getValue()) {
			changed = true;
		}

		if (oldVar_aupEinzelwert != aupEinzelwert.getValue()) {
			changed = true;
		}

		if (oldVar_aupGesamtwert != aupGesamtwert.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		artNr.setConstraint(new SimpleConstraint("NO EMPTY"));
		aupMenge.setConstraint("NO EMPTY, NO ZERO");
		aupEinzelwert.setConstraint("NO EMPTY, NO ZERO");
		aupGesamtwert.setConstraint("NO EMPTY, NO ZERO");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		artNr.setConstraint("");
		aupMenge.setConstraint("");
		aupEinzelwert.setConstraint("");
		aupGesamtwert.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void doDelete() throws InterruptedException {

		final Auftrag auftrag = getAuftrag();

		// Show a confirm box
		String msg = "Are you sure to delete this order position ?" + "\n\n --> " + auftragposition.getArtikel().getArtKurzbezeichnung();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					delete();
				case Messagebox.NO:
					break; // 
				}
			}

			private void delete() {

				// delete from database
				getAuftragService().delete(auftragposition);

				// now synchronize the listBox in the parent zul-file
				ListModelList lml = (ListModelList) listBoxOrderOrderPositions.getListModel();
				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml.indexOf(auftragposition) == -1) {
				} else {
					lml.remove(lml.indexOf(auftragposition));
				}

				// +++++++ now synchronize the listBox in the parent zul-file
				// +++ //
				Listbox listBoxOrderArticle = (Listbox) orderListCtrl.getFellow("listBoxOrderArticle");
				// now synchronize the orderposition listBox
				ListModelList lml3 = (ListModelList) listBoxOrderArticle.getListModel();
				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml3.indexOf(auftragposition) == -1) {
				} else {
					lml3.remove(lml3.indexOf(auftragposition));
				}

				orderPositionDialogWindow.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		Auftragposition auftragposition = getAuftragService().getNewAuftragposition();
		setAuftragposition(auftragposition);
		auftragposition.setAuftrag(auftrag);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		// artNr + description are only be filled by searchBox
		artNr.setReadonly(true);
		artKurzbezeichnung.setReadonly(true);
		bandbox_OrderPositionDialog_ArticleSearch.setDisabled(false);

		aupMenge.setReadonly(false);
		aupEinzelwert.setReadonly(false);
		aupGesamtwert.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		// artNr + description are only be filled by searchBox
		artNr.setReadonly(true);
		artKurzbezeichnung.setReadonly(true);
		bandbox_OrderPositionDialog_ArticleSearch.setDisabled(true);

		aupMenge.setReadonly(true);
		aupEinzelwert.setReadonly(true);
		aupGesamtwert.setReadonly(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		artNr.setValue("");
		artKurzbezeichnung.setValue("");
		bandbox_OrderPositionDialog_ArticleSearch.setValue("");
		aupMenge.setValue(new BigDecimal(0));
		aupEinzelwert.setValue(new BigDecimal(0));
		aupGesamtwert.setValue(new BigDecimal(0));

	}

	public void doSave() throws InterruptedException {

		Kunde kunde = getKunde();
		Auftrag auftrag = getAuftrag();
		Auftragposition auftragposition = getAuftragposition();
		Artikel artikel = getArtikel();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		artNr.getValue();

		// additionally calculate new
		if (isDataChanged()) {
			doCalculate();
		}

		// fill the objects with the components data
		auftragposition.setAuftrag(auftrag);
		auftragposition.setArtikel(artikel);
		auftragposition.setAupMenge(aupMenge.getValue());
		auftragposition.setAupEinzelwert(aupEinzelwert.getValue());
		auftragposition.setAupGesamtwert(aupGesamtwert.getValue());

		// save it to database
		try {
			getAuftragService().saveOrUpdate(auftragposition);
		} catch (Exception e) {
			String message = e.getMessage();
			// String message = e.getCause().getMessage();
			String title = Labels.getLabel("message_Error");
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "ERROR", true);

			// Reset to init values
			doResetInitValues();

			doReadOnly();
			btnCtrl.setBtnStatus_Save();
			return;
		}

		OrderDialogCtrl odc = orderDialogCtrl;

		odc.setSearchObjOrderPosition(new HibernateSearchObject(Auftragposition.class));
		odc.getSearchObjOrderPosition().addFilter(new Filter("auftrag", getAuftrag(), Filter.OP_EQUAL));
		// deeper loading of a relation to prevent the lazy
		// loading problem.
		odc.getSearchObjOrderPosition().addFetch("artikel");

		odc.listBoxOrderOrderPositions.setModel(new PagedListWrapper<Auftragposition>(odc.listBoxOrderOrderPositions,
				odc.paging_ListBoxOrderOrderPositions, odc.getTestService().getSRBySearchObject(odc.getSearchObjOrderPosition(), 0,
						odc.getPageSizeOrderPosition()), odc.getSearchObjOrderPosition()));

		Listbox listBoxOrderArticle = (Listbox) orderListCtrl.getFellow("listBoxOrderArticle");
		listBoxOrderArticle.setModel(odc.listBoxOrderOrderPositions.getModel());

		// synchronize the TotalCount from the paging component
		orderListCtrl.paging_OrderArticleList.setTotalSize(odc.paging_ListBoxOrderOrderPositions.getTotalSize());

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$bt_Orders_CustomerSearchClose(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		bbox_Orders_CustomerSearch.close();
	}

	/**
	 * when the "new" button is clicked.
	 * 
	 * Calls the Customer dialog.
	 * 
	 * @param event
	 */
	public void onClick$bt_Orders_CustomerNew(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		Kunde kunde = getKundeService().getNewKunde();
		// kunde.setFiliale(UserWorkspace.getInstance().getFiliale()); // init
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

		// call the zul-file with the parameters packed in a map
		Executions.createComponents("/WEB-INF/pages/customer/customerDialog.zul", null, map);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search article +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button of the search bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_ArticleSearch_Close(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		bandbox_OrderPositionDialog_ArticleSearch.close();
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_ArticleSearch_Search(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSearchArticle();
	}

	public void onOpen$bandbox_OrderPositionDialog_ArticleSearch(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		setSearchObjArticle(new HibernateSearchObject(Artikel.class));
		getSearchObjArticle().addSort("artNr", false);

		listBoxArticleSearch.setModel(new PagedListWrapper<Artikel>(listBoxArticleSearch, paging_ListBoxArticleSearch, getTestService()
				.getSRBySearchObject(getSearchObjArticle(), 0, getPageSizeArticleSearch()), getSearchObjArticle()));

	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Count how many textboxes are filled. <br>
	 * 2. Create a map with the count entries. <br>
	 * 3. Store the propertynames(must corresponds to the domain classes
	 * properties) and values to the map. <br>
	 * 4. Call the ServiceDAO method with the map as parameter. <br>
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	private void doSearchArticle() {

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		setSearchObjArticle(new HibernateSearchObject(Artikel.class));
		getSearchObjArticle().addSort("artNr", false);

		if (StringUtils.isNotEmpty(tb_OrderPosition_SearchArticlelNo.getValue())) {
			getSearchObjArticle().addFilter(new Filter("artNr", "%" + tb_OrderPosition_SearchArticlelNo.getValue() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_OrderPosition_SearchArticleDesc.getValue())) {
			getSearchObjArticle().addFilter(
					new Filter("artKurzbezeichnung", "%" + tb_OrderPosition_SearchArticleDesc.getValue() + "%", Filter.OP_ILIKE));
		}

		listBoxArticleSearch.setModel(new PagedListWrapper<Artikel>(listBoxArticleSearch, paging_ListBoxArticleSearch, getTestService()
				.getSRBySearchObject(getSearchObjArticle(), 0, getPageSizeArticleSearch()), getSearchObjArticle()));

	}

	/**
	 * when doubleClick on a item in the bandbox search list.<br>
	 * <br>
	 * Select the customer and search all orders for him.
	 * 
	 * @param event
	 */
	public void onDoubleClickedArticleItem(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the customer
		Listitem item = listBoxArticleSearch.getSelectedItem();
		if (item != null) {

			// get and cast the seleczted object
			setArtikel((Artikel) item.getAttribute("data"));

			artNr.setValue(artikel.getArtNr());
			artKurzbezeichnung.setValue(artikel.getArtKurzbezeichnung());
			aupEinzelwert.setValue(artikel.getArtPreis());
		}

		// clear old stuff at end, because the NO EMPTY validation
		aupMenge.setValue(new BigDecimal(0));
		aupGesamtwert.setValue(new BigDecimal(0));

		// close the bandbox
		bandbox_OrderPositionDialog_ArticleSearch.close();
	}

	/**
	 * when click on button calculate. <br>
	 * <br>
	 * Calculate the count x singlePrice = amount
	 * 
	 * @param event
	 */
	public void onClick$button_OrderPositionDialog_Calculate(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doCalculate();

	}

	/**
	 * close the article search bandbox. <br>
	 * 
	 * @param event
	 */
	public void onClick$btn_ArticleSearchClose(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// close the bandbox
		bandbox_OrderPositionDialog_ArticleSearch.close();
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
	 * onClick button print. <br>
	 * 
	 * @param event
	 */
	public void onClick$button_OrderPositionDialog_PrintOrderPositions(Event event) throws InterruptedException {
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

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public AuftragService getAuftragService() {
		if (auftragService == null) {
			auftragService = (AuftragService) SpringUtil.getBean("auftragService");
			setAuftragService(auftragService);
		}
		return auftragService;
	}

	public void setAuftragService(AuftragService auftragService) {
		this.auftragService = auftragService;
	}

	public KundeService getKundeService() {
		if (kundeService == null) {
			kundeService = (KundeService) SpringUtil.getBean("kundeService");
			setKundeService(kundeService);
		}
		return kundeService;
	}

	public void setKundeService(KundeService kundeService) {
		this.kundeService = kundeService;
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

	public Auftrag getAuftrag() {
		return auftrag;
	}

	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
	}

	public Auftragposition getAuftragposition() {
		return auftragposition;
	}

	public void setAuftragposition(Auftragposition auftragposition) {
		this.auftragposition = auftragposition;
	}

	public void setSearchObjArticle(HibernateSearchObject<Artikel> searchObjArticle) {
		this.searchObjArticle = searchObjArticle;
	}

	public HibernateSearchObject<Artikel> getSearchObjArticle() {
		return searchObjArticle;
	}

	public void setPageSizeArticleSearch(int pageSizeArticleSearch) {
		this.pageSizeArticleSearch = pageSizeArticleSearch;
	}

	public int getPageSizeArticleSearch() {
		return pageSizeArticleSearch;
	}

}
