package de.forsthaus.zksample.webui.security.right;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.zksample.webui.security.right.model.SecRightSecTypListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secRightDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class SecRightDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(SecRightDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secRightDialogWindow; // autowired
	protected transient Textbox rigName; // autowired
	protected transient Listbox rigType; // autowired

	// overhanded vars per params
	private transient Listbox listBoxSecRights; // overhanded
	private transient SecRight right; // overhanded
	private transient SecRightListCtrl secRightListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_rigName;
	private transient Listitem oldVar_rigType;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_SecRightDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowired
	protected transient Button btnEdit; // autowired
	protected transient Button btnDelete; // autowired
	protected transient Button btnSave; // autowired
	protected transient Button btnClose; // autowired

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;

	/**
	 * default constructor.<br>
	 */
	public SecRightDialogCtrl() {
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
	public void onCreate$secRightDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(secRightDialogWindow, event);

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("right")) {
			right = (SecRight) args.get("right");
			setRight(right);
		} else {
			setRight(null);
		}

		// we get the listBox Object for the users list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete users here.
		if (args.containsKey("listBoxSecRights")) {
			listBoxSecRights = (Listbox) args.get("listBoxSecRights");
		} else {
			listBoxSecRights = null;
		}

		if (args.containsKey("secRightListCtrl")) {
			secRightListCtrl = (SecRightListCtrl) args.get("secRightListCtrl");
		} else {
			secRightListCtrl = null;
		}

		// +++++++++ DropDown ListBox
		// set listModel and itemRenderer for the dropdown listbox
		rigType.setModel(new ListModelList(getSecurityService().getAllTypes()));
		rigType.setItemRenderer(new SecRightSecTypListModelItemRenderer());

		// if available, select the object
		ListModelList lml = (ListModelList) rigType.getModel();
		SecTyp typ = getSecurityService().getTypById(right.getRigType());

		if (right.isNew()) {
			rigType.setSelectedIndex(-1);
		} else {
			rigType.setSelectedIndex(lml.indexOf(typ));
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getRight());

	}

	private void doSetFieldProperties() {
		rigName.setMaxlength(50);
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
	public void onClose$secRightDialogWindow(Event event) throws Exception {

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
			secRightDialogWindow.onClose();
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

		secRightDialogWindow.onClose();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object, if so they
	 * set the readOnly mode accordingly.
	 */
	public void doShowDialog(SecRight right) throws InterruptedException {

		// if user == null then we opened the Dialog.zul without
		// args for a given user, so we do a new Object()
		if (right == null) {
			right = getSecurityService().getNewSecRight();
			setRight(right);
		} else {
			setRight(right);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (right.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			if (!right.isNew()) {
				rigName.setValue(right.getRigName());
			}

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			secRightDialogWindow.doModal(); // open the dialog in modal mode
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
		oldVar_rigName = rigName.getValue();
		oldVar_rigType = rigType.getSelectedItem();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		rigName.setValue(oldVar_rigName);
		rigType.setSelectedItem(oldVar_rigType);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_rigName != rigName.getValue()) {
			changed = true;
		}
		if (oldVar_rigType != rigType.getSelectedItem()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		rigName.setConstraint("NO EMPTY");
		// TODO helper textbox for selectedItem ?????
		// rigType.getSelectedItem()) {
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		rigName.setConstraint("");
		// TODO helper textbox for selectedItem ?????
		// rigType.getSelectedItem()) {
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doDelete() throws InterruptedException {

		final SecRight right = getRight();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> " + right.getRigName();
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
				getSecurityService().delete(right);

				// now synchronize the listBox
				ListModelList lml = (ListModelList) listBoxSecRights.getListModel();

				// Check if the object is new or updated
				// -1 means that the obj is not in the list, so it's new..
				if (lml.indexOf(right) == -1) {
				} else {
					lml.remove(lml.indexOf(right));
				}

				secRightDialogWindow.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		SecRight right = getSecurityService().getNewSecRight();
		setRight(right);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		rigType.setSelectedIndex(-1);

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		rigName.setReadonly(false);
		rigType.setDisabled(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		rigName.setReadonly(true);
		rigType.setDisabled(true);
	}

	public void doClear() {

		// temporarely disable the validation to allow the field's clearing
		doRemoveValidation();

		rigName.setValue("");
		// unselect the last customers branch
		rigType.setSelectedIndex(-1);
	}

	public void doSave() throws InterruptedException {

		SecRight right = getRight();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the object with the components data
		right.setRigName(rigName.getValue());

		// get the selected object from the listbox
		Listitem item = rigType.getSelectedItem();

		if (item == null) {
			try {
				Messagebox.show("Please select a right type !");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		ListModelList lml1 = (ListModelList) rigType.getListModel();
		SecTyp typ = (SecTyp) lml1.get(item.getIndex());
		right.setRigType(typ.getStpId());

		// save it to database
		try {
			getSecurityService().saveOrUpdate(right);
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

		// now synchronize the listBox
		ListModelList lml = (ListModelList) listBoxSecRights.getListModel();

		// Check if the object is new or updated
		// -1 means that the obj is not in the list, so it's new.
		if (lml.indexOf(right) == -1) {
			lml.add(right);
		} else {
			lml.set(lml.indexOf(right), right);
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

	public SecRight getRight() {
		return right;
	}

	public void setRight(SecRight right) {
		this.right = right;
	}

}
