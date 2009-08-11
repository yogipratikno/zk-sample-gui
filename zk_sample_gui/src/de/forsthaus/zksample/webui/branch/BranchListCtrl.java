/*
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * This is the controller class for the brancheList.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * it extends from our BaseCtrl class.
 * 
 * 
 * 
 */
package de.forsthaus.zksample.webui.branch;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.branch.model.BranchListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

public class BranchListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 2038742641853727975L;
	private transient final static Logger logger = Logger.getLogger(BranchListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_BranchesList; // autowire
	protected transient Borderlayout borderLayout_branchList; // autowire

	// filter components
	protected transient Checkbox checkbox_Branch_ShowAll; // autowire
	protected transient Textbox tb_Branch_Name; // aurowire
	protected transient Textbox tb_Branch_No; // aurowire

	// listBox
	protected transient Paging paging_BranchList; // autowired
	protected transient Listbox listBoxBranch; // autowired
	protected transient Listheader listheader_Branch_Description; // autowired
	protected transient Listheader listheader_Branch_No; // autowired

	// checkRights
	protected transient Button btnHelp;
	protected transient Button button_BranchList_NewBranch;
	protected transient Button button_BranchList_PrintBranches;
	protected transient Button button_BranchList_Search_BranchName;
	protected transient Button button_BranchList_Search_BranchNo;

	private transient HibernateSearchObject<Branche> searchObjBranch;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain classes
	private transient BrancheService brancheService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public BranchListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$window_BranchesList(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps and vars */
		doOnCreateCommon(window_BranchesList, event);

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 155);
		countRows = Math.round(maxListBoxHeight / 17);
		// listBoxBranch.setPageSize(countRows);

		borderLayout_branchList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all branches
		checkbox_Branch_ShowAll.setChecked(true);

		// ++ create the searchObject and init sorting ++//
		setSearchObjBranch(new HibernateSearchObject(Branche.class));
		getSearchObjBranch().addSort("braBezeichnung", false);

		// set the paging params
		paging_BranchList.setPageSize(countRows);
		paging_BranchList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_Branch_No.setSortAscending(new FieldComparator("braNr", true));
		listheader_Branch_No.setSortDescending(new FieldComparator("braNr", false));
		listheader_Branch_Description.setSortAscending(new FieldComparator("braBezeichnung", true));
		listheader_Branch_Description.setSortDescending(new FieldComparator("braBezeichnung", false));

		listBoxBranch.setModel(new PagedListWrapper<Branche>(listBoxBranch, paging_BranchList, getTestService().getSRBySearchObject(
				searchObjBranch, 0, countRows), searchObjBranch));

		listBoxBranch.setItemRenderer(new BranchListModelItemRenderer());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_BranchesList.setVisible(workspace.isAllowed("window_BranchesList"));
		btnHelp.setVisible(workspace.isAllowed("button_BranchList_btnHelp"));
		button_BranchList_NewBranch.setVisible(workspace.isAllowed("button_BranchList_NewBranch"));
		button_BranchList_PrintBranches.setVisible(workspace.isAllowed("button_BranchList_PrintBranches"));
		button_BranchList_Search_BranchName.setVisible(workspace.isAllowed("button_BranchList_Search_BranchName"));
		button_BranchList_Search_BranchNo.setVisible(workspace.isAllowed("button_BranchList_Search_BranchNo"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	@SuppressWarnings("unchecked")
	public void onDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxBranch.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			Branche branche = (Branche) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + branche.getBraBezeichnung());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("branche", branche);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("lbBranch", listBoxBranch);
			/*
			 * we do additionally handed over the whole controller.
			 */
			map.put("branchCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;

			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/branch/branchDialog.zul", null, map);
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

	/*
	 * call the branche dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_BranchList_NewBranch(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new branch object
		Branche branche = getBrancheService().getNewBranche();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("branche", branche);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("lbBranch", listBoxBranch);
		map.put("branchCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {

			win = (Window) Executions.createComponents("/WEB-INF/pages/branch/branchDialog.zul", null, map);
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

	/*
	 * call the binding new
	 */
	public void doBindNew() {
		binder.loadAll();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_Branch_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_Branch_Name.setValue(""); // clear
		tb_Branch_No.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		setSearchObjBranch(new HibernateSearchObject(Branche.class));
		getSearchObjBranch().addSort("braBezeichnung", false);

		listBoxBranch.setModel(new PagedListWrapper<Branche>(listBoxBranch, paging_BranchList, getTestService().getSRBySearchObject(
				getSearchObjBranch(), 0, countRows), getSearchObjBranch()));

	}

	/**
	 * when the "print branches list" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_BranchList_PrintBranches(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

	}

	/**
	 * Filter the article list with 'like Branch Number'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_BranchList_Search_BranchNo(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_Branch_No.getValue().isEmpty()) {
			checkbox_Branch_ShowAll.setChecked(false); // unCheck
			tb_Branch_Name.setValue(""); // clear

			// ++ create the searchObject and init sorting ++//
			setSearchObjBranch(new HibernateSearchObject(Branche.class));
			getSearchObjBranch().addFilter(new Filter("braNr", "%" + tb_Branch_No.getValue() + "%", Filter.OP_ILIKE));
			getSearchObjBranch().addSort("braNr", false);

			listBoxBranch.setModel(new PagedListWrapper<Branche>(listBoxBranch, paging_BranchList, getTestService().getSRBySearchObject(
					getSearchObjBranch(), 0, countRows), getSearchObjBranch()));
		}
	}

	/**
	 * Filter the article list with 'like branch name'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_BranchList_Search_BranchName(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_Branch_Name.getValue().isEmpty()) {
			checkbox_Branch_ShowAll.setChecked(false); // unCheck
			tb_Branch_No.setValue(""); // clear

			// ++ create the searchObject and init sorting ++//
			setSearchObjBranch(new HibernateSearchObject(Branche.class));
			getSearchObjBranch().addFilter(new Filter("braBezeichnung", "%" + tb_Branch_Name.getValue() + "%", Filter.OP_ILIKE));
			getSearchObjBranch().addSort("braBezeichnung", false);

			listBoxBranch.setModel(new PagedListWrapper<Branche>(listBoxBranch, paging_BranchList, getTestService().getSRBySearchObject(
					searchObjBranch, 0, countRows), searchObjBranch));
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public BrancheService getBrancheService() {
		if (brancheService == null) {
			brancheService = (BrancheService) SpringUtil.getBean("brancheService");
			setBrancheService(brancheService);
		}
		return brancheService;
	}

	private TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
			setTestService(testService);
		}
		return testService;
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	public void setSearchObjBranch(HibernateSearchObject<Branche> searchObjBranch) {
		this.searchObjBranch = searchObjBranch;
	}

	public HibernateSearchObject<Branche> getSearchObjBranch() {
		return searchObjBranch;
	}

}
