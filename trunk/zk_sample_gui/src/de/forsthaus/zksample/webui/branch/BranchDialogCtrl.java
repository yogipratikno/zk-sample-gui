package de.forsthaus.zksample.webui.branch;

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

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ This is the
 * controller class for the branchDialog.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * it extends from our BaseCtrl class.
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class BranchDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(BranchDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_BranchesDialog; // autowired
	protected transient Textbox braNr; // autowired
	protected transient Textbox braBezeichnung; // autowired

	// overhanded vars per param
	private transient Listbox lbBranch; // overhanded
	private transient BranchListCtrl branchCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_braNr;
	private transient String oldVar_braBezeichnung;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient String btnCtroller_ClassPrefix = "button_BranchDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowire
	protected transient Button btnEdit; // autowire
	protected transient Button btnDelete; // autowire
	protected transient Button btnSave; // autowire
	protected transient Button btnClose; // autowire

	protected transient Button btnHelp; // autowire

	// ServiceDAOs / Domain classes
	private transient Branche branche; // overhanded per param
	private transient BrancheService brancheService;

	/**
	 * default constructor.<br>
	 */
	public BranchDialogCtrl() {
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
	public void onCreate$window_BranchesDialog(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(window_BranchesDialog, event); // autowire the vars

		/* set components visible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("branche")) {
			branche = (Branche) args.get("branche");
			setBranche(branche);
		} else {
			setBranche(null);
		}

		// we get the listBox Object for the branch list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete branches here.
		if (args.containsKey("lbBranch")) {
			lbBranch = (Listbox) args.get("lbBranch");
		} else {
			lbBranch = null;
		}

		if (args.containsKey("branchCtrl")) {
			branchCtrl = (BranchListCtrl) args.get("branchCtrl");
		} else {
			branchCtrl = null;
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getBranche());

	}

	private void doSetFieldProperties() {
		braNr.setMaxlength(3);
		braBezeichnung.setMaxlength(30);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_BranchesDialog.setVisible(workspace.isAllowed("window_BranchesDialog"));

		// TODO we must check wich TabPanel is the first so we can FORCE
		// a click that it become to front.
		btnHelp.setVisible(workspace.isAllowed("button_BranchDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_BranchDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_BranchDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_BranchDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_BranchDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_BranchDialog_btnClose"));

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
	public void onClose$window_BranchesDialog(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doClose();
	}

	/**
	 * when the "save" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {

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
			window_BranchesDialog.onClose();
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
			if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case Messagebox.YES:
						doSave();
					case Messagebox.NO:
						break; // 
					}
				}
			}

			) == Messagebox.YES) {
			}
		}

		window_BranchesDialog.onClose();
	}

	/**
	 * opens the dialog window in modal mode.
	 */
	public void doShowDialog(Branche branche) throws InterruptedException {

		// if customer == null then we opened the customerDialog.zul without
		// args for a given customer, so we do a new Branche()
		if (branche == null) {
			branche = getBrancheService().getNewBranche();
		}

		// set Readonly mode accordingly if the object is new or not.
		if (branche.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			braNr.setValue(branche.getBraNr());
			braBezeichnung.setValue(branche.getBraBezeichnung());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			window_BranchesDialog.doModal(); // open the dialog in modal mode
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
		oldVar_braNr = braNr.getValue();
		oldVar_braBezeichnung = braBezeichnung.getValue();
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitValues() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_braNr != braNr.getValue()) {
			changed = true;
		}
		if (oldVar_braBezeichnung != braBezeichnung.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		braNr.setConstraint("NO EMPTY");
		braBezeichnung.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		braNr.setConstraint("");
		braBezeichnung.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void doDelete() throws InterruptedException {

		final Branche branche = getBranche();

		// Show a confirm box
		String msg = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> " + branche.getBraBezeichnung();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					deleteBranch();
				case Messagebox.NO:
					break; // 
				}
			}

			private void deleteBranch() {

				if (branche.getBraNr().equalsIgnoreCase("000")) {
					try {
						// Show a error box
						String msg = Labels.getLabel("message_Cannot_Delete_Default_Branch");
						String title = Labels.getLabel("message_Deleting_Record");

						MultiLineMessageBox.doSetTemplate();
						MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);
						return;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// delete from database
				getBrancheService().delete(branche);

				// now synchronize the branches listBox
				ListModelList lml = (ListModelList) lbBranch.getListModel();

				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml.indexOf(branche) == -1) {
				} else {
					lml.remove(lml.indexOf(branche));
				}

				window_BranchesDialog.onClose(); // close the dialog
			} // deleteBranch()
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		Branche branche = getBrancheService().getNewBranche();
		setBranche(branche);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	private void doEdit() {

		braNr.setReadonly(false);
		braBezeichnung.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		braNr.setReadonly(true);
		braBezeichnung.setReadonly(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		braNr.setValue("");
		braBezeichnung.setValue("");
	}

	public void doSave() {

		Branche branche = getBranche();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the objects with the components data
		branche.setBraNr(braNr.getValue());
		branche.setBraBezeichnung(braBezeichnung.getValue());

		/* check if not the default branch */
		if (branche.getBraNr().equalsIgnoreCase("000")) {

			try {
				// Show a error box
				String msg = Labels.getLabel("message.information.cannot_made_changes_on_system_object");
				String title = Labels.getLabel("window.title.information");

				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "INFORMATION", true);

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			// save to database
			getBrancheService().saveOrUpdate(branche);

			// now synchronize the branches listBox
			ListModelList lml = (ListModelList) lbBranch.getListModel();
			// MyListModelList<Branche> lml = (MyListModelList)
			// lbBranch.getListModel();

			// Check if the branch object is new or updated
			// -1 means that the obj is not in the list, so its new.
			if (lml.indexOf(branche) == -1) {
				lml.add(branche);
			} else {
				lml.set(lml.indexOf(branche), branche);
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

	public Branche getBranche() {
		return branche;
	}

	public void setBranche(Branche branche) {
		this.branche = branche;
	}

}
