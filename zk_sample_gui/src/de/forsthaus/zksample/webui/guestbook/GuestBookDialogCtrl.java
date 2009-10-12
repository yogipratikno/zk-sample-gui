package de.forsthaus.zksample.webui.guestbook;

import java.util.Date;

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

import de.forsthaus.backend.model.GuestBook;
import de.forsthaus.backend.service.GuestBookService;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the guestBookDialog.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class GuestBookDialogCtrl extends BaseCtrl {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(GuestBookDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_GuestBookDialog; // autowired
	protected transient Textbox textbox_gubUsrName; // autowired
	protected transient Textbox textbox_gubSubject; // autowired
	protected transient Textbox textbox_gubText; // autowired

	// overhanded vars per params
	private transient Listbox listbox_GuestBookList; // overhanded
	private transient GuestBookListCtrl guestBookListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_gubUsrName;
	private transient String oldVar_gubSubject;
	private transient String oldVar_gubText;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_GuestBookDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	protected transient Button btnHelp; // autowire

	// ServiceDAOs / Domain classes
	private transient GuestBook guestBook; // overhanded per param
	private transient GuestBookService guestBookService;

	/**
	 * default constructor.<br>
	 */
	public GuestBookDialogCtrl() {
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
	public void onCreate$window_GuestBookDialog(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(window_GuestBookDialog, event); // autowire the vars

		/* set components visible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("guestBook")) {
			guestBook = (GuestBook) args.get("guestBook");
			setGuestBook(guestBook);
		} else {
			setGuestBook(null);
		}

		// we get the listBox Object for the branch list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete branches here.
		if (args.containsKey("listbox_GuestBookList")) {
			listbox_GuestBookList = (Listbox) args.get("listbox_GuestBookList");
		} else {
			listbox_GuestBookList = null;
		}

		if (args.containsKey("guestBookListCtrl")) {
			guestBookListCtrl = (GuestBookListCtrl) args.get("guestBookListCtrl");
		} else {
			guestBookListCtrl = null;
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getGuestBook());

	}

	private void doSetFieldProperties() {
		textbox_gubUsrName.setMaxlength(40);
		textbox_gubSubject.setMaxlength(40);
		textbox_gubText.setMaxlength(1000);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_GuestBookDialog.setVisible(true);

		// TODO we must check wich TabPanel is the first so we can FORCE
		// a click that it become to front.
		btnHelp.setVisible(true);
		btnNew.setVisible(true);
		btnEdit.setVisible(false);
		btnDelete.setVisible(false);
		btnSave.setVisible(true);
		btnClose.setVisible(true);

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
	public void onClose$window_GuestBookDialog(Event event) throws Exception {

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
			window_GuestBookDialog.onClose();
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

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
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

		window_GuestBookDialog.onClose();
	}

	/**
	 * opens the dialog window in modal mode.
	 */
	public void doShowDialog(GuestBook guestBook) throws InterruptedException {

		// if customer == null then we opened the customerDialog.zul without
		// args for a given customer, so we do a new Branche()
		if (guestBook == null) {
			guestBook = getguestBookService().getNewGuestBook();

			guestBook.setGubDate(new Date());
			setGuestBook(guestBook);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (guestBook.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			textbox_gubUsrName.setValue(guestBook.getGubUsrname());
			textbox_gubSubject.setValue(guestBook.getGubSubject());
			textbox_gubText.setValue(guestBook.getGubText());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			window_GuestBookDialog.doModal(); // open the dialog in modal mode
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
		oldVar_gubUsrName = textbox_gubUsrName.getValue();
		oldVar_gubSubject = textbox_gubSubject.getValue();
		oldVar_gubText = textbox_gubText.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		textbox_gubUsrName.setValue(oldVar_gubUsrName);
		textbox_gubSubject.setValue(oldVar_gubSubject);
		textbox_gubText.setValue(oldVar_gubText);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_gubUsrName != textbox_gubUsrName.getValue()) {
			changed = true;
		}
		if (oldVar_gubSubject != textbox_gubSubject.getValue()) {
			changed = true;
		}
		if (oldVar_gubText != textbox_gubText.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		textbox_gubUsrName.setConstraint("NO EMPTY");
		textbox_gubSubject.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		textbox_gubUsrName.setConstraint("");
		textbox_gubSubject.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void doDelete() throws InterruptedException {

		final GuestBook guestBook = getGuestBook();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> " + guestBook.getGubDate() + "/"
				+ guestBook.getGubSubject();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					deleteBranch();
				case Messagebox.NO:
					break; // 
				}
			}

			private void deleteBranch() {

				// delete from database
				getguestBookService().delete(guestBook);

				// now synchronize the branches listBox
				ListModelList lml = (ListModelList) listbox_GuestBookList.getListModel();

				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml.indexOf(guestBook) == -1) {
				} else {
					lml.remove(lml.indexOf(guestBook));
				}

				window_GuestBookDialog.onClose(); // close the dialog
			} // deleteBranch()
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		GuestBook guestBook = getguestBookService().getNewGuestBook();

		// init with actual date
		guestBook.setGubDate(new Date());

		setGuestBook(guestBook);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		textbox_gubUsrName.setReadonly(false);
		textbox_gubSubject.setReadonly(false);
		textbox_gubText.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		textbox_gubUsrName.setReadonly(true);
		textbox_gubSubject.setReadonly(true);
		textbox_gubText.setReadonly(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		textbox_gubUsrName.setValue("");
		textbox_gubSubject.setValue("");
		textbox_gubText.setValue("");
	}

	public void doSave() throws InterruptedException {

		GuestBook guestBook = getGuestBook();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the objects with the components data
		guestBook.setGubUsrname(textbox_gubUsrName.getValue());
		guestBook.setGubSubject(textbox_gubSubject.getValue());
		guestBook.setGubText(textbox_gubText.getValue());

		// save it to database
		try {
			getguestBookService().saveOrUpdate(guestBook);
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

		// now synchronize the branches listBox
		ListModelList lml = (ListModelList) listbox_GuestBookList.getListModel();

		// Check if the branch object is new or updated
		// -1 means that the obj is not in the list, so its new.
		if (lml.indexOf(guestBook) == -1) {
			lml.add(guestBook);
		} else {
			lml.set(lml.indexOf(guestBook), guestBook);
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

	public GuestBookService getguestBookService() {
		if (guestBookService == null) {
			guestBookService = (GuestBookService) SpringUtil.getBean("guestBookService");
			setGuestBookService(guestBookService);
		}
		return guestBookService;
	}

	public void setGuestBookService(GuestBookService guestBookService) {
		this.guestBookService = guestBookService;
	}

	public GuestBook getGuestBook() {
		return guestBook;
	}

	public void setGuestBook(GuestBook guestBook) {
		this.guestBook = guestBook;
	}

}
