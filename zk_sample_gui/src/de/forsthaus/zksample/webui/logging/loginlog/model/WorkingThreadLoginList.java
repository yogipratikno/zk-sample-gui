package de.forsthaus.zksample.webui.logging.loginlog.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Listbox;

import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.zksample.webui.logging.loginlog.SecLoginlogListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Thread for demonstrate the serverPush. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 07/24/2009: sge changes for clustering
 * 
 */
public class WorkingThreadLoginList extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(WorkingThreadLoginList.class);

	private transient final Desktop _desktop;
	private transient final Listbox _listBox;
	private transient final LoginLoggingService _service;

	private transient final SecLoginlogListCtrl _ctrl;

	private transient int refreshTime = 3000;
	private transient boolean _ceased;

	private transient int i = 0;

	/**
	 * Constructor. <br>
	 * 
	 * @param listBox
	 * @param service
	 *            ServiceDAO
	 */
	public WorkingThreadLoginList(SecLoginlogListCtrl ctrl, Listbox listBox, LoginLoggingService service) {
		_desktop = listBox.getDesktop();
		_listBox = listBox;
		_service = service;
		_ctrl = ctrl;
	}

	public void run() {

		if (!_desktop.isServerPushEnabled()) {
			_desktop.enableServerPush(true);
		}

		/**
		 * We must check if the window is already seen or if the user has
		 * changed the page/close the browser and don't stops the thread.
		 */
		if (_listBox == null) {
			logger.debug("The window for showing the list is no longer exist! ");
			this.interrupt();
		}

		try {

			while (!_ceased) {

				Executions.activate(_desktop);

				try {
					i = i + 1;

					if (i % 2 == 0) {
						updateList(); // do something
					} else {
						updateList2(); // do something others
					}
				} finally {
					Executions.deactivate(_desktop);
				}
				Threads.sleep(refreshTime); // update each xx seconds
			}

		} catch (DesktopUnavailableException e) {
			logger.debug(e);
			Executions.deactivate(_desktop);
			this.setDone();

		} catch (InterruptedException e) {
			logger.debug(e);
			this.setDone();
		} finally {
			if (_desktop.isServerPushEnabled()) {
				_desktop.enableServerPush(false);
				Executions.deactivate(_desktop);
			}
		}

	}

	public void updateList() {
		// call a methode for retrieving the logins
		_ctrl.serverPushList1();
	}

	public void updateList2() {
		// call an other methode for retrieving the logins
		_ctrl.serverPushList2();
	}

	public void setDone() {
		_ceased = true;
	}

}
