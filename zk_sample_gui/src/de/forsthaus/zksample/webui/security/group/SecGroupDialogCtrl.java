package de.forsthaus.zksample.webui.security.group;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the secGroupDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering 10/12/2009: sge changings in
 *          the saving routine.<br>
 * 
 */

public class SecGroupDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(SecGroupDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window secGroupDialogWindow; // autowired
	protected transient Textbox grpShortdescription; // autowired
	protected transient Textbox grpLongdescription; // autowired

	// overhanded vars per params
	private transient Listbox listBoxSecGroups; // overhanded
	private transient SecGroup group; // overhanded
	private transient SecGroupListCtrl secGroupListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_grpShortdescription;
	private transient String oldVar_grpLongdescription;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_SecGroupDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;

	/**
	 * default constructor.<br>
	 */
	public SecGroupDialogCtrl() {
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
	public void onCreate$secGroupDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(secGroupDialogWindow, event);

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("group")) {
			group = (SecGroup) args.get("group");
			setGroup(group);
		} else {
			setGroup(null);
		}

		// we get the listBox Object for the users list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete users here.
		if (args.containsKey("listBoxSecGroups")) {
			listBoxSecGroups = (Listbox) args.get("listBoxSecGroups");
		} else {
			listBoxSecGroups = null;
		}

		if (args.containsKey("secGroupListCtrl")) {
			secGroupListCtrl = (SecGroupListCtrl) args.get("secGroupListCtrl");
		} else {
			secGroupListCtrl = null;
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getGroup());

	}

	private void doSetFieldProperties() {
		grpShortdescription.setMaxlength(30);
		grpLongdescription.setMaxlength(1000);
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
	public void onClose$secGroupDialogWindow(Event event) throws Exception {

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
			secGroupDialogWindow.onClose();
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
		secGroupDialogWindow.onClose();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object, if so they
	 * set the readOnly mode accordingly.
	 */
	public void doShowDialog(SecGroup group) throws InterruptedException {

		// if user == null then we opened the Dialog.zul without
		// args for a given user, so we do a new Object()
		if (group == null) {
			group = getSecurityService().getNewSecGroup();
			setGroup(group);
		} else {
			setGroup(group);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (group.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			if (!group.isNew()) {
				grpShortdescription.setValue(group.getGrpShortdescription());
				grpLongdescription.setValue(group.getGrpLongdescription());
			}

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			secGroupDialogWindow.doModal(); // open the dialog in modal mode
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
		oldVar_grpShortdescription = grpShortdescription.getValue();
		oldVar_grpLongdescription = grpLongdescription.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		grpShortdescription.setValue(oldVar_grpShortdescription);
		grpLongdescription.setValue(oldVar_grpLongdescription);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_grpShortdescription != grpShortdescription.getValue()) {
			changed = true;
		}
		if (oldVar_grpLongdescription != grpLongdescription.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		grpShortdescription.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		grpShortdescription.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doDelete() throws InterruptedException {

		final SecGroup group = getGroup();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> " + group.getGrpShortdescription();
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
				getSecurityService().delete(group);

				// now synchronize the listBox
				ListModelList lml = (ListModelList) listBoxSecGroups.getListModel();

				// Check if the object is new or updated
				// -1 means that the obj is not in the list, so it's new..
				if (lml.indexOf(group) == -1) {
				} else {
					lml.remove(lml.indexOf(group));
				}

				secGroupDialogWindow.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		SecGroup group = getSecurityService().getNewSecGroup();
		setGroup(group);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		grpShortdescription.setReadonly(false);
		grpLongdescription.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		grpShortdescription.setReadonly(true);
		grpLongdescription.setReadonly(true);
	}

	public void doClear() {

		// temporarely disable the validation to allow the field's clearing
		doRemoveValidation();

		grpShortdescription.setValue("");
		grpLongdescription.setValue("");
	}

	public void doSave() throws InterruptedException {

		SecGroup group = getGroup();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the object with the components data
		group.setGrpShortdescription(grpShortdescription.getValue());
		group.setGrpLongdescription(grpLongdescription.getValue());

		// save it to database
		try {
			getSecurityService().saveOrUpdate(group);
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
		ListModelList lml = (ListModelList) listBoxSecGroups.getListModel();

		// Check if the object is new or updated
		// -1 means that the obj is not in the list, so it's new.
		if (lml.indexOf(group) == -1) {
			lml.add(group);
		} else {
			lml.set(lml.indexOf(group), group);
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

	public SecGroup getGroup() {
		return group;
	}

	public void setGroup(SecGroup group) {
		this.group = group;
	}

}
