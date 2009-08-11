package de.forsthaus.zksample.webui.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.zkoss.spring.SpringUtil;

import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.AuftragService;
import de.forsthaus.backend.service.ReportService;

public class TestReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient AuftragService auftragService;
	private transient ReportService reportService;

	@SuppressWarnings("unchecked")
	public JRDataSource getBeanCollectionByAuftrag(Auftrag auf) {

		// init all needed lists
		ArrayList<Kunde> kundeList = new ArrayList<Kunde>();
		Set<Auftragposition> auftragpositionSet = new HashSet<Auftragposition>();
		Set<Auftrag> auftragSet = new HashSet<Auftrag>();

		Auftrag auftrag = auf;

		// get the customer for this order
		Kunde kunde = getAuftragService().getKundeForAuftrag(auf);

		/* Liste mit Daten füllen */
		auftragpositionSet.addAll((java.util.Collection) getAuftragService().getAuftragsPositionenByAuftrag(auftrag));

		// wire all together
		auftrag.setAuftragpositions(auftragpositionSet);
		auftragSet.add(auftrag);
		kunde.setAuftrags(auftragSet);
		kundeList.add(kunde);

		return new JRBeanCollectionDataSource((java.util.Collection) kundeList);
	}

	@SuppressWarnings("unchecked")
	public static JRDataSource testBeanCollectionDatasource() {

		java.util.Vector collection = new java.util.Vector();

		ArrayList<Kunde> kundeList = new ArrayList<Kunde>();

		// Auftrag auftrag = getAuftragService().getAuftragById(40);

		Branche branche;
		Kunde kunde;
		Auftrag auftrag;
		Auftragposition auftragposition1;
		Auftragposition auftragposition2;
		Auftragposition auftragposition3;
		Auftragposition auftragposition4;
		Auftragposition auftragposition5;
		Auftragposition auftragposition6;

		auftragposition1 = new Auftragposition();
		auftragposition1.setAupId(50);
		auftragposition1.setAupMenge(new BigDecimal(3.00));
		auftragposition1.setAupEinzelwert(new BigDecimal(10.00));
		auftragposition1.setAupGesamtwert(new BigDecimal(30.00));

		auftragposition2 = new Auftragposition();
		auftragposition2.setAupId(51);
		auftragposition2.setAupMenge(new BigDecimal(6.00));
		auftragposition2.setAupEinzelwert(new BigDecimal(6.00));
		auftragposition2.setAupGesamtwert(new BigDecimal(36.00));

		auftragposition3 = new Auftragposition();
		auftragposition3.setAupId(52);
		auftragposition3.setAupMenge(new BigDecimal(12.00));
		auftragposition3.setAupEinzelwert(new BigDecimal(12.00));
		auftragposition3.setAupGesamtwert(new BigDecimal(144.00));

		auftragposition4 = new Auftragposition();
		auftragposition4.setAupId(53);
		auftragposition4.setAupMenge(new BigDecimal(20.00));
		auftragposition4.setAupEinzelwert(new BigDecimal(80.00));
		auftragposition4.setAupGesamtwert(new BigDecimal(160.00));

		auftragposition5 = new Auftragposition();
		auftragposition5.setAupId(54);
		auftragposition5.setAupMenge(new BigDecimal(7.00));
		auftragposition5.setAupEinzelwert(new BigDecimal(12.00));
		auftragposition5.setAupGesamtwert(new BigDecimal(84.00));

		auftragposition6 = new Auftragposition();
		auftragposition6.setAupId(55);
		auftragposition6.setAupMenge(new BigDecimal(60.00));
		auftragposition6.setAupEinzelwert(new BigDecimal(6.00));
		auftragposition6.setAupGesamtwert(new BigDecimal(360.00));

		auftrag = new Auftrag();
		auftrag.setAufId(40);
		auftrag.setAufBezeichnung("Test Auftrag");
		auftrag.setAufNr("AUF4711");

		kunde = new Kunde();
		kunde.setKunId(20);
		kunde.setKunNr("CU-20");
		kunde.setKunMatchcode("MÜLLER");
		kunde.setKunName1("Elektr Müller GmbH");
		kunde.setKunName2("Elektroinstallationen");
		kunde.setKunOrt("Freiburg");

		Set<Auftragposition> auftragpositionSet = new HashSet<Auftragposition>();
		auftragpositionSet.add(auftragposition1);
		auftragpositionSet.add(auftragposition2);
		auftragpositionSet.add(auftragposition3);
		auftragpositionSet.add(auftragposition4);
		auftragpositionSet.add(auftragposition5);
		auftragpositionSet.add(auftragposition6);
		auftrag.setAuftragpositions(auftragpositionSet);

		Set<Auftrag> auftragSet = new HashSet<Auftrag>();
		auftragSet.add(auftrag);
		kunde.setAuftrags(auftragSet);

		branche = new Branche();
		branche.setBraId(200);
		branche.setBraNr("200");
		branche.setBraBezeichnung("Eleketroinstallationen");
		kunde.setBranche(branche);

		// collection.add(kunde);
		// return new JRBeanCollectionDataSource((Collection) collection);

		kundeList.add(kunde);

		return new JRBeanCollectionDataSource((java.util.Collection) kundeList);
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Kunde> testBeanCollection() {

		java.util.Vector collection = new java.util.Vector();

		ArrayList<Kunde> kundeList = new ArrayList<Kunde>();

		// Auftrag auftrag = getAuftragService().getAuftragById(40);
		Branche branche;
		Kunde kunde;
		Auftrag auftrag;
		Auftragposition auftragposition1;
		Auftragposition auftragposition2;
		Auftragposition auftragposition3;
		Auftragposition auftragposition4;
		Auftragposition auftragposition5;
		Auftragposition auftragposition6;

		auftragposition1 = new Auftragposition();
		auftragposition1.setAupId(50);
		auftragposition1.setAupMenge(new BigDecimal(3.00));
		auftragposition1.setAupEinzelwert(new BigDecimal(10.00));
		auftragposition1.setAupGesamtwert(new BigDecimal(30.00));

		auftragposition2 = new Auftragposition();
		auftragposition2.setAupId(51);
		auftragposition2.setAupMenge(new BigDecimal(6.00));
		auftragposition2.setAupEinzelwert(new BigDecimal(6.00));
		auftragposition2.setAupGesamtwert(new BigDecimal(36.00));

		auftragposition3 = new Auftragposition();
		auftragposition3.setAupId(52);
		auftragposition3.setAupMenge(new BigDecimal(12.00));
		auftragposition3.setAupEinzelwert(new BigDecimal(12.00));
		auftragposition3.setAupGesamtwert(new BigDecimal(144.00));

		auftragposition4 = new Auftragposition();
		auftragposition4.setAupId(53);
		auftragposition4.setAupMenge(new BigDecimal(20.00));
		auftragposition4.setAupEinzelwert(new BigDecimal(80.00));
		auftragposition4.setAupGesamtwert(new BigDecimal(160.00));

		auftragposition5 = new Auftragposition();
		auftragposition5.setAupId(54);
		auftragposition5.setAupMenge(new BigDecimal(7.00));
		auftragposition5.setAupEinzelwert(new BigDecimal(12.00));
		auftragposition5.setAupGesamtwert(new BigDecimal(84.00));

		auftragposition6 = new Auftragposition();
		auftragposition6.setAupId(55);
		auftragposition6.setAupMenge(new BigDecimal(60.00));
		auftragposition6.setAupEinzelwert(new BigDecimal(6.00));
		auftragposition6.setAupGesamtwert(new BigDecimal(360.00));

		auftrag = new Auftrag();
		auftrag.setAufId(40);
		auftrag.setAufBezeichnung("Test Auftrag");
		auftrag.setAufNr("AUF4711");

		kunde = new Kunde();
		kunde.setKunId(20);
		kunde.setKunNr("CU-20");
		kunde.setKunMatchcode("MÜLLER");
		kunde.setKunName1("Elektr Müller GmbH");
		kunde.setKunName2("Elektroinstallationen");
		kunde.setKunOrt("Freiburg");

		Set<Auftragposition> auftragpositionSet = new HashSet<Auftragposition>();
		auftragpositionSet.add(auftragposition1);
		auftragpositionSet.add(auftragposition2);
		auftragpositionSet.add(auftragposition3);
		auftragpositionSet.add(auftragposition4);
		auftragpositionSet.add(auftragposition5);
		auftragpositionSet.add(auftragposition6);
		auftrag.setAuftragpositions(auftragpositionSet);

		Set<Auftrag> auftragSet = new HashSet<Auftrag>();
		auftragSet.add(auftrag);
		kunde.setAuftrags(auftragSet);

		branche = new Branche();
		branche.setBraId(200);
		branche.setBraNr("200");
		branche.setBraBezeichnung("Eleketroinstallationen");
		kunde.setBranche(branche);

		// collection.add(kunde);
		// return new JRBeanCollectionDataSource((Collection) collection);

		kundeList.add(kunde);

		return kundeList;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuftragService getAuftragService() {
		if (auftragService == null) {
			auftragService = (AuftragService) SpringUtil.getBean("auftragService");
		}
		return auftragService;
	}

	public ReportService getReportService() {
		if (reportService == null) {
			reportService = (ReportService) SpringUtil.getBean("reportService");
		}
		return reportService;
	}

}
