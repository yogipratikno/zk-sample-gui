package de.forsthaus.zksample.webui.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
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

import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.KeyValuePair;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.AuftragService;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.order.model.OrderSearchCustomerListModelItemRenderer;
import de.forsthaus.zksample.webui.orderposition.model.OrderpositionListModelItemRenderer;
import de.forsthaus.zksample.webui.reports.TestReport;
import de.forsthaus.zksample.webui.reports.util.JRreportWindow;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the orderDialog.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class OrderDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private transient final static Logger logger = Logger.getLogger(OrderDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window orderDialogWindow; // autowired
	protected transient Textbox kunNr; // autowired
	protected transient Textbox kunName1; // autowired
	protected transient Textbox aufNr; // autowired
	protected transient Textbox aufBezeichnung; // autowired

	protected transient Paging paging_ListBoxOrderOrderPositions; // autowired
	protected transient Listbox listBoxOrderOrderPositions; // autowired
	protected transient Listheader listheader_OrderPosList2_Orderpos_No; // autowired
	protected transient Listheader listheader_OrderPosList2_Shorttext; // autowired
	protected transient Listheader listheader_OrderPosList2_Count; // autowired
	protected transient Listheader listheader_OrderPosList2_SinglePrice; // autowired
	protected transient Listheader listheader_OrderPosList2_WholePrice; // autowired

	// search components
	// bandbox for searchCustomer
	protected transient Bandbox bandbox_OrderDialog_CustomerSearch; // autowired
	protected transient Textbox tb_Orders_SearchCustNo; // autowired
	protected transient Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected transient Textbox tb_Orders_SearchCustName1; // autowired
	protected transient Textbox tb_Orders_SearchCustCity; // autowired
	protected transient Paging paging_OrderDialog_CustomerSearchList; // autowired
	protected transient Listbox listBoxCustomerSearch; // autowired
	protected transient Listheader listheader_CustNo_2; // autowired
	protected transient Listheader listheader_CustMatchcode_2; // autowired
	protected transient Listheader listheader_CustName1_2; // autowired
	protected transient Listheader listheader_CustCity_2; // autowired

	// not wired vars
	private transient Auftrag auftrag; // overhanded per param
	private transient Listbox listBoxOrder; // overhanded per param
	private transient OrderListCtrl orderListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_kunNr;
	private transient String oldVar_kunName1;
	private transient String oldVar_aufNr;
	private transient String oldVar_aufBezeichnung;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_OrderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	protected transient Button btnHelp; // autowire
	protected transient Button button_OrderDialog_PrintOrder; // autowire
	protected transient Button button_OrderDialog_NewOrderPosition; // autowire

	private transient HibernateSearchObject<Auftragposition> searchObjOrderPosition;
	private transient HibernateSearchObject<Kunde> searchObjCustomer;

	private transient int pageSizeOrderPosition;

	// ServiceDAOs / Domain Classes
	private transient Kunde kunde;
	private transient AuftragService auftragService;
	private transient KundeService kundeService;
	private transient BrancheService brancheService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public OrderDialogCtrl() {
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
	public void onCreate$orderDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps the vars */
		doOnCreateCommon(orderDialogWindow, event);

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("auftrag")) {
			auftrag = (Auftrag) args.get("auftrag");
			// we must addionally check if there is NO customer object in the
			// order, so its new.
			if (auftrag.getKunde() != null) {
				setAuftrag(auftrag);
				setKunde(auftrag.getKunde());
			}
		} else {
			setAuftrag(null);
		}

		// we get the listBox Object for the offices list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete offices here.
		if (args.containsKey("listBoxOrder")) {
			listBoxOrder = (Listbox) args.get("listBoxOrder");
		} else {
			listBoxOrder = null;
		}

		if (args.containsKey("orderListCtrl")) {
			orderListCtrl = (OrderListCtrl) args.get("orderListCtrl");
		} else {
			orderListCtrl = null;
		}

		pageSizeOrderPosition = 10;
		paging_ListBoxOrderOrderPositions.setPageSize(getPageSizeOrderPosition());
		paging_ListBoxOrderOrderPositions.setDetailed(true);

		// Set the ListModel for the orderPositions.
		if (auftrag != null) {
			if (!auftrag.isNew()) {
				setSearchObjOrderPosition(new HibernateSearchObject(Auftragposition.class));
				getSearchObjOrderPosition().addFilter(new Filter("auftrag", auftrag, Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy
				// loading problem.
				getSearchObjOrderPosition().addFetch("artikel");

				listBoxOrderOrderPositions.setModel(new PagedListWrapper<Auftragposition>(listBoxOrderOrderPositions,
						paging_ListBoxOrderOrderPositions, getTestService().getSRBySearchObject(getSearchObjOrderPosition(), 0,
								getPageSizeOrderPosition()), getSearchObjOrderPosition()));

			}
		}
		listBoxOrderOrderPositions.setItemRenderer(new OrderpositionListModelItemRenderer());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderPosList2_Orderpos_No.setSortAscending(new FieldComparator("aupId", true));
		listheader_OrderPosList2_Orderpos_No.setSortDescending(new FieldComparator("aupId", false));
		listheader_OrderPosList2_Shorttext.setSortAscending(new FieldComparator("artikel.artKurzbezeichnung", true));
		listheader_OrderPosList2_Shorttext.setSortDescending(new FieldComparator("artikel.artKurzbezeichnung", false));
		listheader_OrderPosList2_Count.setSortAscending(new FieldComparator("aupMenge", true));
		listheader_OrderPosList2_Count.setSortDescending(new FieldComparator("aupMenge", false));
		listheader_OrderPosList2_SinglePrice.setSortAscending(new FieldComparator("aupEinzelwert", true));
		listheader_OrderPosList2_SinglePrice.setSortDescending(new FieldComparator("aupEinzelwert", false));
		listheader_OrderPosList2_WholePrice.setSortAscending(new FieldComparator("aupGesamtwert", true));
		listheader_OrderPosList2_WholePrice.setSortDescending(new FieldComparator("aupGesamtwert", false));

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getAuftrag());

	}

	private void doSetFieldProperties() {
		kunNr.setMaxlength(20);
		kunName1.setMaxlength(50);
		aufNr.setMaxlength(20);
		aufBezeichnung.setMaxlength(50);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		orderDialogWindow.setVisible(workspace.isAllowed("orderDialogWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_OrderDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_OrderDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_OrderDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_OrderDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_OrderDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_OrderDialog_btnClose"));

		button_OrderDialog_PrintOrder.setVisible(workspace.isAllowed("button_OrderDialog_PrintOrder"));
		button_OrderDialog_NewOrderPosition.setVisible(workspace.isAllowed("button_OrderDialog_NewOrderPosition"));
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
	public void onClose$orderDialogWindow(Event event) throws Exception {

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
			orderDialogWindow.onClose();
			// Messagebox.show(e.toString());
		}
	}

	/**
	 * when 'print order' is clicked.<br>
	 * 
	 * @param event
	 */
	public void onClick$button_OrderDialog_PrintOrder(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		try {
			doPrintReport();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Prints a Jasperreport with overhanded params. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void doPrintReport() throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> begin with printing");
		}

		// Get the real path for the report
		String repSrc = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reports/Test_Report.jasper");
		String subDir = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reports") + "/";

		// preparing parameters
		Map params = new HashMap();
		params.put("Title", "Sample Order Report");
		params.put("SUBREPORT_DIR", subDir);

		if (logger.isDebugEnabled()) {
			logger.debug("JasperReport : " + repSrc);
			logger.debug("SubDir : " + subDir);
		}

		if (getAuftrag() != null && !getAuftrag().isNew()) {
			System.out.println(getAuftrag().getAufId());
			JRDataSource ds = new TestReport().getBeanCollectionByAuftrag(getAuftrag());
			Component parent = orderListCtrl.getRoot();

			JRreportWindow jrw = new JRreportWindow(parent, true, params, repSrc, ds, "pdf");
		}

	}

	/**
	 * when the "new order position" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_OrderDialog_NewOrderPosition(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new orderPosition object
		Auftragposition auftragposition = getAuftragService().getNewAuftragposition();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("orderListCtrl", orderListCtrl);
		map.put("auftrag", auftrag);
		map.put("auftragposition", auftragposition);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxOrderOrderPositions", listBoxOrderOrderPositions);
		map.put("orderDialogCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/order/orderPositionDialog.zul", null, map);
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

		if (logger.isDebugEnabled()) {
			logger.debug("--> DataIsChanged :" + isDataChanged());
		}

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

		orderDialogWindow.onClose();
	}

	/**
	 * opens the dialog window in modal mode.
	 */
	public void doShowDialog(Auftrag auftrag) throws InterruptedException {

		// if customer == null then we opened the customerDialog.zul without
		// args for a given customer, so we do a new filiale()
		if (auftrag == null) {
			auftrag = getAuftragService().getNewAuftrag();
		}

		// set Readonly mode accordingly if the object is new or not.
		if (auftrag.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			if (auftrag.getKunde() != null) {
				kunNr.setValue(auftrag.getKunde().getKunNr());
				kunName1.setValue(auftrag.getKunde().getKunName1() + " " + auftrag.getKunde().getKunName2() + ", "
						+ auftrag.getKunde().getKunOrt());

			}
			aufNr.setValue(auftrag.getAufNr());
			aufBezeichnung.setValue(auftrag.getAufBezeichnung());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			orderDialogWindow.doModal(); // open the dialog in modal mode
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
		oldVar_kunNr = kunNr.getValue();
		oldVar_kunName1 = kunName1.getValue();
		oldVar_aufNr = aufNr.getValue();
		oldVar_aufBezeichnung = aufBezeichnung.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		kunNr.setValue(oldVar_kunNr);
		kunName1.setValue(oldVar_kunName1);
		aufNr.setValue(oldVar_aufNr);
		aufBezeichnung.setValue(oldVar_aufBezeichnung);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_kunNr != kunNr.getValue()) {
			changed = true;
		}
		if (oldVar_kunName1 != kunName1.getValue()) {
			changed = true;
		}
		if (oldVar_aufNr != aufNr.getValue()) {
			changed = true;
		}
		if (oldVar_aufBezeichnung != aufBezeichnung.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		kunNr.setConstraint(new SimpleConstraint("NO EMPTY"));
		kunName1.setConstraint("NO EMPTY");
		aufNr.setConstraint("NO EMPTY");
		aufBezeichnung.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		kunNr.setConstraint("");
		kunName1.setConstraint("");
		aufNr.setConstraint("");
		aufBezeichnung.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void doDelete() throws InterruptedException {

		final Auftrag auftrag = getAuftrag();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record");
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
				getAuftragService().delete(auftrag);

				// now synchronize the listBox in the parent zul-file
				ListModelList lml = (ListModelList) listBoxOrder.getListModel();

				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml.indexOf(auftrag) == -1) {
				} else {
					lml.remove(lml.indexOf(auftrag));
				}

				orderDialogWindow.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		Auftrag auftrag = getAuftragService().getNewAuftrag();
		setAuftrag(auftrag);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		// kunNr only be filled by searchBandBox
		kunNr.setReadonly(true);
		bandbox_OrderDialog_CustomerSearch.setDisabled(false);
		// kunName1.setReadonly(false);
		aufNr.setReadonly(false);
		aufBezeichnung.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		kunNr.setReadonly(true);
		bandbox_OrderDialog_CustomerSearch.setDisabled(true);
		kunName1.setReadonly(true);
		aufNr.setReadonly(true);
		aufBezeichnung.setReadonly(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		kunNr.setValue("");
		bandbox_OrderDialog_CustomerSearch.setValue("");
		kunName1.setValue("");
		aufNr.setValue("");
		aufBezeichnung.setValue("");

		// clear the listbox
		ListModelList lml = (ListModelList) listBoxOrderOrderPositions.getModel();
		lml.clear();

		// doSetValidation();
	}

	public void doSave() throws InterruptedException {

		Auftrag auftrag = getAuftrag();
		Kunde kunde = getKunde();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		kunNr.getValue();
		kunName1.getValue();
		// bbox_Orders_CustomerSearch.getValue();

		// fill the order object with the components data
		auftrag.setKunde(kunde);
		auftrag.setAufNr(aufNr.getValue());
		auftrag.setAufBezeichnung(aufBezeichnung.getValue());

		// save it to database
		try {
			getAuftragService().saveOrUpdate(auftrag);
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

		// now synchronize the offices listBox
		ListModelList lml = (ListModelList) listBoxOrder.getListModel();

		// Check if the object is new or updated
		// -1 means that the object is not in the list, so its new.
		if (lml.indexOf(auftrag) == -1) {
			lml.add(auftrag);
		} else {
			lml.set(lml.indexOf(auftrag), auftrag);
		}

		// bind the vars new for updating the components
		// officeCtrl.doBindNew();

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++ Order Positions operations ++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when an item in the order positions list is double clicked. <br>
	 * 
	 * @param event
	 */
	public void onDoubleClickedOrderPositionItem(Event event) throws InterruptedException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listBoxOrderOrderPositions.getSelectedItem();

		if (item != null) {
			// store the selected order object
			Auftragposition auftragposition = (Auftragposition) item.getAttribute("data");

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("orderListCtrl", orderListCtrl);
			map.put("auftrag", getAuftrag());
			map.put("auftragposition", auftragposition);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxOrderOrderPositions", listBoxOrderOrderPositions);
			map.put("orderDialogCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/order/orderPositionDialog.zul", null, map);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Close(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		bandbox_OrderDialog_CustomerSearch.close();
	}

	/**
	 * when the "new" button is clicked.
	 * 
	 * Calls the Customer dialog.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_NewCustomer(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		Kunde kunde = getKundeService().getNewKunde();
//		kunde.setFiliale(UserWorkspace.getInstance().getFiliale()); // init
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

	/**
	 * when the "search/filter" button in the bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Search(Event event) {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doSearch();
	}

	public void onOpen$bandbox_OrderDialog_CustomerSearch(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// ++ create the searchObject and init sorting ++//
		searchObjCustomer = new HibernateSearchObject(Kunde.class);
		searchObjCustomer.addSort("kunName1", false);
		setSearchObjCustomer(searchObjCustomer);

		// set the paging params
		paging_OrderDialog_CustomerSearchList.setPageSize(20);
		paging_OrderDialog_CustomerSearchList.setDetailed(true);

		listBoxCustomerSearch.setModel(new PagedListWrapper<Kunde>(listBoxCustomerSearch, paging_OrderDialog_CustomerSearchList,
				getTestService().getSRBySearchObject(searchObjCustomer, 0, 20), searchObjCustomer));

		listBoxCustomerSearch.setItemRenderer(new OrderSearchCustomerListModelItemRenderer());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Create a map with the count entries. <br>
	 * 2. Store the propertynames and values to the map. <br>
	 * 3. Call the ServiceDAO method with the map as parameter. <br>
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	private void doSearch() {

		List<KeyValuePair> list = new ArrayList<KeyValuePair>();

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustNo.getValue())) {
			list.add(new KeyValuePair("kunNr", tb_Orders_SearchCustNo.getValue()));
		}
		if (StringUtils.isNotEmpty(tb_Orders_CustSearchMatchcode.getValue())) {
			list.add(new KeyValuePair("kunMatchcode", tb_Orders_CustSearchMatchcode.getValue()));
		}
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustName1.getValue())) {
			list.add(new KeyValuePair("kunName1", tb_Orders_SearchCustName1.getValue()));
		}
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustCity.getValue())) {
			list.add(new KeyValuePair("kunOrt", tb_Orders_SearchCustCity.getValue()));
		}

		if (!list.isEmpty()) {
			listBoxCustomerSearch.setModel(new ListModelList(getKundeService().getKundenByParams(list)));
		}

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

			// Kunde kunde = (Kunde) item.getAttribute("data");
			// get the customer object
			Kunde kunde = (Kunde) item.getAttribute("data");
			setKunde(kunde);

			kunNr.setValue(getKunde().getKunNr());
			bandbox_OrderDialog_CustomerSearch.setValue(getKunde().getKunNr());
			kunName1.setValue(getKunde().getKunName1() + " " + getKunde().getKunName2() + ", " + getKunde().getKunOrt());
			System.out.println("hier");
		}

		// close the bandbox
		bandbox_OrderDialog_CustomerSearch.close();

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

	public void setSearchObjOrderPosition(HibernateSearchObject<Auftragposition> searchObjOrderPosition) {
		this.searchObjOrderPosition = searchObjOrderPosition;
	}

	public HibernateSearchObject<Auftragposition> getSearchObjOrderPosition() {
		return searchObjOrderPosition;
	}

	public void setPageSizeOrderPosition(int pageSizeOrderPosition) {
		this.pageSizeOrderPosition = pageSizeOrderPosition;
	}

	public int getPageSizeOrderPosition() {
		return pageSizeOrderPosition;
	}

	public void setSearchObjCustomer(HibernateSearchObject<Kunde> searchObjCustomer) {
		this.searchObjCustomer = searchObjCustomer;
	}

	public HibernateSearchObject<Kunde> getSearchObjCustomer() {
		return searchObjCustomer;
	}

}
