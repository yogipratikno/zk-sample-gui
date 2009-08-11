package de.forsthaus.zksample.webui.util;

import java.io.Serializable;

import org.zkoss.zul.Button;

import de.forsthaus.zksample.UserWorkspace;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Button controller for the CRUD buttons in the dialog windows. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * Works by calling the setBtnStatus_xxx where xxx is the kind of pressed <br>
 * button action, i.e. new delete or save. After calling these methods <br>
 * all buttons are disabled/enabled or visible/not visible by <br>
 * param disableButtons. <br>
 * <br>
 * disableButtons = true --> Buttons are disabled/enabled <br>
 * disableButtons = false --> Buttons are visible/not visible <br>
 * 
 * 
 * 
 * @author sge
 * @changes 03/25/2009 sge Extended for security. So we need a right prefix.<br>
 *          That suppose that we have a convention in writing the prefix like <br>
 *          if (workspace.isAllowed(_rightPrefix + "_btnNew"))) {<br>
 *          means that the right have following name:
 *          "button_CustomerDialog_btnNew" <br>
 *          07/24/2009: sge changes for clustering.<br>
 */
public class ButtonStatusCtrl implements Serializable {

	private static final long serialVersionUID = 1L;

	/** NEW Button */
	private transient Button _btnNew;
	/** EDIT Button */
	private transient Button _btnEdit;
	/** DELETE Button */
	private transient Button _btnDelete;
	/** SAVE Button */
	private transient Button _btnSave;
	/** CLOSE Button */
	private transient Button _btnClose;

	/** rightName prefix */
	private transient String _rightPrefix;

	/**
	 * var for disable/enable or visible/not visible mode of the butttons. <br>
	 * true = disable the button <br>
	 * false = make the button unvisible<br>
	 */
	private boolean disableButtons = false;

	/**
	 * Constructor
	 * 
	 * @param btnNew
	 *            (New Button)
	 * @param btnEdit
	 *            (Edit Button)
	 * @param btnDelete
	 *            (Delete Button)
	 * @param btnSave
	 *            (Save Button)
	 * @param btnClose
	 *            (Close Button)
	 */
	public ButtonStatusCtrl(String rightPrefix, Button btnNew, Button btnEdit, Button btnDelete, Button btnSave, Button btnClose) {
		super();
		this._btnNew = btnNew;
		this._btnEdit = btnEdit;
		this._btnDelete = btnDelete;
		this._btnSave = btnSave;
		this._btnClose = btnClose;
		this._rightPrefix = rightPrefix;

		// PREPARED FOR: application wide properties
		// this.disableButtons =
		// AppWorkspace().getInstance().isDisabledButtonsVisible();
	}

	/**
	 * Set all Buttons for the Mode NEW is pressed. <br>
	 */
	public void setBtnStatus_New() {
		UserWorkspace workspace = UserWorkspace.getInstance();

		if (disableButtons) {

			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setDisabled(false);
			}
		} else {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setVisible(true);
			}
		}
	}

	/**
	 * Set all Buttons for the Mode EDIT is pressed. <br>
	 */
	public void setBtnStatus_Edit() {
		UserWorkspace workspace = UserWorkspace.getInstance();

		if (disableButtons) {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setDisabled(false);
			}
		} else {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setVisible(true);
			}
		}
	}

	/**
	 * Not needed yet, because after pressed the delete button <br>
	 * the window is closing. <br>
	 */
	public void setBtnStatus_Delete() {
	}

	/**
	 * Set all Buttons for the Mode SAVE is pressed. <br>
	 */
	public void setBtnStatus_Save() {
		setInitEdit();
	}

	/**
	 * Set all Buttons for the Mode INIT in EDIT mode. <br>
	 * This means that the Dialog window is opened and <br>
	 * shows data. <br>
	 */
	public void setInitEdit() {
		UserWorkspace workspace = UserWorkspace.getInstance();

		if (disableButtons) {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setDisabled(false);
			}
		} else {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setVisible(true);
			}
		}
	}

	/**
	 * Set all Buttons for the Mode INIT in NEW mode. <br>
	 * This means that the Dialog window is freshly new <br>
	 * and have no data. <br>
	 */
	public void setInitNew() {
		UserWorkspace workspace = UserWorkspace.getInstance();

		if (disableButtons) {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setDisabled(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setDisabled(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setDisabled(false);
			}
		} else {
			if (workspace.isAllowed(_rightPrefix + "btnNew")) {
				_btnNew.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnEdit")) {
				_btnEdit.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnDelete")) {
				_btnDelete.setVisible(false);
			}
			if (workspace.isAllowed(_rightPrefix + "btnSave")) {
				_btnSave.setVisible(true);
			}
			if (workspace.isAllowed(_rightPrefix + "btnClose")) {
				_btnClose.setVisible(true);
			}
		}
	}

}
