package de.forsthaus.zksample.webui.customer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.ChartData;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.ChartService;
import de.forsthaus.zksample.UserWorkspace;
import de.forsthaus.zksample.webui.util.BaseCtrl;
import de.forsthaus.zksample.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the customerChart.zul file. *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * 1. In this controller we get the pressed nuttons for the several<br>
 * charts and fill them with data and shows the result graphical.<br>
 * 
 * @author sge(at)forsthaus(dot)de
 * @changes
 * 
 */
public class CustomerChartCtrl extends BaseCtrl implements Serializable {

	private static final long serialVersionUID = -3464049954099545446L;
	private transient final static Logger logger = Logger.getLogger(CustomerChartCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected transient Window customerChartWindow; // autowire
	protected transient Div div_chartArea;

	// Toolbar Buttons
	protected transient Button button_CustomerChart_PieChart; // autowire
	protected transient Button button_CustomerChart_PieChart3D; // autowire
	protected transient Button button_CustomerChart_RingChart; // autowire
	protected transient Button button_CustomerChart_BarChart; // autowire
	protected transient Button button_CustomerChart_BarChart3D; // autowire
	protected transient Button button_CustomerChart_StackedBar; // autowire
	protected transient Button button_CustomerChart_StackedBar3D; // autowire
	protected transient Button button_CustomerChart_LineBar; // autowire
	protected transient Button button_CustomerChart_LineBar3D; // autowire

	// Button controller for the CRUD buttons
	protected transient Button btnHelp; // autowire

	// ServiceDAOs / Domain Classes
	private transient Kunde kunde;
	private transient ChartService chartService;

	public CustomerChartCtrl() {
		super();

		if (logger.isDebugEnabled()) {
			logger.debug("--> super()");
		}
	}

	public void onCreate$customerChartWindow(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		/* autowire comps and vars */
		doOnCreateCommon(customerChartWindow, event);

		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED params !
		if (args.containsKey("kunde")) {
			kunde = (Kunde) args.get("kunde");
			setKunde(kunde);
		} else {
			setKunde(null);
		}

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		UserWorkspace workspace = UserWorkspace.getInstance();

		// customerChartWindow.setVisible(workspace.isAllowed(
		// "window_BranchesList"));
		//btnHelp.setVisible(workspace.isAllowed("button_CustomerDialog_btnHelp"
		// ));

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

	/**
	 * onClick button PieChart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_PieChart(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createPieChart(title, pieDataset, true, true, true);
			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Pie Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button PieChart 3D. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_PieChart3D(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createPieChart3D(title, pieDataset, true, true, true);
			PiePlot3D plot = (PiePlot3D) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Pie Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Ring Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_RingChart(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createRingChart(title, pieDataset, true, true, true);
			RingPlot plot = (RingPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Ring Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_BarChart(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createBarChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Bar Chart 3D. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_BarChart3D(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createBarChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);
		}
	}

	/**
	 * onClick button Stacked Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_StackedBar(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createStackedBarChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Stacked Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Stacked Bar 3D Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_StackedBar3D(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createStackedBarChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Stacked Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Line Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_LineBar(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), "2009", key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createLineChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Line Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Line Bar 3D Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_LineBar3D(Event event) throws InterruptedException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getKunde().getKunId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), "2009", key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createLineChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(750, 400, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Line Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/*
	 * call the binding new
	 */
	public void doBindNew() {
		binder.loadAll();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	public ChartService getChartService() {
		if (chartService == null) {
			chartService = (ChartService) SpringUtil.getBean("chartService");
		}
		return chartService;
	}
}
