package de.forsthaus.zksample.webui.article;

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

import de.forsthaus.backend.model.Artikel;
import de.forsthaus.backend.service.ArtikelService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.article.model.ArticleListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ This is the
 * controller class for the articleList.zul file.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * it extends from our BaseCtrl class.
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changings for clustering.<br>
 * 
 */
public class ArticleListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 2038742641853727975L;
	private transient final static Logger logger = Logger.getLogger(ArticleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window window_ArticlesList; // autowired

	// search/filter components
	protected transient Checkbox checkbox_ArticleList_ShowAll; // autowired
	protected transient Textbox tb_Article_ArticleID; // aurowired
	protected transient Textbox tb_Article_Name; // aurowired

	// listbox articles
	protected transient Borderlayout borderLayout_articleList; // autowired
	protected transient Paging paging_ArticleList; // autowired
	protected transient Listbox listBoxArticle; // autowired
	protected transient Listheader listheader_ArticleList_No; // autowired
	protected transient Listheader listheader_ArticleList_ShortDescr; // autowired
	protected transient Listheader listheader_ArticleList_SinglePrice; // autowired

	// textbox long description
	protected transient Textbox longBoxArt_LangBeschreibung; // autowired

	// checkRights
	protected transient Button btnHelp;
	protected transient Button button_ArticleList_NewArticle;
	protected transient Button button_ArticleList_PrintList;

	// count of rows in the listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient ArtikelService artikelService;
	private transient TestService testService;

	private transient HibernateSearchObject<Artikel> searchObjArticle;

	public ArticleListCtrl() {
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$window_ArticlesList(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps the vars */
		doOnCreateCommon(window_ArticlesList, event);

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 210);
		countRows = Math.round(maxListBoxHeight / 14);
		// listBoxArticle.setPageSize(countRows);

		borderLayout_articleList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all articles
		checkbox_ArticleList_ShowAll.setChecked(true);

		// ++ create the searchObject and init sorting ++ //
		searchObjArticle = new HibernateSearchObject(Artikel.class);
		searchObjArticle.addSort("artNr", false);
		setSearchObjArticle(searchObjArticle);

		// set the paging params
		int pageSize = countRows;
		paging_ArticleList.setPageSize(pageSize);
		paging_ArticleList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_ArticleList_No.setSortAscending(new FieldComparator("artNr", true));
		listheader_ArticleList_No.setSortDescending(new FieldComparator("artNr", false));
		listheader_ArticleList_ShortDescr.setSortAscending(new FieldComparator("artKurzbezeichnung", true));
		listheader_ArticleList_ShortDescr.setSortDescending(new FieldComparator("artKurzbezeichnung", false));
		listheader_ArticleList_SinglePrice.setSortAscending(new FieldComparator("artPreis", true));
		listheader_ArticleList_SinglePrice.setSortDescending(new FieldComparator("artPreis", false));

		// Set the ListModel for the articles.
		listBoxArticle.setModel(new PagedListWrapper<Artikel>(listBoxArticle, paging_ArticleList, getTestService().getSRBySearchObject(
				searchObjArticle, 0, pageSize), searchObjArticle));

		// set the itemRenderer
		listBoxArticle.setItemRenderer(new ArticleListModelItemRenderer());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		window_ArticlesList.setVisible(workspace.isAllowed("window_ArticlesList"));
		btnHelp.setVisible(workspace.isAllowed("button_ArticlesList_btnHelp"));
		button_ArticleList_NewArticle.setVisible(workspace.isAllowed("button_ArticleList_NewArticle"));
		button_ArticleList_PrintList.setVisible(workspace.isAllowed("button_ArticleList_PrintList"));
	}

	/**
	 * If the user cliecked on a item in the list. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onArticleItemClicked(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listBoxArticle.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			Artikel artikel = (Artikel) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + artikel.getArtKurzbezeichnung());
			}

			longBoxArt_LangBeschreibung.setValue(artikel.getArtLangbezeichnung());
		}
	}

	@SuppressWarnings("unchecked")
	public void onDoubleClicked(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// get the selected object
		Listitem item = listBoxArticle.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			Artikel artikel = (Artikel) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + artikel.getArtKurzbezeichnung());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("artikel", artikel);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("lbArticle", listBoxArticle);
			map.put("articleCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/article/articleDialog.zul", null, map);
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
	 * call the article dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_ArticleList_NewArticle(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		Artikel artikel = getArtikelService().getNewArtikel();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("artikel", artikel);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("lbArticle", listBoxArticle);
		map.put("articleCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/article/articleDialog.zul", null, map);
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

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_ArticleList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_Article_ArticleID.setValue(""); // clear
		tb_Article_Name.setValue(""); // clear

		// ++ create the searchObject and init sorting ++ //
		searchObjArticle = new HibernateSearchObject(Artikel.class);
		searchObjArticle.addSort("artNr", false);
		setSearchObjArticle(searchObjArticle);

		// Set the ListModel for the articles.
		listBoxArticle.setModel(new PagedListWrapper<Artikel>(listBoxArticle, paging_ArticleList, getTestService().getSRBySearchObject(
				searchObjArticle, 0, paging_ArticleList.getPageSize()), searchObjArticle));

	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ArticleList_PrintList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Filter the article list with 'like ArticleID'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_ArticleList_SearchArticleID(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_Article_ArticleID.getValue().isEmpty()) {
			checkbox_ArticleList_ShowAll.setChecked(false); // unCheck
			tb_Article_Name.setValue(""); // clear

			// ++ create the searchObject and init sorting ++ //
			searchObjArticle = new HibernateSearchObject(Artikel.class);
			searchObjArticle.addFilter(new Filter("artNr", "%" + tb_Article_ArticleID.getValue() + "%", Filter.OP_ILIKE));
			searchObjArticle.addSort("artNr", false);
			setSearchObjArticle(searchObjArticle);

			// Set the ListModel for the articles.
			listBoxArticle.setModel(new PagedListWrapper<Artikel>(listBoxArticle, paging_ArticleList, getTestService().getSRBySearchObject(
					searchObjArticle, 0, paging_ArticleList.getPageSize()), searchObjArticle));

			// listBoxArticle.setModel(new
			// ListModelList(getArtikelService().getArtikelLikeId
			// (tb_Article_ArticleID.getValue())));
		}
	}

	/**
	 * Filter the article list with 'like article shortname'. <br>
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_ArticleList_SearchName(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_Article_Name.getValue().isEmpty()) {
			checkbox_ArticleList_ShowAll.setChecked(false); // unCheck
			tb_Article_ArticleID.setValue(""); // clear

			// ++ create the searchObject and init sorting ++ //
			searchObjArticle = new HibernateSearchObject(Artikel.class);
			searchObjArticle.addFilter(new Filter("artKurzbezeichnung", "%" + tb_Article_Name.getValue() + "%", Filter.OP_ILIKE));
			searchObjArticle.addSort("artKurzbezeichnung", false);
			setSearchObjArticle(searchObjArticle);

			// Set the ListModel for the articles.
			listBoxArticle.setModel(new PagedListWrapper<Artikel>(listBoxArticle, paging_ArticleList, getTestService().getSRBySearchObject(
					searchObjArticle, 0, paging_ArticleList.getPageSize()), searchObjArticle));

		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSearchObjArticle(HibernateSearchObject<Artikel> searchObjArticle) {
		this.searchObjArticle = searchObjArticle;
	}

	public HibernateSearchObject<Artikel> getSearchObjArticle() {
		return searchObjArticle;
	}

	private void setArtikelService(ArtikelService artikelService) {
		this.artikelService = artikelService;
	}

	public ArtikelService getArtikelService() {
		if (artikelService == null) {
			artikelService = (ArtikelService) SpringUtil.getBean("artikelService");
			setArtikelService(artikelService);
		}
		return artikelService;
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	public TestService getTestService() {
		if (testService == null) {
			testService = (TestService) SpringUtil.getBean("testService");
			setTestService(testService);
		}
		return testService;
	}

}
