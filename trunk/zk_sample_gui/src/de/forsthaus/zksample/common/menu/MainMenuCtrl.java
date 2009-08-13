package de.forsthaus.zksample.common.menu;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import de.forsthaus.zksample.common.menu.sub.AdministrationMenuTree;
import de.forsthaus.zksample.common.menu.sub.HomeMenuTree;
import de.forsthaus.zksample.common.menu.sub.MainDataMenuTree;
import de.forsthaus.zksample.common.menu.sub.OfficeDataMenuTree;
import de.forsthaus.zksample.common.menu.util.ZkossMenuUtil;
import de.forsthaus.zksample.webui.util.BaseCtrl;

/**
 * MainMenu Controller
 * -------------------
 * 
 * Build the menu with the MenuTree + MenuItem classes.
 * 
 * All menuItems calls that are used in the application must be fixed 
 * coded in the doMenuItem() methode.
 * 
 * By creating the menuItems we give them the used id's from the doMenuItem() methode. 
 * 
 * The admin can create/modify  the usergroups/usergoupsRights/usergroupMembers
 * for the users with the given MenuCategories/MenuItems.  
 * 
 * 
 * 		mt = new MenuTree(this, "OfficeData", "Office Data", true);
 *		mt.addMenuItem("menuOfficeData_Customers", "Customers");
 *		mt.addMenuItem("menuOfficeData_Orders", "Orders");
 *		getMainMenuWindow().appendChild(mt);
 *
 * @author sge
 * @changes bj create the menu by java. 
 *
 */

public class MainMenuCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -909795057747345551L;
	private transient final static Logger logger = Logger.getLogger(MainMenuCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private transient Window mainMenuWindow; // autowire

	/**
	 * Creates the mainMenu. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void createMenu() throws InterruptedException {
		Div div = new Div();
		div.setWidth("100%");
		div.setHeight("100%");
		div.setStyle("padding:5px");
		div.setParent(getMainMenuWindow());

		div.appendChild(createSeparator(false));
		div.appendChild(createSeparator(false));
		div.appendChild(createSeparator(false));

		Hbox hbox = new Hbox();
		div.appendChild(hbox);
		Toolbarbutton toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuExpandAll");
		toolbarbutton.setImage("/images/icons/folder_open_16x16.gif");
		toolbarbutton.setTooltiptext(Labels.getLabel("btnFolderExpand.tooltiptext"));
		toolbarbutton.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuExpandAll(event);
			}
		});

		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuCollapseAll");

		toolbarbutton.setImage("/images/icons/folder_closed2_16x16.gif");
		toolbarbutton.setTooltiptext(Labels.getLabel("btnFolderCollapse.tooltiptext"));
		toolbarbutton.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuCollapseAll(event);
			}
		});

		Separator separator = createSeparator(false);
		separator.setWidth("97%");
		separator.setBar(true);
		div.appendChild(separator);

		Tree tree = new Tree();
		div.appendChild(tree);

		tree.setZclass("z-dottree");
		tree.setStyle("border: none");

		Treechildren treechildren = new Treechildren();
		tree.appendChild(treechildren);

		ZkossMenuUtil menuUtil = new ZkossMenuUtil(treechildren);

		new HomeMenuTree(menuUtil);
		new OfficeDataMenuTree(menuUtil);
		new MainDataMenuTree(menuUtil);
		new AdministrationMenuTree(menuUtil);

		// Guestbook
		Separator sep1 = new Separator();
		sep1.setWidth("97%");
		sep1.setBar(false);
		sep1.setParent(div);

		Separator sep2 = new Separator();
		sep2.setWidth("97%");
		sep2.setBar(true);
		sep2.setParent(div);

		Separator sep3 = new Separator();
		sep3.setWidth("97%");
		sep3.setBar(false);
		sep3.setParent(div);

		Button btn = new Button("ZK Guestbook");
		btn.setParent(div);
		btn.addEventListener("onClick", new GuestBookListener());

		/* as standard, call the welcome page */
		showPage("/WEB-INF/pages/welcome.zul");

		/** Helper: Shows the whole menu tree in console **/
		// System.out.println(menuUtil.getZulBaum(this));
	}

	/**
	 * Creates a seperator. <br>
	 * 
	 * @param withBar
	 * <br>
	 *            true=with Bar <br>
	 *            false = without Bar <br>
	 * @return
	 */
	private static Separator createSeparator(boolean withBar) {

		Separator sep = new Separator();
		sep.setBar(withBar);

		return sep;
	}

	public final class GuestBookListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			showPage("/WEB-INF/pages/guestbook/guestBookList.zul");
		}
	}

	private void showPage(String zulFilePathName) throws InterruptedException {
		try {
			/* get an instance of the borderlayout defined in the index.zul-file */
			Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			/* get an instance of the searched CENTER layout area */
			Center center = bl.getCenter();
			center.setFlex(true);
			/* clear the center child comps */
			center.getChildren().clear();
			/*
			 * create the page and put it in the center layout area
			 */
			Executions.createComponents(zulFilePathName, center, null);

			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + zulFilePathName);
			}
		} catch (Exception e) {
			Messagebox.show(e.toString());
		}
	}

	public Window getMainMenuWindow() {
		return mainMenuWindow;
	}

	public void setMainMenuWindow(Window mainMenuWindow) {
		this.mainMenuWindow = mainMenuWindow;
	}

	public void onCreate$mainMenuWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(getMainMenuWindow(), event); // wire vars

		createMenu();
	}

	public void onClick$btnMainMenuExpandAll(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		fooBarMirFaelltKeinNameEin(getMainMenuWindow(), true);
	}

	public void onClick$btnMainMenuCollapseAll(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		fooBarMirFaelltKeinNameEin(getMainMenuWindow(), false);
	}

	private void fooBarMirFaelltKeinNameEin(Component component, boolean aufklappen) {
		if (component instanceof Treeitem) {
			Treeitem treeitem = (Treeitem) component;
			treeitem.setOpen(aufklappen);
		}
		Collection<?> com = component.getChildren();
		if (com != null) {
			for (Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
				fooBarMirFaelltKeinNameEin((Component) iterator.next(), aufklappen);

			}
		}
	}
}