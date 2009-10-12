package de.forsthaus.zksample.webui.guestbook;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.GuestBook;
import de.forsthaus.backend.service.GuestBookService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.guestbook.model.GuestBookListtemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the guestBookList.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class GuestBookListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 2038742641853727975L;
	private transient final static Logger logger = Logger.getLogger(GuestBookListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_GuestBookList; // autowire
	protected transient Borderlayout borderLayout_GuestBookList; // autowire

	// listBox
	protected transient Paging paging_GuestBookList; // autowired
	protected transient Listbox listbox_GuestBookList; // autowired
	protected transient Listheader listheader_GuestBook_gubDate; // autowired
	protected transient Listheader listheader_GuestBook_gubUsrName; // autowired
	protected transient Listheader listheader_GuestBook_gubSubject; // autowired
	protected transient Textbox textbox_GuestBook_gubText; // autowired

	private transient HibernateSearchObject<GuestBook> searchObjGuestBook;

	// row count for listbox
	private transient int countRows;

	private transient GuestBook guestBook;
	private transient GuestBookService guestBookService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public GuestBookListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$window_GuestBookList(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps and vars */
		doOnCreateCommon(window_GuestBookList, event);

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 155);
		countRows = Math.round(maxListBoxHeight / 16);
		// listBoxBranch.setPageSize(countRows);
		countRows = 15;

		borderLayout_GuestBookList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// ++ create the searchObject and init sorting ++//
		setSearchObjGuestBook(new HibernateSearchObject(GuestBook.class));
		getSearchObjGuestBook().addSort("gubDate", true);

		// set the paging params
		paging_GuestBookList.setPageSize(countRows);
		paging_GuestBookList.setDetailed(true);

		// getSearchObjGuestBook().addFilter(new Filter("artCatId",
		// getGuestBook().getCatId(), Filter.OP_EQUAL));

		listbox_GuestBookList.setModel(new PagedListWrapper<GuestBook>(listbox_GuestBookList, paging_GuestBookList, getTestService()
				.getSRBySearchObject(getSearchObjGuestBook(), 0, countRows), getSearchObjGuestBook()));

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_GuestBook_gubDate.setSortAscending(new FieldComparator("gubDate", true));
		listheader_GuestBook_gubDate.setSortDescending(new FieldComparator("gubDate", false));
		listheader_GuestBook_gubUsrName.setSortAscending(new FieldComparator("gubUsrname", true));
		listheader_GuestBook_gubUsrName.setSortDescending(new FieldComparator("gubUsrname", false));
		listheader_GuestBook_gubSubject.setSortAscending(new FieldComparator("gubSubject", true));
		listheader_GuestBook_gubSubject.setSortDescending(new FieldComparator("gubSubject", false));

		listbox_GuestBookList.setItemRenderer(new GuestBookListtemRenderer());

		// init the first entry for showing the long text.
		ListModelList lml = (ListModelList) listbox_GuestBookList.getModel();

		// hmmmm geht nicht
		if (lml.getSize() > 0) {
			System.out.println("kjhkh khjk hkjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
			listbox_GuestBookList.setSelectedIndex(0);
			Listitem item = listbox_GuestBookList.getSelectedItem();
			GuestBook guestBook = (GuestBook) item.getAttribute("data");
			if (guestBook != null) {
				listbox_GuestBookList.setSelectedIndex(0);
			}
		}

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		// window_GuestBookList.setVisible(workspace.isAllowed(
		// "window_BranchesList"));
		window_GuestBookList.setVisible(true);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
	public void onClick$button_GuestBookList_NewEntry(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new branch object
		GuestBook guestBook = getGuestBookService().getNewGuestBook();
		guestBook.setGubDate(new Date());

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("guestBook", guestBook);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listbox_GuestBookList", listbox_GuestBookList);
		map.put("guestBookListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/guestbook/guestBookDialog.zul", null, map);
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

	@SuppressWarnings("unchecked")
	public void onGuestBookItemClicked(Event event) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listbox_GuestBookList.getSelectedItem();

		if (item != null) {

			GuestBook guestBook = (GuestBook) item.getAttribute("data");

			// store the selected customer object
			textbox_GuestBook_gubText.setValue(guestBook.getGubText());
		}
	}

	public void onGuestBookItemDoubleClicked(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listbox_GuestBookList.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			GuestBook guestBook = (GuestBook) item.getAttribute("data");

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("guestBook", guestBook);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listbox_GuestBookList", listbox_GuestBookList);
			map.put("resultListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/guestbook/guestBookDialog.zul", null, map);
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
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
		}
		return testService;
	}

	public void setGuestBookService(GuestBookService guestBookService) {
		this.guestBookService = guestBookService;
	}

	public GuestBookService getGuestBookService() {
		if (guestBookService == null) {
			guestBookService = (GuestBookService) SpringUtil.getBean("guestBookService");
			setGuestBookService(guestBookService);
		}
		return guestBookService;
	}

	public void setSearchObjGuestBook(HibernateSearchObject<GuestBook> searchObjGuestBook) {
		this.searchObjGuestBook = searchObjGuestBook;
	}

	public HibernateSearchObject<GuestBook> getSearchObjGuestBook() {
		return searchObjGuestBook;
	}

	public void setGuestBook(GuestBook guestBook) {
		this.guestBook = guestBook;
	}

	public GuestBook getGuestBook() {
		return guestBook;
	}

}