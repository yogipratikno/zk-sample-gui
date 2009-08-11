package de.forsthaus.zksample.webui.reports.util;

import java.io.Serializable;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Window;

public class JRreportWindow extends Window implements Serializable {

	private static final long serialVersionUID = -5587316458377274805L;
	private transient final static Logger logger = Logger.getLogger(JRreportWindow.class);

	private transient Jasperreport report;

	/* The parent that calls the report */
	private transient Component parent;

	/* if true, shows the ReportWindow in ModalMode */
	private transient boolean modal;

	/* report params like subreports, title, author etc. */
	private transient Map reportParams;

	/* reportname with whole path must ends with .jasper (compiled) */
	private transient String reportPathName;

	/* JasperReports Datasource */
	private transient JRDataSource ds;

	/* 'pdf', 'xml', .... */
	private transient String type;

	/**
	 * Constructor.<br>
	 * <br>
	 * Creates a report window container.<br>
	 * 
	 * @param parent
	 * @param modal
	 * @param reportParams
	 * @param reportPathName
	 * @param ds
	 * @param type
	 */
	public JRreportWindow(Component parent, boolean modal, Map reportParams, String reportPathName, JRDataSource ds, String type) {
		super();
		this.parent = parent;
		this.modal = modal;
		this.reportParams = reportParams;
		this.reportPathName = reportPathName;
		this.ds = ds;
		this.type = type;

		createReport();
	}

	private void createReport() {

		if ((Boolean) modal == null) {
			modal = true;
		}

		if (reportPathName.isEmpty()) {
			// throw new FileNotFoundException(reportPathName, "kjhkjhkj");
		}

		if (ds == null) {
			// throw new FileNotFoundException(reportPathName, "kjhkjhkj");
		}

		if (type.isEmpty()) {
			type = "pdf";
		}

		report = new Jasperreport();
		report.setSrc(reportPathName);
		report.setParameters(reportParams);
		report.setDatasource(ds);
		report.setType(type);

		// Window win = new Window();
		this.setParent(parent);

		System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwww " + parent.getRoot().toString());

		this.setTitle("JasperReports Sample Report for ZKoss");
		this.setVisible(true);
		this.setMaximizable(true);
		this.setMinimizable(true);
		this.setSizable(true);
		this.setClosable(true);
		this.setHeight("100%");
		this.setWidth("80%");

		this.addEventListener("onClose", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				closeReportWindow();
			}
		});

		this.appendChild(report);

		if (modal == true) {
			try {
				this.doModal();
			} catch (SuspendNotAllowedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * We must clear something to prevent errors or problems <br>
	 * by opening the report several times. <br>
	 */
	private void closeReportWindow() {
		if (logger.isDebugEnabled()) {
			logger.debug("detach Report and close ReportWindow");
		}
		report.removeEventListener("onClose", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				closeReportWindow();
			}
		});

		report.detach();
		this.onClose();

	}
}
