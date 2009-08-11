package de.forsthaus.zksample.webui.util.pagging;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;

import com.trg.search.SearchResult;

import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Helper class for allow the sorting of listheaders by paged records. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * All not used Listheaders must me declared as: <br>
 * listheader.setSortAscending(""); <br>
 * listheader.setSortDescending(""); <br>
 * 
 * <br>
 * zkoss 3.6.0 or greater (by using FieldComparator) <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering.<br>
 * 
 */
public class PagedListWrapper<E> extends ListModelList implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(PagedListWrapper.class);

	// Service that calls the DAO methods
	private transient TestService testService;

	// param. The listbox component
	protected transient Listbox _listBox;

	// param. The listboxes paging component
	protected transient Paging _paging;

	// param. Initial list if needed
	private transient List<E> _list;

	// param. Initial SearchResult if needed
	private transient SearchResult<E> _searchResult;

	// param. The SearchObject
	private transient HibernateSearchObject<E> _hibernateSearchObject;

	// paging start row
	private transient int _start;

	// count records a page have to fetch
	private transient int _pageSize;

	// count total records queried (without paging)
	private transient int _totalCount;

	// not used yet. so it's init to 'true'.
	private transient boolean _supportPaging = true;

	// not used yet. so it's init to
	private transient boolean _supportFilter = true;

	/**
	 * Constructor. <br>
	 * with an initial list.
	 * 
	 * @param listBox
	 *            Overhanded listBox. <br>
	 * @param paging
	 *            Overhanded Paging component. <br>
	 * @param initialList
	 *            Overhanded List with initial data. <br>
	 * @param searchObj
	 *            Overhanded SearchObject. <br>
	 */
	public PagedListWrapper(Listbox listBox, Paging paging, List initialList, HibernateSearchObject<E> searchObj) {

		super(initialList);

		this._listBox = listBox;
		this._paging = paging;
		this._pageSize = _paging.getPageSize();
		this._hibernateSearchObject = searchObj;
		this._list = initialList;

		// set_list(initialList);

		setListeners();
	}

	/**
	 * Constructor. <br>
	 * with an initial SearchResult for getting the totalRecordCount and the
	 * list. <br>
	 * 
	 * @param listBox
	 *            Overhanded listBox. <br>
	 * @param paging
	 *            Overhanded Paging component. <br>
	 * @param searchResult
	 *            Overhanded SearchResult Object. <br>
	 * @param searchObj
	 *            Overhanded SearchObject. <br>
	 */
	public PagedListWrapper(Listbox listBox, Paging paging, SearchResult searchResult, HibernateSearchObject searchObj) {

		super(searchResult.getResult());

		this._listBox = listBox;
		this._paging = paging;
		this._paging.setTotalSize(searchResult.getTotalCount());
		this._pageSize = _paging.getPageSize();
		this._searchResult = searchResult;
		this._hibernateSearchObject = searchObj;

		set_list(searchResult.getResult());
		set_totalCount(searchResult.getTotalCount());

		setListeners();
	}

	/**
	 * Sets the listeners. <br>
	 * <br>
	 * 1. "onPaging" for the paging component. <br>
	 * 2. "onSort" for all listheaders that have a sortDirection declared. <br>
	 */
	private void setListeners() {

		// Add 'onPaging' listener to the paging component
		_paging.addEventListener("onPaging", new OnPagingEventListener());

		/*
		 * Add 'onSort' listeners to the used listheader components. All not
		 * used Listheaders must me declared as:
		 * listheader.setSortAscending(""); <br>
		 * listheader.setSortDescending(""); <br>
		 */
		Listhead listhead = _listBox.getListhead();
		List list = listhead.getChildren();

		for (Object object : list) {
			if (object instanceof Listheader) {
				Listheader lheader = (Listheader) object;

				if (lheader.getSortAscending() != null || lheader.getSortDescending() != null) {

					if (logger.isDebugEnabled()) {
						logger.debug("--> : " + lheader.getId());
					}
					lheader.addEventListener("onSort", new OnSortEventListener());
				}
			}
		}

	}

	/**
	 * "onPaging" eventlistener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the methode for refreshing the data with the new rowStart and
	 * pageSize. <br>
	 */
	public final class OnPagingEventListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			PagingEvent pe = (PagingEvent) event;
			int pageNo = pe.getActivePage();
			int start = pageNo * _pageSize;

			if (logger.isDebugEnabled()) {
				logger.debug("--> : " + start + "/" + _pageSize);
			}

			// refresh the list
			refreshModel(get_hibernateSearchObject(), start, _pageSize);
		}
	}

	/**
	 * "onSort" eventlistener for the listheader components. <br>
	 * <br>
	 * Checks wich listheader is clicked and checks which orderDirection must be
	 * set. <br>
	 * 
	 * Calls the methode for refreshing the data with the new ordering. and the
	 * remembered rowStart and pageSize. <br>
	 */
	public final class OnSortEventListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			final Listheader lh = (Listheader) event.getTarget();
			final String sortDirection = lh.getSortDirection();

			if ("ascending".equals(sortDirection)) {
				final Comparator cmpr = lh.getSortDescending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = orderBy.replace("DESC", "").trim();

					// update SearchObject with orderBy
					get_hibernateSearchObject().clearSorts();
					get_hibernateSearchObject().addSort(orderBy, true);
				}
			} else if ("descending".equals(sortDirection) || "natural".equals(sortDirection) || Strings.isBlank(sortDirection)) {
				final Comparator cmpr = lh.getSortAscending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = orderBy.replace("ASC", "").trim();

					// update SearchObject with orderBy
					get_hibernateSearchObject().clearSorts();
					get_hibernateSearchObject().addSort(orderBy, false);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("--> : " + lh.getId() + "/" + sortDirection);
				logger.debug("--> added  getSorts() : " + get_hibernateSearchObject().getSorts().toString());
			}

			if (is_supportPagging()) {
				// refresh the list
				refreshModel(get_hibernateSearchObject(), 0, _pageSize);
				_paging.setActivePage(0);
			}
		}
	}

	/**
	 * Refreshes the list by calling the DAO methode with the modified search
	 * object. <br>
	 * 
	 * @param so
	 *            SearchObject, holds the entity and properties to search. <br>
	 * @param start
	 *            Row to start. <br>
	 * @param pageSize
	 *            Count rows to fetch. <br>
	 */
	private void refreshModel(HibernateSearchObject<E> so, int start, int pageSize) {

		// clear old data
		get_list().clear();

		List list = getTestService().getBySearchObject(so, start, pageSize);
		get_list().addAll((Collection) list);
		_listBox.setModel(new ListModelList(get_list()));

	}

	public void doClear() {
		get_list().clear();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void set_supportPagging(boolean _supportPagging) {
		this._supportPaging = _supportPagging;
	}

	public boolean is_supportPagging() {
		return _supportPaging;
	}

	public void set_supportFilter(boolean _supportFilter) {
		this._supportFilter = _supportFilter;
	}

	public boolean is_supportFilter() {
		return _supportFilter;
	}

	public void set_hibernateSearchObject(HibernateSearchObject<E> hibernateSearchObject) {
		this._hibernateSearchObject = hibernateSearchObject;
	}

	public HibernateSearchObject<E> get_hibernateSearchObject() {
		return _hibernateSearchObject;
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

	public void set_totalCount(int _totalCount) {
		this._totalCount = _totalCount;
	}

	public int get_totalCount() {
		return _totalCount;
	}

	public void set_list(List<E> _list) {
		this._list = _list;
	}

	public List<E> get_list() {
		return _list;
	}

}
