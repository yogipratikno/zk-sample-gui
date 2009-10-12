package de.forsthaus.zksample.webui.customer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.ChartService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.customer.model.CustomerBrancheListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the customerDialog.zul file. *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * 1. In this dialog we can do the mainly database oriented methods like <br>
 * new, edit, save, delete a customer. Please attention, we have a relation to <br>
 * the table 'filiale'. <br>
 * <br>
 * 2. Before closing this dialog we check if there are unsaved changes. <br>
 * <br>
 * 3. We show the components corresponding to the logged-in user rights. <br>
 * 4. We have a little validation implemented. <br>
 * <br>
 * 5. By selecting the order tab we load a new zul-file in it for showing <br>
 * the orders and the orderpositions. They have their own controllers. <br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class CustomerDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(CustomerDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_customerDialog; // autowired

	// tab Address
	protected transient Tab tabCustomerDialogAddress; // autowired
	protected transient Tabpanel tabPanelCustomerAddress; // autowired
	protected transient Textbox kunNr; // autowired
	protected transient Textbox kunMatchcode; // autowired
	protected transient Textbox kunName1; // autowired
	protected transient Textbox kunName2; // autowired
	protected transient Textbox kunOrt; // autowired
	protected transient Listbox kunBranche; // autowired
	protected transient Checkbox kunMahnsperre; // autowired

	// tab Chart
	protected transient Tab tabCustomerDialogChart; // autowired
	protected transient Tabpanel tabPanelCustomerDialogChart; // autowired

	// tab Orders
	protected transient Tab tabCustomerDialogOrders; // autowired
	protected transient Tabpanel tabPanelCustomerOrders; // autowired

	// tab Memos
	protected transient Tab tabCustomerDialogMemos; // autowired
	protected transient Tabpanel tabPanelCustomerMemos; // autowired

	// overhanded vars per params
	private transient Kunde kunde; // overhanded
	private transient Listbox lbCustomer; // overhanded
	private transient CustomerListCtrl customerCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_kunNr;
	private transient String oldVar_kunMatchcode;
	private transient String oldVar_kunName1;
	private transient String oldVar_kunName2;
	private transient String oldVar_kunOrt;
	private transient Listitem oldVar_kunBranche;
	private transient boolean oldVar_kunMahnsperre;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_CustomerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	protected transient Button btnHelp; // autowire

	// ServiceDAOs / Domain Classes
	private transient BrancheService brancheService;
	private transient KundeService kundeService;
	private transient TestService testService;
	private transient ChartService chartService;

	/**
	 * default constructor.<br>
	 */
	public CustomerDialogCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_customerDialog(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps the vars */
		doOnCreateCommon(window_customerDialog, event);

		/* set components visible dependent of the users rights */
		doCheckRights();
		/* create the Button Controller. Disable not used buttons during working */
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		// READ OVERHANDED params !
		if (args.containsKey("kunde")) {
			kunde = (Kunde) args.get("kunde");
			setKunde(kunde);
		} else {
			setKunde(null);
		}

		// READ OVERHANDED params !
		// we get the listBox Object for the customers list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customers here.
		if (args.containsKey("lbCustomer")) {
			lbCustomer = (Listbox) args.get("lbCustomer");
		} else {
			lbCustomer = null;
		}

		// READ OVERHANDED params !
		// we get the customerListWindow controller
		if (args.containsKey("customerCtrl")) {
			customerCtrl = (CustomerListCtrl) args.get("customerCtrl");
		} else {
			customerCtrl = null;
		}

		// +++++++++ DropDown ListBox +++++++++++++++++++ //
		// set listModel and itemRenderer for the Branch dropdown listbox
		HibernateSearchObject<Branche> so = new HibernateSearchObject(Branche.class);
		so.addSort("braBezeichnung", false);

		kunBranche.setModel(new ListModelList(getTestService().getBySearchObject(so, 0, Integer.MAX_VALUE)));
		kunBranche.setItemRenderer(new CustomerBrancheListModelItemRenderer());

		ListModelList lml = (ListModelList) kunBranche.getModel();

		// get and select the customers branch
		Branche branche = kunde.getBranche();
		kunBranche.setSelectedIndex(lml.indexOf(branche));

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getKunde());

	}

	private void doSetFieldProperties() {
		kunNr.setMaxlength(20);
		kunMatchcode.setMaxlength(20);
		kunName1.setMaxlength(50);
		kunName2.setMaxlength(50);
		kunOrt.setMaxlength(50);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_customerDialog.setVisible(workspace.isAllowed("window_customerDialog"));

		tabCustomerDialogAddress.setVisible(workspace.isAllowed("tab_CustomerDialog_Address"));
		tabPanelCustomerAddress.setVisible(workspace.isAllowed("tab_CustomerDialog_Address"));

		tabCustomerDialogChart.setVisible(workspace.isAllowed("tab_CustomerDialog_Chart"));
		tabPanelCustomerDialogChart.setVisible(workspace.isAllowed("tab_CustomerDialog_Chart"));

		tabCustomerDialogOrders.setVisible(workspace.isAllowed("tab_CustomerDialog_Orders"));
		tabPanelCustomerOrders.setVisible(workspace.isAllowed("tab_CustomerDialog_Orders"));

		tabCustomerDialogMemos.setVisible(workspace.isAllowed("tab_CustomerDialog_Memos"));
		tabPanelCustomerMemos.setVisible(workspace.isAllowed("tab_CustomerDialog_Memos"));

		// TODO we must check wich TabPanel is the first so we can FORCE
		// a click that it become to front.
		btnHelp.setVisible(workspace.isAllowed("button_CustomerDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_CustomerDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_CustomerDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_CustomerDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_CustomerDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_CustomerDialog_btnClose"));

	}

	/**
	 * If we select the tab 'Orders' we load the components from a new zul-file <br>
	 * with his own controller. <br>
	 * 
	 * @param event
	 */
	public void onSelect$tabCustomerDialogOrders(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		kunde = getKunde();

		/* overhanded params to the zul file */
		HashMap map = new HashMap();
		map.put("kunde", kunde);
		map.put("customerDialogCtrl", this);

		// PageSize for the Listboxes
		map.put("rowSizeOrders", new Integer(10));
		map.put("rowSizeOrderPositions", new Integer(10));

		Tabpanel orderTab = (Tabpanel) Path.getComponent("/window_customerDialog/tabPanelCustomerOrders");
		orderTab.getChildren().clear();

		Panel panel = new Panel();
		Panelchildren pChildren = new Panelchildren();

		panel.appendChild(pChildren);
		orderTab.appendChild(panel);

		// call the zul-file and put it on the tab.
		Executions.createComponents("/WEB-INF/pages/order/orderList.zul", pChildren, map);
	}

	/**
	 * If we select the tab 'Chart'. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabCustomerDialogChart(Event event) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		kunde = getKunde();

		/* overhanded params to the zul file */
		HashMap map = new HashMap();
		map.put("kunde", kunde);
		map.put("customerDialogCtrl", this);

		// PageSize for the Listboxes
		map.put("rowSizeOrders", new Integer(10));
		map.put("rowSizeOrderPositions", new Integer(10));

		Tabpanel chartTab = (Tabpanel) Path.getComponent("/window_customerDialog/tabPanelCustomerDialogChart");
		chartTab.getChildren().clear();

		Panel panel = new Panel();
		Panelchildren pChildren = new Panelchildren();

		panel.appendChild(pChildren);
		chartTab.appendChild(panel);

		// call the zul-file and put it on the tab.
		Executions.createComponents("/WEB-INF/pages/customer/customerChart.zul", pChildren, map);

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
	public void onClose$window_customerDialog(Event event) throws Exception {

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
			window_customerDialog.onClose();
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
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {

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
		window_customerDialog.onClose();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object, if so they
	 * set the readOnly mode accordingly.
	 */
	public void doShowDialog(Kunde kunde) throws InterruptedException {

		// if customer == null then we opened the customerDialog.zul without
		// args for a given customer, so we do a new Kunde()
		if (kunde == null) {
			// !!! DO NOT BREAK THE TIERS !!!
			// we don't create a new Kunde() in the frontend.
			// we get it from the backend.
			kunde = getKundeService().getNewKunde();
			setKunde(kunde);
		} else {
			setKunde(kunde);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (kunde.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			kunNr.setValue(kunde.getKunNr());
			kunMatchcode.setValue(kunde.getKunMatchcode());
			kunName1.setValue(kunde.getKunName1());
			kunName2.setValue(kunde.getKunName2());
			kunOrt.setValue(kunde.getKunOrt());
			kunMahnsperre.setChecked(kunde.getKunMahnsperre());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			window_customerDialog.doModal(); // open the dialog in modal mode
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
		oldVar_kunMatchcode = kunMatchcode.getValue();
		oldVar_kunName1 = kunName1.getValue();
		oldVar_kunName2 = kunName2.getValue();
		oldVar_kunOrt = kunOrt.getValue();

		oldVar_kunBranche = kunBranche.getSelectedItem();
		oldVar_kunMahnsperre = kunMahnsperre.isChecked();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		kunNr.setValue(oldVar_kunNr);
		kunMatchcode.setValue(oldVar_kunMatchcode);
		kunName1.setValue(oldVar_kunName1);
		kunName2.setValue(oldVar_kunName2);
		kunOrt.setValue(oldVar_kunOrt);

		kunBranche.setSelectedItem(oldVar_kunBranche);
		kunMahnsperre.setChecked(oldVar_kunMahnsperre);
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
		if (oldVar_kunMatchcode != kunMatchcode.getValue()) {
			changed = true;
		}
		if (oldVar_kunName1 != kunName1.getValue()) {
			changed = true;
		}
		if (oldVar_kunName2 != kunName2.getValue()) {
			changed = true;
		}
		if (oldVar_kunOrt != kunOrt.getValue()) {
			changed = true;
		}
		if (oldVar_kunBranche != kunBranche.getSelectedItem()) {
			changed = true;
		}
		if (oldVar_kunMahnsperre != kunMahnsperre.isChecked()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		kunNr.setConstraint("NO EMPTY");
		kunMatchcode.setConstraint("NO EMPTY");
		kunName1.setConstraint("NO EMPTY");
		kunOrt.setConstraint("NO EMPTY");
		// TODO helper textbox for selectedItem ?????
		// kunBranche.setConstraint(new SimpleConstraint("NO EMPTY"));
	}

	/**
	 * Disables the Validation by setting the constraints empty.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		kunNr.setConstraint("");
		kunMatchcode.setConstraint("");
		kunName1.setConstraint("");
		kunOrt.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doDelete() throws InterruptedException {

		final Kunde kunde = getKunde();

		// Show a confirm box
		String msg = "Are you sure to delete this customer  ?" + "\n\n --> " + kunde.getKunName1() + " " + kunde.getKunName2() + " ,"
				+ kunde.getKunOrt();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					deleteCustomer();
				case Messagebox.NO:
					break; // 
				}
			}

			private void deleteCustomer() {

				// delete from database
				getKundeService().delete(kunde);

				// now synchronize the customers listBox
				ListModelList lml = (ListModelList) lbCustomer.getListModel();

				// Check if the customer object is new or updated
				// -1 means that the obj is not in the list, so it's new..
				if (lml.indexOf(kunde) == -1) {
				} else {
					lml.remove(lml.indexOf(kunde));
				}

				window_customerDialog.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		// !!! DO NOT BREAK THE TIERS !!!
		// we don't create a new Kunde() in the frontend.
		// we get it from the backend.
		Kunde kunde = getKundeService().getNewKunde();

		// our customer have a table-reference on filiale
		// we take the filiale we have become by logged in
		// kunde.setFiliale(UserWorkspace.getInstance().getFiliale());
		setKunde(kunde);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		kunNr.setReadonly(false);
		kunMatchcode.setReadonly(false);
		kunName1.setReadonly(false);
		kunName2.setReadonly(false);
		kunOrt.setReadonly(false);
		kunBranche.setDisabled(false);
		kunMahnsperre.setDisabled(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		kunNr.setReadonly(true);
		kunMatchcode.setReadonly(true);
		kunName1.setReadonly(true);
		kunName2.setReadonly(true);
		kunOrt.setReadonly(true);
		kunBranche.setDisabled(true);
		kunMahnsperre.setDisabled(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		kunNr.setValue("");
		kunMatchcode.setValue("");
		kunName1.setValue("");
		kunName2.setValue("");
		kunOrt.setValue("");
		kunMahnsperre.setChecked(false);

		// unselect the last customers branch
		kunBranche.setSelectedIndex(0);
	}

	public void doSave() throws InterruptedException {

		Kunde kunde = getKunde();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the customer object with the components data
		kunde.setKunNr(kunNr.getValue());
		kunde.setKunMatchcode(kunMatchcode.getValue());
		kunde.setKunName1(kunName1.getValue());
		kunde.setKunName2(kunName2.getValue());
		kunde.setKunOrt(kunOrt.getValue());

		// get the selected branch object from the listbox
		Listitem item = kunBranche.getSelectedItem();

		if (item == null) {
			try {
				Messagebox.show("Please select a branch !");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		ListModelList lml1 = (ListModelList) kunBranche.getListModel();
		Branche branche = (Branche) lml1.get(item.getIndex());
		kunde.setBranche(branche);

		if (kunMahnsperre.isChecked() == true) {
			kunde.setKunMahnsperre(true);
		} else {
			kunde.setKunMahnsperre(false);
		}

		// save it to database
		try {
			getKundeService().saveOrUpdate(kunde);
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

		// call from cusromerList then synchronize the customers listBox
		if (lbCustomer != null) {

			ListModelList lml = (ListModelList) lbCustomer.getListModel();

			// Check if the customer object is new or updated
			// -1 means that the obj is not in the list, so it's new.
			if (lml.indexOf(kunde) == -1) {
				lml.add(kunde);
			} else {
				lml.set(lml.indexOf(kunde), kunde);
			}

		}

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();

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

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
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

	private TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
		}
		return testService;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	public ChartService getChartService() {
		if (chartService == null) {
			chartService = (ChartService) SpringUtil.getBean("chartService");
		}
		return chartService;
	}

}
