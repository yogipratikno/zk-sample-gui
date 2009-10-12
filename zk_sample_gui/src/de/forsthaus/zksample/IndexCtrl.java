package de.forsthaus.zksample;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.security.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.West;
import org.zkoss.zul.Column;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import de.forsthaus.zksample.webui.util.BaseCtrl;

public class IndexCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -3407055074703929527L;
	private transient final static Logger logger = Logger.getLogger(IndexCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window outerIndexWindow; // autowired
	protected transient Menubar mainMenuBar; // autowired

	protected transient Column statusBarAppVersion; // autowired
	protected transient Column statusBarColUser; // autowired

	public IndexCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}

		/* Create the singleton Workspace for the Application */
		/* CLUSTERING ???????????? */
		// ApplicationWorkspace.getInstance();
	}

	/**
	 * Gets the current desktop height and width and <br>
	 * stores it in the UserWorkspace properties. <br>
	 * We use these values for calculating the count of rows in the listboxes. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClientInfo(ClientInfoEvent event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("Current desktop height :" + event.getDesktopHeight());
			logger.debug("Current desktop width  :" + event.getDesktopWidth());
		}

		UserWorkspace.getInstance().setCurrentDesktopHeight(event.getDesktopHeight());
		UserWorkspace.getInstance().setCurrentDesktopWidth(event.getDesktopWidth());

	}

	public void onCreate$outerIndexWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(outerIndexWindow);

		mainMenuBar.setVisible(false);

		createMainTreeMenu();

		doShowVersion();

		doShowUser();

	}

	private void createMainTreeMenu() {

		// get an instance of the borderlayout defined in the index.zul-file
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");

		// get an instance of the searched west layout area
		West west = bl.getWest();
		west.setFlex(true);
		// clear the center child comps
		west.getChildren().clear();

		// create the components from the mainmenu.zul-file and put
		// it in the west layout area
		Executions.createComponents("/WEB-INF/pages/mainmenu.zul", west, null);
	}

	public void showWelcomePage() throws InterruptedException {
		// get an instance of the borderlayout defined in the zul-file
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched CENTER layout area
		Center center = bl.getCenter();

		center.setFlex(true);

		// clear the center child comps
		center.getChildren().clear();
		// call the zul-file and put it in the center layout area
		Executions.createComponents("/WEB-INF/pages/welcome.zul", center, null);
	}

	private void doShowVersion() {

		statusBarAppVersion.setLabel(Executions.getCurrent().getDesktop().getWebApp().getBuild());

	}

	public void doShowUser() {

		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		statusBarColUser.setLabel(userName);

	}

	public void onClick$btnLogout() throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("-->");
		}

		UserWorkspace.getInstance().doLogout(); // logout.
	}

	public void callMessageBox() throws InterruptedException {
		Messagebox.show("Hello, it's me a MessageBox ", "Info", Messagebox.OK, Messagebox.ERROR);
	}

}
