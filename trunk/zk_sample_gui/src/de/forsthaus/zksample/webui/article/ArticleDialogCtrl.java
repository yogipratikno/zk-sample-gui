package de.forsthaus.zksample.webui.article;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Artikel;
import de.forsthaus.backend.service.ArtikelService;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.ButtonStatusCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ This is the
 * controller class for the articleDialog.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * it extends from our BaseCtrl class.
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering
 * 
 */
public class ArticleDialogCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient final static Logger logger = Logger.getLogger(ArticleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_ArticlesDialog; // autowired
	protected transient Textbox artNr; // autowired
	protected transient Textbox artKurzbezeichnung; // autowired
	protected transient Textbox artLangbezeichnung; // autowired
	protected transient Decimalbox artPreis; // autowired

	// not wired vars
	private transient Listbox lbArticle; // overhanded per param
	private transient Artikel artikel; // overhanded per param
	private transient ArticleListCtrl articleCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_artNr;
	private transient String oldVar_artKurzbezeichnung;
	private transient String oldVar_artLangbezeichnung;
	private transient BigDecimal oldVar_artPreis;

	private transient boolean validationOn;

	// Button Controler
	// TODO prefix only for testing is same as Customers
	private transient String btnCtroller_ClassPrefix = "button_ArticlesDialog_";

	private transient ButtonStatusCtrl btnCtrl;
	protected transient Button btnNew; // autowired
	protected transient Button btnEdit; // autowired
	protected transient Button btnDelete; // autowired
	protected transient Button btnSave; // autowired
	protected transient Button btnClose; // autowired

	protected transient Button btnHelp; // autowire

	// ServiceDAOs
	private transient ArtikelService artikelService;

	/**
	 * default constructor.<br>
	 */
	public ArticleDialogCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	/**
	 * Before binding articles data and calling the dialog window we check, if
	 * the zul-file is called with a parameter for a selected article object
	 * that is stored in a Map.
	 * 
	 * Code Convention: articleDialogWindow is the 'id' in the zul-file
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ArticlesDialog(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(window_ArticlesDialog, event); // autowire the vars

		/* set components visible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(btnCtroller_ClassPrefix, btnNew, btnEdit, btnDelete, btnSave, btnClose);

		if (args.containsKey("artikel")) {
			artikel = (Artikel) args.get("artikel");
			setArtikel(artikel);
		} else {
			setArtikel(null);
		}

		// we get the listBox Object for the articles list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete articles here.
		if (args.containsKey("lbArticle")) {
			lbArticle = (Listbox) args.get("lbArticle");
		} else {
			lbArticle = null;
		}

		if (args.containsKey("articleCtrl")) {
			articleCtrl = (ArticleListCtrl) args.get("articleCtrl");
		} else {
			articleCtrl = null;
		}

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getArtikel());

	}

	private void doSetFieldProperties() {
		artNr.setMaxlength(20);
		artKurzbezeichnung.setMaxlength(50);
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_ArticlesDialog.setVisible(workspace.isAllowed("window_ArticlesDialog"));

		// TODO we must check wich TabPanel is the first so we can FORCE
		// a click that it become to front.
		btnHelp.setVisible(workspace.isAllowed("button_ArticlesDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_ArticlesDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_ArticlesDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_ArticlesDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_ArticlesDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_ArticlesDialog_btnClose"));

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
	public void onClose$window_ArticlesDialog(Event event) throws Exception {

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
			window_ArticlesDialog.onClose();
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
			String message = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			String title = Labels.getLabel("message_Information");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
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

		window_ArticlesDialog.onClose();
	}

	/**
	 * opens the dialog window modal
	 */
	public void doShowDialog(Artikel artikel) throws InterruptedException {

		// if article == null then we opened the articleDialog.zul without
		// args for a given customer, so we do a new Artikel()
		if (artikel == null) {
			artikel = getArtikelService().getNewArtikel();
		}

		// set Readonly mode accordingly if the object is new or not.
		if (artikel.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			artNr.setValue(artikel.getArtNr());
			artKurzbezeichnung.setValue(artikel.getArtKurzbezeichnung());
			artLangbezeichnung.setValue(artikel.getArtLangbezeichnung());
			artPreis.setValue(artikel.getArtPreis());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			window_ArticlesDialog.doModal(); // open the dialog in modal mode
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
		oldVar_artLangbezeichnung = artLangbezeichnung.getValue();
		oldVar_artPreis = artPreis.getValue();
	}

	/**
	 * Resets the old values
	 */
	private void doResetInitValues() {
		artNr.setValue(oldVar_artNr);
		artKurzbezeichnung.setValue(oldVar_artKurzbezeichnung);
		artLangbezeichnung.setValue(oldVar_artLangbezeichnung);
		artPreis.setValue(oldVar_artPreis);
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
		if (oldVar_artLangbezeichnung != artLangbezeichnung.getValue()) {
			changed = true;
		}
		if (oldVar_artPreis != artPreis.getValue()) {
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
		artKurzbezeichnung.setConstraint("NO EMPTY");
		artPreis.setConstraint("NO EMPTY, NO ZERO");
	}

	/**
	 * Disables the Validation by setting the empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		artNr.setConstraint("");
		artKurzbezeichnung.setConstraint("");
		artPreis.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void doDelete() throws InterruptedException {

		final Artikel artikel = getArtikel();

		// Show a confirm box
		String message = Labels.getLabel("message.question.are_you_sure_to_delete_this_record") + "\n\n --> "
				+ artikel.getArtKurzbezeichnung();
		String title = Labels.getLabel("message_Deleting_Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(message, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.YES:
					deleteArticle();
				case Messagebox.NO:
					break; // 
				}
			}

			private void deleteArticle() {

				// delete from database
				getArtikelService().delete(artikel);

				// now synchronize the branches listBox
				ListModelList lml = (ListModelList) lbArticle.getListModel();

				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's new.
				if (lml.indexOf(artikel) == -1) {
				} else {
					lml.remove(lml.indexOf(artikel));
				}

				window_ArticlesDialog.onClose(); // close the dialog
			}
		}

		) == Messagebox.YES) {
		}

	}

	private void doNew() {

		Artikel artikel = getArtikelService().getNewArtikel();
		setArtikel(artikel);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		doStoreInitValues();
	}

	private void doEdit() {

		artNr.setReadonly(false);
		artKurzbezeichnung.setReadonly(false);
		artLangbezeichnung.setReadonly(false);
		artPreis.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	public void doReadOnly() {

		artNr.setReadonly(true);
		artKurzbezeichnung.setReadonly(true);
		artLangbezeichnung.setReadonly(true);
		artPreis.setReadonly(true);
	}

	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		artNr.setValue("");
		artKurzbezeichnung.setValue("");
		artLangbezeichnung.setValue("");
		artPreis.setValue(new BigDecimal(0));
	}

	public void doSave() throws InterruptedException {

		Artikel artikel = getArtikel();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the customer object with the components data
		artikel.setArtNr(artNr.getValue());
		artikel.setArtKurzbezeichnung(artKurzbezeichnung.getValue());
		artikel.setArtLangbezeichnung(artLangbezeichnung.getValue());
		artikel.setArtPreis(artPreis.getValue());

		// save it to database
		try {
			getArtikelService().saveOrUpdate(artikel);
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
		ListModelList lml = (ListModelList) lbArticle.getListModel();

		// Check if the branch object is new or updated
		// -1 means that the obj is not in the list, so its new.
		if (lml.indexOf(artikel) == -1) {
			lml.add(artikel);
		} else {
			lml.set(lml.indexOf(artikel), artikel);
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

	public ArtikelService getArtikelService() {
		if (artikelService == null) {
			artikelService = (ArtikelService) SpringUtil.getBean("artikelService");
			setArtikelService(artikelService);
		}
		return artikelService;
	}

	public void setArtikelService(ArtikelService artikelService) {
		this.artikelService = artikelService;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
}
