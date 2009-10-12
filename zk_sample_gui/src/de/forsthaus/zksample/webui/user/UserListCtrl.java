package de.forsthaus.zksample.webui.user;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.security.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.user.model.UserListModelItemRenderer;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;
import de.forsthaus.zksample.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the userList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 * 
 */
public class UserListCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 2038742641853727975L;
	private transient final static Logger logger = Logger.getLogger(UserListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window userListWindow; // autowired

	// filter components
	protected transient Checkbox checkbox_UserList_ShowAll; // autowired
	protected transient Textbox tb_SecUser_Loginname; // aurowired
	protected transient Textbox tb_SecUser_Lastname; // aurowired
	protected transient Textbox tb_SecUser_Email; // autowired

	// listbox userList
	protected transient Borderlayout borderLayout_secUserList; // autowired
	protected transient Paging paging_UserList; // autowired
	protected transient Listbox listBoxUser; // aurowired
	protected transient Listheader listheader_UserList_usrLoginname; // autowired
	protected transient Listheader listheader_UserList_usrLastname; // autowired
	protected transient Listheader listheader_UserList_usrEmail; // autowired
	protected transient Listheader listheader_UserList_usrEnabled; // autowired
	protected transient Listheader listheader_UserList_usrAccountnonexpired; // autowired
	protected transient Listheader listheader_UserList_usrCredentialsnonexpired; // autowired
	protected transient Listheader listheader_UserList_usrAccountnonlocked; // autowired

	// checkRights
	protected transient Button btnHelp; // autowired
	protected transient Button button_UserList_NewUser; // autowired
	protected transient Button button_UserList_PrintUserList; // autowired
	protected transient Hbox hbox_UserList_SearchUsers; // autowired

	private transient HibernateSearchObject<SecUser> searchObjUser;

	// row count for listbox
	private transient int countRows;

	// ServiceDAOs / Domain Classes
	private transient UserService userService;
	private transient TestService testService;

	/**
	 * default constructor.<br>
	 */
	public UserListCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$userListWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps and vars */
		doOnCreateCommon(userListWindow, event);

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		/**
		 * calculate how many rows have place on desktop. and set it to the
		 * listBox.
		 */
		int maxListBoxHeight = (UserWorkspace.getInstance().getCurrentDesktopHeight() - 152);
		countRows = Math.round(maxListBoxHeight / 14);
		// listBoxUser.setPageSize(countRows);

		borderLayout_secUserList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// ++ create the searchObject and init sorting ++//
		setSearchObjUser(new HibernateSearchObject(SecUser.class));
		getSearchObjUser().addSort("usrLoginname", false);
		setSearchObjUser(searchObjUser);

		// set the paging params
		paging_UserList.setPageSize(countRows);
		paging_UserList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_UserList_usrLoginname.setSortAscending(new FieldComparator("usrLoginname", true));
		listheader_UserList_usrLoginname.setSortDescending(new FieldComparator("usrLoginname", false));
		listheader_UserList_usrLastname.setSortAscending(new FieldComparator("usrLastname", true));
		listheader_UserList_usrLastname.setSortDescending(new FieldComparator("usrLastname", false));
		listheader_UserList_usrEmail.setSortAscending(new FieldComparator("usrEmail", true));
		listheader_UserList_usrEmail.setSortDescending(new FieldComparator("usrEmail", false));
		listheader_UserList_usrEnabled.setSortAscending(new FieldComparator("usrEnabled", true));
		listheader_UserList_usrEnabled.setSortDescending(new FieldComparator("usrEnabled", false));
		listheader_UserList_usrAccountnonexpired.setSortAscending(new FieldComparator("usrAccountnonexpired", true));
		listheader_UserList_usrAccountnonexpired.setSortDescending(new FieldComparator("usrAccountnonexpired", false));
		listheader_UserList_usrCredentialsnonexpired.setSortAscending(new FieldComparator("usrCredentialsnonexpired", true));
		listheader_UserList_usrCredentialsnonexpired.setSortDescending(new FieldComparator("usrCredentialsnonexpired", false));
		listheader_UserList_usrAccountnonlocked.setSortAscending(new FieldComparator("usrAccountnonlocked", true));
		listheader_UserList_usrAccountnonlocked.setSortDescending(new FieldComparator("usrAccountnonlocked", false));

		/* New check the rights. If UserOnly mode than show only the users data */
		UserWorkspace workspace = UserWorkspace.getInstance();

		// special right for see all other users
		if (workspace.isAllowed("data_SeeAllUserData")) {
			// show all users
			checkbox_UserList_ShowAll.setChecked(true);

			listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
					searchObjUser, 0, countRows), searchObjUser));

		} else {
			// show only logged in users data
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();

			searchObjUser.addFilter(new Filter("usrLoginname", userName, Filter.OP_EQUAL));

			listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
					searchObjUser, 0, countRows), searchObjUser));

		}

		listBoxUser.setItemRenderer(new UserListModelItemRenderer());

		// Assign the Comparator for sorting
		// listheader_UserList_usrLoginname.setSortAscending(new
		// UserComparator(true, UserComparator.FieldsEnum.USER_LOGINNAME));
		// listheader_UserList_usrLoginname.setSortDescending(new
		// UserComparator(false, UserComparator.FieldsEnum.USER_LOGINNAME));
		//
		// listheader_UserList_usrLastname.setSortAscending(new
		// UserComparator(true, UserComparator.FieldsEnum.USER_LASTNAME));
		// listheader_UserList_usrLastname.setSortDescending(new
		// UserComparator(false, UserComparator.FieldsEnum.USER_LASTNAME));
		//
		// listheader_UserList_usrEmail.setSortAscending(new
		// UserComparator(true, UserComparator.FieldsEnum.USER_EMAIL));
		// listheader_UserList_usrEmail.setSortDescending(new
		// UserComparator(false, UserComparator.FieldsEnum.USER_EMAIL));
		//
		// listheader_UserList_usrEnabled.setSortAscending(new
		// UserComparator(true, UserComparator.FieldsEnum.USER_ENABLED));
		// listheader_UserList_usrEnabled.setSortDescending(new
		// UserComparator(false, UserComparator.FieldsEnum.USER_ENABLED));
		//
		// listheader_UserList_usrAccountnonexpired.setSortAscending(new
		// UserComparator(true,
		// UserComparator.FieldsEnum.USER_ACCOUNT_NON_EXPIRED));
		// listheader_UserList_usrAccountnonexpired.setSortDescending(new
		// UserComparator(false,
		// UserComparator.FieldsEnum.USER_ACCOUNT_NON_EXPIRED));
		//
		// listheader_UserList_usrCredentialsnonexpired.setSortAscending(new
		// UserComparator(true,
		// UserComparator.FieldsEnum.USER_CREDENTIALS_NON_EXPIRED));
		// listheader_UserList_usrCredentialsnonexpired.setSortDescending(new
		// UserComparator(false,
		// UserComparator.FieldsEnum.USER_CREDENTIALS_NON_EXPIRED));
		//
		// listheader_UserList_usrAccountnonlocked
		// .setSortAscending(new UserComparator(true,
		// UserComparator.FieldsEnum.USER_ACCOUNT_NON_LOCKED));
		// listheader_UserList_usrAccountnonlocked.setSortDescending(new
		// UserComparator(false,
		// UserComparator.FieldsEnum.USER_ACCOUNT_NON_LOCKED));

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		userListWindow.setVisible(workspace.isAllowed("userListWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_UserList_btnHelp"));
		button_UserList_NewUser.setVisible(workspace.isAllowed("button_UserList_NewUser"));
		button_UserList_PrintUserList.setVisible(workspace.isAllowed("button_UserList_PrintUserList"));

		hbox_UserList_SearchUsers.setVisible(workspace.isAllowed("hbox_UserList_SearchUsers"));

	}

	@SuppressWarnings("unchecked")
	public void onUserListItemDoubleClicked(Event event) throws Exception {

		UserWorkspace workspace = UserWorkspace.getInstance();
		if (!workspace.isAllowed("UserList_listBoxUser.onDoubleClick")) {
			return;
		}

		// get the selected object
		Listitem item = listBoxUser.getSelectedItem();

		if (item != null) {
			// store the selected customer object
			SecUser user = (SecUser) item.getAttribute("data");

			if (logger.isDebugEnabled()) {
				logger.debug("--> " + user.getUsrLoginname());
			}

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap map = new HashMap();
			map.put("user", user);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxUser", listBoxUser);
			map.put("userListCtrl", this);

			// call the zul-file with the parameters packed in a map
			Window win = null;
			try {
				win = (Window) Executions.createComponents("/WEB-INF/pages/sec_user/userDialog.zul", null, map);
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
	 * call the user dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_UserList_NewUser(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// create a new customer object
		SecUser user = getUserService().getNewUser();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap map = new HashMap();
		map.put("user", user);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxUser", listBoxUser);
		map.put("userListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/sec_user/userDialog.zul", null, map);
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
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_UserList_PrintUserList(Event event) throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		String message = Labels.getLabel("message_Not_Implemented_Yet");
		String title = Labels.getLabel("message_Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/*
	 * Filter the user list with 'like Loginname'
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_UserList_SearchLoginname(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecUser_Loginname.getValue().isEmpty()) {
			checkbox_UserList_ShowAll.setChecked(false);
			tb_SecUser_Lastname.setValue("");
			tb_SecUser_Email.setValue("");

			setSearchObjUser(new HibernateSearchObject(SecUser.class));
			getSearchObjUser().addFilter(new Filter("usrLoginname", "%" + tb_SecUser_Loginname.getValue() + "%", Filter.OP_ILIKE));
			getSearchObjUser().addSort("usrLoginname", false);
			setSearchObjUser(searchObjUser);

			/*
			 * New check the rights. If UserOnly mode than show only the users
			 * data
			 */
			UserWorkspace workspace = UserWorkspace.getInstance();

			// special right for see all other users
			if (workspace.isAllowed("data_SeeAllUserData")) {
				// show all users

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			} else {
				// show only logged in users data
				String userName = SecurityContextHolder.getContext().getAuthentication().getName();

				searchObjUser.addFilter(new Filter("usrLoginname", userName, Filter.OP_EQUAL));

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			}

			// listBoxUser.setModel(new
			// ListModelList(getUserService().getUserLikeLoginname
			// (tb_SecUser_Loginname.getValue())));
		}
	}

	/*
	 * Filter the user list with 'like Lastname'
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_UserList_SearchLastname(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecUser_Lastname.getValue().isEmpty()) {
			checkbox_UserList_ShowAll.setChecked(false);
			tb_SecUser_Loginname.setValue("");
			tb_SecUser_Email.setValue("");

			setSearchObjUser(new HibernateSearchObject(SecUser.class));
			getSearchObjUser().addFilter(new Filter("usrLastname", "%" + tb_SecUser_Lastname.getValue() + "%", Filter.OP_ILIKE));
			getSearchObjUser().addSort("usrLoginname", false);
			setSearchObjUser(searchObjUser);

			/*
			 * New check the rights. If UserOnly mode than show only the users
			 * data
			 */
			UserWorkspace workspace = UserWorkspace.getInstance();

			// special right for see all other users
			if (workspace.isAllowed("data_SeeAllUserData")) {
				// show all users

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			} else {
				// show only logged in users data
				String userName = SecurityContextHolder.getContext().getAuthentication().getName();

				searchObjUser.addFilter(new Filter("usrLoginname", userName, Filter.OP_EQUAL));

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			}

			// listBoxUser.setModel(new
			// ListModelList(getUserService().getUserLikeLastname
			// (tb_SecUser_Lastname.getValue())));
		}
	}

	/*
	 * Filter the user list with 'like Email'
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_UserList_SearchEmail(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// if not empty
		if (!tb_SecUser_Email.getValue().isEmpty()) {
			checkbox_UserList_ShowAll.setChecked(false);
			tb_SecUser_Loginname.setValue("");
			tb_SecUser_Lastname.setValue("");

			setSearchObjUser(new HibernateSearchObject(SecUser.class));
			getSearchObjUser().addFilter(new Filter("usrEmail", "%" + tb_SecUser_Email.getValue() + "%", Filter.OP_ILIKE));
			getSearchObjUser().addSort("usrLoginname", false);
			setSearchObjUser(searchObjUser);

			/*
			 * New check the rights. If UserOnly mode than show only the users
			 * data
			 */
			UserWorkspace workspace = UserWorkspace.getInstance();

			// special right for see all other users
			if (workspace.isAllowed("data_SeeAllUserData")) {
				// show all users

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			} else {
				// show only logged in users data
				String userName = SecurityContextHolder.getContext().getAuthentication().getName();

				searchObjUser.addFilter(new Filter("usrLoginname", userName, Filter.OP_EQUAL));

				listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
						searchObjUser, 0, countRows), searchObjUser));

			}

			// listBoxUser.setModel(new
			// ListModelList(getUserService().getUserLikeEmail
			// (tb_SecUser_Email.getValue())));
		}
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked.<br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_UserList_ShowAll(Event event) {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		// empty the text search boxes
		tb_SecUser_Loginname.setValue("");
		tb_SecUser_Lastname.setValue("");
		tb_SecUser_Email.setValue("");

		setSearchObjUser(new HibernateSearchObject(SecUser.class));
		getSearchObjUser().addSort("usrLoginname", false);
		setSearchObjUser(searchObjUser);

		/* New check the rights. If UserOnly mode than show only the users data */
		UserWorkspace workspace = UserWorkspace.getInstance();

		// special right for see all other users
		if (workspace.isAllowed("data_SeeAllUserData")) {
			// show all users
			checkbox_UserList_ShowAll.setChecked(true);

			listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
					searchObjUser, 0, countRows), searchObjUser));

		} else {
			// show only logged in users data
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();

			searchObjUser.addFilter(new Filter("usrLoginname", userName, Filter.OP_EQUAL));

			listBoxUser.setModel(new PagedListWrapper<SecUser>(listBoxUser, paging_UserList, getTestService().getSRBySearchObject(
					searchObjUser, 0, countRows), searchObjUser));

		}

		// listBoxUser.setModel(new
		// ListModelList(getUserService().getAlleUser()));

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setSearchObjUser(HibernateSearchObject<SecUser> searchObjUser) {
		this.searchObjUser = searchObjUser;
	}

	public HibernateSearchObject<SecUser> getSearchObjUser() {
		return searchObjUser;
	}

}
