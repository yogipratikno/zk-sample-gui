package de.forsthaus.zksample;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Hr;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zkmax.zul.Tablelayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Window;

import com.trg.search.SearchResult;

import de.forsthaus.backend.model.Artikel;
import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.GuestBook;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.model.SecGroupright;
import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.model.SecRolegroup;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.model.SecUserrole;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.KundeService;
import de.forsthaus.backend.service.TestService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.sampledata.statistic.Statistic;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the unsecured index.zul file<br>
 * as the entry point to the application.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * Here we show the count of all created records of demo data. Additionally we
 * can inserts customer records for better demonstrating the 'feeling' for the
 * speed by working with paged data.<br>
 * <br>
 * The inserts need a little bit of time because we have an spring aspect
 * declared for the transaction handling. So here in this case every inserts
 * have a single commit.<br>
 * <br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes 24.07.2009/sge changes for clustering.<br>
 *          26.07.2009/sge design modifications.<br>
 * 
 */
public class InitApplication extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(InitApplication.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window startWindow; // autowired
	protected transient North bl_north; // autowire
	protected transient South bl_south; // autowire
	protected transient Center bl_center; // autowire

	private transient Tablelayout tableLayout;
	private transient Tablechildren tableChildrenRecords;
	private transient Tablechildren tableChildrenStatistic;
	private transient Tablechildren tableChildrenButtons;

	private transient Label label_RecordCountCustomer;

	// ServiceDAOs / Domain Classes
	private transient KundeService kundeService;
	private transient BrancheService brancheService;

	/**
	 * Construtor
	 */
	public InitApplication() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super() ");
		}
	}

	public void onCreate$startWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		doOnCreateCommon(startWindow); // do the autowire

		createMainGrid();
		showDemoData();
		// Monitor the application
		showStatistic();

		showButtons();

	}

	private void createMainGrid() {

		Div div = new Div();
		div.setParent(bl_center);

		Hr hr = new Hr();
		hr.setParent(div);

		/*
		 * Borderlayout around the grid for make it scrollable to see all table
		 * records
		 */
		Borderlayout bl = new Borderlayout();
		bl.setParent(div);
		Center ct = new Center();
		ct.setAutoscroll(true);
		ct.setStyle("background-color: #EBEBEB");
		ct.setBorder("none");
		ct.setFlex(true);
		ct.setParent(bl);
		Div divCt = new Div();
		divCt.setParent(ct);

		tableLayout = new Tablelayout();
		tableLayout.setColumns(3);
		tableLayout.setWidth("900px");
		tableLayout.setParent(divCt);

		tableChildrenRecords = new Tablechildren();
		tableChildrenRecords.setRowspan(1);
		tableChildrenRecords.setWidth("20%");
		tableChildrenRecords.setParent(tableLayout);

		tableChildrenStatistic = new Tablechildren();
		tableChildrenStatistic.setRowspan(1);
		tableChildrenStatistic.setWidth("50%");
		tableChildrenStatistic.setParent(tableLayout);

		tableChildrenButtons = new Tablechildren();
		tableChildrenButtons.setRowspan(1);
		tableChildrenButtons.setWidth("30%");
		tableChildrenButtons.setParent(tableLayout);

		Separator sep = new Separator();
		sep.setParent(divCt);
		Separator sep2 = new Separator();
		sep2.setParent(divCt);

		Div divFooter = new Div();
		divFooter.setAlign("center");
		divFooter.setParent(bl_south);

		Hr hr2 = new Hr();
		hr2.setParent(divFooter);

		Label footerLabel = new Label();
		footerLabel.setValue(" Help to prevent the global warming by writing cool software.");
		footerLabel.setStyle("align:center; padding-top:0px; font-family:Verdana; black: white; font-size: 0.6em");
		footerLabel.setParent(divFooter);

	}

	private void showDemoData() {

		Panel panel = new Panel();
		panel.setTitle("Demo-Data stored in H2 Database");
		// panel.setWidth("100%");
		panel.setWidth("300px");
		panel.setHeight("100%");
		panel.setBorder("none");
		panel.setStyle("align:left; color:red");
		panel.setParent(tableChildrenRecords);

		Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setParent(panelchildren);

		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		Column column1 = new Column();
		column1.setWidth("180px");
		column1.setLabel("Table");
		column1.setParent(columns);
		Column column2 = new Column();
		column2.setWidth("120px");
		column2.setLabel("records");
		column2.setParent(columns);

		Rows rows = new Rows();
		rows.setParent(grid);

		TestService ts = (TestService) SpringUtil.getBean("testService");
		SearchResult sr = null;
		int i = 0;

		HibernateSearchObject<Kunde> kunde = new HibernateSearchObject(Kunde.class);
		sr = ts.getSRBySearchObject(kunde, 1, 15);
		i = sr.getTotalCount();
		Row row;
		Label label_TableName;
		// Label label_RecordCountCustomer;
		row = new Row();

		label_TableName = new Label("Customer");
		label_TableName.setParent(row);
		label_RecordCountCustomer = new Label(String.valueOf(i));
		label_RecordCountCustomer.setParent(row);
		row.setParent(rows);

		HibernateSearchObject<Branche> branche = new HibernateSearchObject(Branche.class);
		sr = ts.getSRBySearchObject(branche, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "Branch", i);

		HibernateSearchObject<Artikel> artikel = new HibernateSearchObject(Artikel.class);
		sr = ts.getSRBySearchObject(artikel, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "Article", i);

		HibernateSearchObject<Auftrag> auftrag = new HibernateSearchObject(Auftrag.class);
		sr = ts.getSRBySearchObject(auftrag, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "Order", i);

		HibernateSearchObject<Auftragposition> auftragposition = new HibernateSearchObject(Auftragposition.class);
		sr = ts.getSRBySearchObject(auftragposition, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "Orderposition", i);

		HibernateSearchObject<GuestBook> guestBook = new HibernateSearchObject(GuestBook.class);
		sr = ts.getSRBySearchObject(guestBook, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "GuestBook", i);

		HibernateSearchObject<SecGroup> secgroup = new HibernateSearchObject(SecGroup.class);
		sr = ts.getSRBySearchObject(secgroup, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecGroup", i);

		HibernateSearchObject<SecGroupright> secgroupright = new HibernateSearchObject(SecGroupright.class);
		sr = ts.getSRBySearchObject(secgroupright, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecGroupright", i);

		HibernateSearchObject<SecRight> secright = new HibernateSearchObject(SecRight.class);
		sr = ts.getSRBySearchObject(secright, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecRight", i);

		HibernateSearchObject<SecRole> secRole = new HibernateSearchObject(SecRole.class);
		sr = ts.getSRBySearchObject(secRole, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecRole", i);

		HibernateSearchObject<SecRolegroup> secRolegroup = new HibernateSearchObject(SecRolegroup.class);
		sr = ts.getSRBySearchObject(secRolegroup, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecRolegroup", i);

		HibernateSearchObject<SecUser> secUser = new HibernateSearchObject(SecUser.class);
		sr = ts.getSRBySearchObject(secUser, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecUser", i);

		HibernateSearchObject<SecUserrole> secUserrole = new HibernateSearchObject(SecUserrole.class);
		sr = ts.getSRBySearchObject(secUserrole, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecUserrole", i);

		HibernateSearchObject<SecLoginlog> secLoginlog = new HibernateSearchObject(SecLoginlog.class);
		sr = ts.getSRBySearchObject(secLoginlog, 1, 15);
		i = sr.getTotalCount();
		addNewRow(rows, "SecLoginlog", i);

	}

	private void showStatistic() {

		// These Statistic Class is activated in the zk.xml
		Statistic stat = de.forsthaus.sampledata.statistic.Statistic.getStatistic();

		Panel panel = new Panel();
		panel.setTitle("Application Statistic");
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.setBorder("none");
		panel.setStyle("align:left; color:red");
		panel.setParent(tableChildrenStatistic);

		Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setParent(panelchildren);

		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		Column column1 = new Column();
		column1.setWidth("300px");
		column1.setLabel("Subject");
		column1.setParent(columns);
		Column column2 = new Column();
		column2.setWidth("200px");
		column2.setLabel("value");
		column2.setParent(columns);

		Rows rows = new Rows();
		rows.setParent(grid);

		addNewRow(rows, "Application Start-Time", String.valueOf(new Date(stat.getStartTime())));

		// String str = getHoursAndMinutes(stat.getRuningHours());
		// addNewRow(rows, "Application runing hours", String.valueOf(str));
		Double d = stat.getRuningHours();
		addNewRow(rows, "Application runing hours", String.valueOf(d));

		addNewRow(rows, "Count of active Desktops", String.valueOf(stat.getActiveDesktopCount()));
		addNewRow(rows, "Count of active Sessions", String.valueOf(stat.getActiveSessionCount()));
		addNewRow(rows, "Count of active Updates", String.valueOf(stat.getActiveUpdateCount()));

		addNewRow(rows, "Average Count of active Desktops/hour", String.valueOf(stat.getAverageDesktopCount()));
		addNewRow(rows, "Average Count of active Sessions/hour", String.valueOf(stat.getAverageSessionCount()));
		addNewRow(rows, "Average Count of active Updates/hour", String.valueOf(stat.getAverageUpdateCount()));

		addNewRow(rows, "Count of total Desktops since start", String.valueOf(stat.getTotalDesktopCount()));
		addNewRow(rows, "Count of total Sessions since start", String.valueOf(stat.getTotalSessionCount()));
		addNewRow(rows, "Count of total Updates since start", String.valueOf(stat.getTotalUpdateCount()));

	}

	private void showButtons() {

		Panel panel = new Panel();
		panel.setTitle("Customer records");
		panel.setWidth("100%");
		panel.setHeight("100%");
		panel.setBorder("none");
		panel.setStyle("align:left; color:red");
		panel.setParent(tableChildrenStatistic);

		Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		Div div = new Div();
		div.setWidth("100%");
		div.setHeight("100%");
		div.setStyle("padding: 10px;");
		div.setParent(panelchildren);

		/* 1000. Button */
		Div divBtn1 = new Div();
		divBtn1.setStyle("align: center");
		divBtn1.setParent(div);

		Button btn = new Button();
		btn.setLabel("insert 1000");
		btn.setImage("/images/icons/database.gif");
		btn.setTooltiptext("Insert 1.000 randomly created customer records");
		btn.setParent(divBtn1);

		btn.addEventListener("onClick", new OnClick1000Eventlistener());

		/* Separator */
		Separator sep1 = new Separator();
		sep1.setParent(div);
		Separator sep2 = new Separator();
		sep2.setParent(div);

		/* 10.000 Button */
		Div divBtn2 = new Div();
		divBtn2.setStyle("align: center;");
		divBtn2.setParent(div);

		Button btn2 = new Button();
		btn2.setLabel("insert 10.000");
		btn2.setImage("/images/icons/database.gif");
		btn2.setTooltiptext("Insert 10.000 randomly created customer records");
		btn2.setParent(divBtn2);

		btn2.addEventListener("onClick", new OnClick10000Eventlistener());

	}

	private void addNewRow(Rows rowParent, String tableName, int countRecords) {
		Row row;
		Label label_TableName;
		Label label_RecordCount;
		row = new Row();
		label_TableName = new Label(tableName);
		label_TableName.setParent(row);
		label_RecordCount = new Label(String.valueOf(countRecords));
		label_RecordCount.setParent(row);
		row.setParent(rowParent);
	}

	private void addNewRow(Rows rowParent, String tableName, Object value) {
		Row row;
		Label label_TableName;
		Label label_RecordCount;
		row = new Row();
		label_TableName = new Label(tableName);
		label_TableName.setParent(row);
		label_RecordCount = new Label(String.valueOf(value));
		label_RecordCount.setParent(row);
		row.setParent(rowParent);
	}

	/**
	 * Get the actual date/time on server. <br>
	 * 
	 * @return String of date/time
	 */
	private String getHoursAndMinutes(Double value) {
		DateFormat dateFormat = new SimpleDateFormat("HHHHH:mm:ss");
		Date date = new Date();
		// java.util.Calendar
		return dateFormat.format(date);
	}

	public final class OnClick1000Eventlistener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			create1000Customer();
		}
	}

	public void create1000Customer() throws InterruptedException {

		/* check if over 50.000 records in DB */
		if (getTotalCountRecordsForCustomer() > 50000) {

			String message = Labels.getLabel("Demo.not_more_than_50000_records");
			String title = Labels.getLabel("message_Information");
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
			return;
		}

		Branche branche = getBrancheService().getBrancheById(1000);

		int countRecords = 1000;

		for (int j = 0; j < countRecords; j++) {
			Kunde kunde = getKundeService().getNewKunde();

			kunde.setKunName1(ObjectMaschine.getRandomM());
			kunde.setKunName2(ObjectMaschine.getRandomNach());
			kunde.setKunMatchcode(kunde.getKunName2().toUpperCase());
			kunde.setKunOrt(ObjectMaschine.getRandomOrt());
			kunde.setBranche(branche);
			kunde.setKunMahnsperre(false);

			getKundeService().saveOrUpdate(kunde);
		}

		TestService ts = (TestService) SpringUtil.getBean("testService");
		SearchResult sr = null;
		int i = 0;

		HibernateSearchObject<Kunde> kunde = new HibernateSearchObject(Kunde.class);
		sr = ts.getSRBySearchObject(kunde, 1, 15);
		i = sr.getTotalCount();

		label_RecordCountCustomer.setValue(String.valueOf(i));
	}

	private int getTotalCountRecordsForCustomer() {

		TestService ts = (TestService) SpringUtil.getBean("testService");
		SearchResult sr = null;
		int i = 0;

		HibernateSearchObject<Kunde> kunde = new HibernateSearchObject(Kunde.class);
		sr = ts.getSRBySearchObject(kunde, 1, 15);
		i = sr.getTotalCount();

		return i;
	}

	public final class OnClick10000Eventlistener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			create10000Customer();
		}
	}

	public void create10000Customer() throws InterruptedException {

		/* check if over 50.000 records in DB */
		if (getTotalCountRecordsForCustomer() > 50000) {

			String message = Labels.getLabel("Demo.not_more_than_50000_records");
			String title = Labels.getLabel("message_Information");
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
			return;
		}

		Branche branche = getBrancheService().getBrancheById(1000);

		int countRecords = 10000;

		for (int j = 0; j < countRecords; j++) {
			Kunde kunde = getKundeService().getNewKunde();

			kunde.setKunName1(ObjectMaschine.getRandomM());
			kunde.setKunName2(ObjectMaschine.getRandomNach());
			kunde.setKunMatchcode(kunde.getKunName2().toUpperCase());
			kunde.setKunOrt(ObjectMaschine.getRandomOrt());
			kunde.setBranche(branche);
			kunde.setKunMahnsperre(false);

			getKundeService().saveOrUpdate(kunde);
		}

		TestService ts = (TestService) SpringUtil.getBean("testService");
		SearchResult sr = null;
		int i = 0;

		HibernateSearchObject<Kunde> kunde = new HibernateSearchObject(Kunde.class);
		sr = ts.getSRBySearchObject(kunde, 1, 15);
		i = sr.getTotalCount();

		label_RecordCountCustomer.setValue(String.valueOf(i));
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

	public KundeService getKundeService() {
		if (kundeService == null) {
			kundeService = (KundeService) SpringUtil.getBean("kundeService");
			setKundeService(kundeService);
		}
		return kundeService;
	}

	private void setKundeService(KundeService kundeService) {
		this.kundeService = kundeService;
	}

}
