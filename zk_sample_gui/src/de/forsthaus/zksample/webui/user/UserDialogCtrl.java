package de.forsthaus.zksample.webui.user;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Language;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.user.model.LanguageListModelItemRenderer;
import de.forsthaus.zksample.webui.user.model.UserRolesListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.NoEmptyAndEqualStringsConstraint;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the userDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class UserDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(UserDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window userDialogWindow; // autowired
	protected transient Tab tab_UserDialog_Details; // autowired

	// panel account details
	protected transient Textbox usrLoginname; // autowired
	protected transient Textbox usrPassword; // autowired
	protected transient Textbox usrPasswordRetype; // autowired
	protected transient Textbox usrFirstname; // autowired
	protected transient Textbox usrLastname; // autowired
	protected transient Textbox usrEmail; // autowired
	protected transient Listbox lbox_usrLocale; // autowired

	// panel status
	protected transient Checkbox usrEnabled; // autowired
	protected transient Checkbox usrAccountnonexpired; // autowired
	protected transient Checkbox usrCredentialsnonexpired; // autowired
	protected transient Checkbox usrAccountnonlocked; // autowired

	// panel security token, SORRY logic it's internally
	protected transient Textbox usrToken; // autowired

	// panel granted roles
	protected transient Listbox listBoxDetails_UserRoles; // autowired
	protected transient Listheader listheader_UserDialog_UserRoleId; // autowired
	protected transient Listheader listheader_UserDialog_UserRoleShortDescription; // autowired

	// overhanded vars per params
	private transient Listbox listBoxUser; // overhanded
	private transient SecUser user; // overhanded
	private transient UserListCtrl userListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_usrLoginname;
	private transient String oldVar_usrPassword;
	private transient String oldVar_usrPasswordRetype;
	private transient String oldVar_usrFirstname;
	private transient String oldVar_usrLastname;
	private transient String oldVar_usrEmail;
	private transient Listitem oldVar_usrLangauge;
	private transient boolean oldVar_usrEnabled;
	private transient boolean oldVar_usrAccountnonexpired;
	private transient boolean oldVar_usrCredentialsnonexpired;
	private transient boolean oldVar_usrAccountnonlocked;
	private transient String oldVar_usrToken;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_UserDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowired
	protected transient Button btnEdit; // autowired
	protected transient Button btnDelete; // autowired
	protected transient Button btnSave; // autowired
	protected transient Button btnClose; // autowired

	// checkRights
	protected transient Button btnHelp; // autowired
	protected transient Panel panel_UserDialog_Status; // autowired
	protected transient Panel panel_UserDialog_SecurityToken; // autowired
	protected transient Tabpanel tabpanel_UserDialog_Details; // autowired

	// ServiceDAOs
	private transient UserService userService;

	/**
	 * default constructor.<br>
	 */
	public UserDialogCtrl() {
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
	public void onCreate$userDialogWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* +++ autowire the vars +++ */
		doOnCreateCommon(userDialogWindow, event);

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("user")) {
			user = (SecUser) args.get("user");
			setUser(user);
		} else {
			setUser(null);
		}

		// we get the listBox Object for the users list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete users here.
		if (args.containsKey("listBoxUser")) {
			listBoxUser = (Listbox) args.get("listBoxUser");
		} else {
			listBoxUser = null;
		}

		if (args.containsKey("userListCtrl")) {
			userListCtrl = (UserListCtrl) args.get("userListCtrl");
		} else {
			userListCtrl = null;
		}

		// Set the ListModel and the itemRenderer. The ZKoss ListmodelList do in
		// most times satisfy your needs
		/* Tab Details */
		listBoxDetails_UserRoles.setModel(new ListModelList(getUserService().getRolesByUser(user)));
		listBoxDetails_UserRoles.setItemRenderer(new UserRolesListModelItemRenderer());

		// +++++++++ DropDown ListBox
		// set listModel and itemRenderer for the dropdown listbox
		lbox_usrLocale.setModel(new ListModelList(getUserService().getAllLanguages()));
		lbox_usrLocale.setItemRenderer(new LanguageListModelItemRenderer());

		// if available, select the object
		ListModelList lml = (ListModelList) lbox_usrLocale.getModel();

		if (user.isNew()) {
			lbox_usrLocale.setSelectedIndex(-1);
		} else {
			if (!StringUtils.isEmpty(user.getUsrLocale())) {
				Language lang = getUserService().getLanguageById(user.getUsrLocale());
				lbox_usrLocale.setSelectedIndex(lml.indexOf(lang));
			}
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getUser());
	}

	private void doSetFieldProperties() {
		usrLoginname.setMaxlength(50);
		usrPassword.setMaxlength(50);
		usrPasswordRetype.setMaxlength(50);
		usrFirstname.setMaxlength(50);
		usrLastname.setMaxlength(50);
		usrEmail.setMaxlength(200);
		usrToken.setMaxlength(20);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		userDialogWindow.setVisible(workspace.isAllowed("userDialogWindow"));

		tab_UserDialog_Details.setVisible(workspace.isAllowed("tab_UserDialog_Details"));
		tabpanel_UserDialog_Details.setVisible(workspace.isAllowed("tab_UserDialog_Details"));

		btnHelp.setVisible(workspace.isAllowed("button_UserDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_UserDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_UserDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_UserDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_UserDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_UserDialog_btnClose"));

		panel_UserDialog_Status.setVisible(workspace.isAllowed("panel_UserDialog_Status"));
		panel_UserDialog_SecurityToken.setVisible(workspace.isAllowed("panel_UserDialog_SecurityToken"));
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
	public void onClose$userDialogWindow(Event event) throws Exception {

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
			userDialogWindow.onClose();
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
		userDialogWindow.onClose();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object, if so they
	 * set the readOnly mode accordingly.
	 */
	public void doShowDialog(SecUser user) throws InterruptedException {

		// if user == null then we opened the userDialog.zul without
		// args for a given user, so we do a new SecUser()
		if (user == null) {
			user = getUserService().getNewUser();
			setUser(user);
		} else {
			setUser(user);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (user.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			if (!user.isNew()) {

				usrLoginname.setValue(user.getUsrLoginname());
				usrPassword.setValue(user.getUsrPassword());
				usrPasswordRetype.setValue(user.getUsrPassword());
				usrFirstname.setValue(user.getUsrFirstname());
				usrLastname.setValue(user.getUsrLastname());
				usrEmail.setValue(user.getUsrEmail());

				usrEnabled.setChecked(user.isUsrEnabled());
				usrAccountnonexpired.setChecked(user.isUsrAccountnonexpired());
				usrAccountnonlocked.setChecked(user.isUsrAccountnonlocked());
				usrCredentialsnonexpired.setChecked(user.isUsrCredentialsnonexpired());

				usrToken.setValue(user.getUsrToken());

			}

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			userDialogWindow.doModal(); // open the dialog in modal mode
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
		oldVar_usrLoginname = usrLoginname.getValue();
		oldVar_usrPassword = usrPassword.getValue();
		oldVar_usrPasswordRetype = usrPasswordRetype.getValue();
		oldVar_usrFirstname = usrFirstname.getValue();
		oldVar_usrLastname = usrLastname.getValue();
		oldVar_usrEmail = usrEmail.getValue();
		oldVar_usrLangauge = lbox_usrLocale.getSelectedItem();
		oldVar_usrEnabled = usrEnabled.isChecked();
		oldVar_usrAccountnonexpired = usrAccountnonexpired.isChecked();
		oldVar_usrCredentialsnonexpired = usrCredentialsnonexpired.isChecked();
		oldVar_usrAccountnonlocked = usrAccountnonlocked.isChecked();
		oldVar_usrToken = usrToken.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		usrLoginname.setValue(oldVar_usrLoginname);
		usrPassword.setValue(oldVar_usrPassword);
		usrPasswordRetype.setValue(oldVar_usrPasswordRetype);
		usrFirstname.setValue(oldVar_usrFirstname);
		usrLastname.setValue(oldVar_usrLastname);
		usrEmail.setValue(oldVar_usrEmail);
		lbox_usrLocale.setSelectedItem(oldVar_usrLangauge);
		usrEnabled.setChecked(oldVar_usrEnabled);
		usrAccountnonexpired.setChecked(oldVar_usrAccountnonexpired);
		usrCredentialsnonexpired.setChecked(oldVar_usrCredentialsnonexpired);
		usrAccountnonlocked.setChecked(oldVar_usrAccountnonlocked);
		usrToken.setValue(oldVar_usrToken);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_usrLoginname != usrLoginname.getValue()) {
			changed = true;
		}
		if (oldVar_usrPassword != usrPassword.getValue()) {
			changed = true;
		}
		if (oldVar_usrPasswordRetype != usrPasswordRetype.getValue()) {
			changed = true;
		}
		if (oldVar_usrFirstname != usrFirstname.getValue()) {
			changed = true;
		}
		if (oldVar_usrLastname != usrLastname.getValue()) {
			changed = true;
		}
		if (oldVar_usrEmail != usrEmail.getValue()) {
			changed = true;
		}
		if (oldVar_usrLangauge != lbox_usrLocale.getSelectedItem()) {
			changed = true;
		}
		if (oldVar_usrEnabled != usrEnabled.isChecked()) {
			changed = true;
		}
		if (oldVar_usrAccountnonexpired != usrAccountnonexpired.isChecked()) {
			changed = true;
		}
		if (oldVar_usrCredentialsnonexpired != usrCredentialsnonexpired.isChecked()) {
			changed = true;
		}
		if (oldVar_usrAccountnonlocked != usrAccountnonlocked.isChecked()) {
			changed = true;
		}
		if (oldVar_usrToken != usrToken.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		usrLoginname.setConstraint("NO EMPTY");
		usrPassword.setConstraint("NO EMPTY");
		usrPasswordRetype.setConstraint(new NoEmptyAndEqualStringsConstraint(usrPassword));
		usrFirstname.setConstraint("NO EMPTY");
		usrLastname.setConstraint("NO EMPTY");

		// TODO helper textbox for selectedItem ?????
		// rigType.getSelectedItem()) {

	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		usrLoginname.setConstraint("");
		usrPassword.setConstraint("");
		usrPasswordRetype.setConstraint("");
		usrFirstname.setConstraint("");
		usrLastname.setConstraint("");

		// TODO helper textbox for selectedItem ?????
		// rigType.getSelectedItem()) {

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private void doDelete() throws InterruptedException {

		final SecUser user = getUser();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> " + user.getUsrLoginname() + " | "
				+ user.getUsrFirstname() + " ," + user.getUsrLastname();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					deleteUser();
				case Messagebox.NO:
					break; // 
				}
			}

			private void deleteUser() {

				// delete from database
				getUserService().delete(user);

				// now synchronize the listBox
				ListModelList lml = (ListModelList) listBoxUser.getListModel();

				// Check if the object is new or updated
				// -1 means that the obj is not in the list, so it's new..
				if (lml.indexOf(user) == -1) {
				} else {
					lml.remove(lml.indexOf(user));
				}

				userDialogWindow.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		SecUser user = getUserService().getNewUser();
		setUser(user);

		// these comps needed to be init
		usrEnabled.setChecked(false);
		usrAccountnonexpired.setChecked(true);
		usrAccountnonlocked.setChecked(true);
		usrCredentialsnonexpired.setChecked(true);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		usrLoginname.setReadonly(false);
		usrPassword.setReadonly(false);
		usrPasswordRetype.setReadonly(false);
		usrFirstname.setReadonly(false);
		usrLastname.setReadonly(false);
		usrEmail.setReadonly(false);
		lbox_usrLocale.setDisabled(false);

		usrEnabled.setDisabled(false);
		usrAccountnonexpired.setDisabled(false);
		usrAccountnonlocked.setDisabled(false);
		usrCredentialsnonexpired.setDisabled(false);

		usrToken.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		usrLoginname.setReadonly(true);
		usrPassword.setReadonly(true);
		usrPasswordRetype.setReadonly(true);
		usrFirstname.setReadonly(true);
		usrLastname.setReadonly(true);
		usrEmail.setReadonly(true);
		lbox_usrLocale.setDisabled(true);

		usrEnabled.setDisabled(true);
		usrAccountnonexpired.setDisabled(true);
		usrAccountnonlocked.setDisabled(true);
		usrCredentialsnonexpired.setDisabled(true);

		usrToken.setReadonly(true);
	}

	public void doClear() {

		// temporarely disable the validation to allow the field's clearing
		doRemoveValidation();

		usrLoginname.setValue("");
		usrPassword.setValue("");
		usrPasswordRetype.setValue("");
		usrFirstname.setValue("");
		usrLastname.setValue("");
		usrEmail.setValue("");

		usrEnabled.setChecked(false);
		usrAccountnonexpired.setChecked(true);
		usrAccountnonlocked.setChecked(true);
		usrCredentialsnonexpired.setChecked(true);

		usrToken.setValue("");
	}

	public void doSave() throws InterruptedException {

		SecUser user = getUser();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the object with the components data
		user.setUsrLoginname(usrLoginname.getValue());
		user.setUsrPassword(usrPassword.getValue());
		user.setUsrFirstname(usrFirstname.getValue());
		user.setUsrLastname(usrLastname.getValue());
		user.setUsrEmail(usrEmail.getValue());

		if (usrEnabled.isChecked() == true) {
			user.setUsrEnabled(true);
		} else {
			user.setUsrEnabled(false);
		}

		if (usrAccountnonexpired.isChecked() == true) {
			user.setUsrAccountnonexpired(true);
		} else {
			user.setUsrAccountnonexpired(false);
		}

		if (usrAccountnonlocked.isChecked() == true) {
			user.setUsrAccountnonlocked(true);
		} else {
			user.setUsrAccountnonlocked(false);
		}

		if (usrCredentialsnonexpired.isChecked() == true) {
			user.setUsrCredentialsnonexpired(true);
		} else {
			user.setUsrCredentialsnonexpired(false);
		}

		user.setUsrToken(usrToken.getValue());

		usrPassword.getValue();
		usrPasswordRetype.getValue();

		/* if a language is selected get the object from the listbox */
		Listitem item = lbox_usrLocale.getSelectedItem();

		if (item != null) {
			ListModelList lml1 = (ListModelList) lbox_usrLocale.getListModel();
			Language lang = (Language) lml1.get(item.getIndex());
			user.setUsrLocale(lang.getLanId());
		}

		// save it to database
		try {
			getUserService().saveOrUpdate(user);
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
		ListModelList lml = (ListModelList) listBoxUser.getListModel();

		// Check if the object is new or updated
		// -1 means that the obj is not in the list, so it's new.
		if (lml.indexOf(user) == -1) {
			lml.add(user);
		} else {
			lml.set(lml.indexOf(user), user);
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

	public UserService getUserService() {
		if (userService == null) {
			userService = (UserService) SpringUtil.getBean("userService");
			setUserService(userService);
		}
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SecUser getUser() {
		return user;
	}

	public void setUser(SecUser user) {
		this.user = user;
	}

}
