package de.forsthaus.zksample.webui.reports;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import org.zkoss.zkplus.spring.SpringUtil;

import de.forsthaus.backend.model.Auftrag;
import de.forsthaus.backend.model.Auftragposition;
import de.forsthaus.backend.model.Kunde;
import de.forsthaus.backend.service.AuftragService;
import de.forsthaus.backend.service.ReportService;

public class AuftragDetails_Report implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient static AuftragService auftragService;
	private transient static ReportService reportService;

	// This is the method to call to get the datasource.
	// The method must be static.....
	public JRDataSource createDatasource() {
		javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(4, 2);
		/*
		 * SampleBean person = new SampleBean(); person.setFirstName("Giulio");
		 * person.setLastName("Toffoli");
		 * person.setEmail("gt@businesslogic.it"); tm.setValueAt(person, 0, 0);
		 * tm.setValueAt("Test value row 1 col 1", 0, 1);
		 * 
		 * person = new SampleBean(); person.setFirstName("Teodor");
		 * person.setLastName("Danciu"); person.setEmail("teodor@hotmail.com");
		 * tm.setValueAt(person, 1, 0); tm.setValueAt("Test value row 2 col 1",
		 * 1, 1);
		 * 
		 * person = new SampleBean(); person.setFirstName("Mario");
		 * person.setLastName("Rossi"); person.setEmail("mario@rossi.org");
		 * tm.setValueAt(person, 2, 0); tm.setValueAt("Test value row 3 col 1",
		 * 2, 1);
		 * 
		 * person = new SampleBean(); person.setFirstName("Jennifer");
		 * person.setLastName("Lopez"); person.setEmail("lopez@jennifer.com");
		 * tm.setValueAt(person, 3, 0); tm.setValueAt("Test value row 4 col 1",
		 * 3, 1);
		 */

		return new JRTableModelDataSource(tm);
	}

	public JRDataSource testBeanCollectionDatasource() {

		Auftrag auftrag = getAuftragService().getAuftragById(40);

		// JOptionPane.showMessageDialog(null, "Hier");

		return new JRBeanCollectionDataSource(createBeanCollection(auftrag));
	}

	public JRDataSource createBeanCollectionDatasource(Auftrag auftrag) {
		return new JRBeanCollectionDataSource(createBeanCollection(auftrag));
	}

	@SuppressWarnings("unchecked")
	public static Vector createBeanCollection(Auftrag auftrag) {

		java.util.Vector collection = new java.util.Vector();

		Kunde kunde = getAuftragService().getKundeForAuftrag(auftrag);
		List<Auftragposition> auftragpositionList = getAuftragService().getAuftragsPositionenByAuftrag(auftrag);

		// /* Liste mit Daten füllen */
		// List<Auftragposition> result =
		// getAuftragService().getAuftragsPositionenByAuftrag(auftrag);
		// /* DataSource mit der Liste erstellen */
		// JRBeanCollectionDataSource datasource = new
		// JRBeanCollectionDataSource(result);

		Set<Auftragposition> auftragpositionSet = new HashSet<Auftragposition>();
		auftragpositionSet.addAll((Collection) auftragpositionList);
		auftrag.setAuftragpositions(auftragpositionSet);

		Set<Auftrag> auftragSet = new HashSet<Auftrag>();
		auftragSet.add(auftrag);
		kunde.setAuftrags(auftragSet);

		collection.add(kunde);

		// collection.add(auftrag);
		// collection.addAll((Collection) auftragpositionList);
		// collection.add(auftragpositionList);

		// for (Auftragposition auftragposition : auftragpositionList) {
		// collection.add(auftragposition);
		// }

		// // Kunde kund = new Kunde();
		// Kunde kund = getAuftragService().getKundeForAuftrag(auftrag);
		//
		// Set setAufPos = new HashSet();
		// setAufPos.addAll(auftragpositionList);
		// auftrag.setAuftragpositions(setAufPos);
		//
		// Set setAuftrag = new HashSet();
		// setAuftrag.add(auftrag);
		//
		// kund.setAuftrags(setAuftrag);
		//
		// collection.clear();
		// collection.add(kund);

		return collection;
		// return result;

	}

	public static AuftragService getAuftragService() {
		if (auftragService == null) {
			auftragService = (AuftragService) SpringUtil.getBean("auftragService");
		}
		return auftragService;
	}

	public static ReportService getReportService() {
		if (reportService == null) {
			reportService = (ReportService) SpringUtil.getBean("reportService");
		}
		return reportService;
	}

}
/*
 * @SuppressWarnings("unchecked") public static java.util.Collection
 * createBeanCollection() { java.util.Vector collection = new
 * java.util.Vector();
 * 
 * collection.add(new Branche(1, "Holzfällerei")); collection.add(new Branche(2,
 * "Jack")); collection.add(new Branche(3, "Bob")); collection.add(new
 * Branche(4, "Alice")); collection.add(new Branche(5, "Robin"));
 * collection.add(new Branche(6, "Peter")); return collection; / // simulated
 * collection returned from iBATIS List list = new ArrayList(); // Java bean
 * populated with row data by iBATIS AuftragId id = new AuftragId();
 * id.setAufId(60); id.setAufKunFilId(1); id.setAufKunId(20);
 * 
 * KundeId kundeId = new KundeId(); kundeId.setKunFilId(1);
 * kundeId.setKunId(20);
 * 
 * Kunde kunde = new Kunde(); kunde.setId(kundeId);
 * kunde.setKunMatchcode("MÜLLER"); kunde.setKunName1("Müller GmbH");
 * kunde.setKunName2("Elektroinstallationen");
 * kunde.setKunOrt("Elektroinstallationen");
 * 
 * Auftrag auftrag = new Auftrag(); auftrag.setId(id);
 * auftrag.setAufBezeichnung("Testauftrag"); auftrag.setKunde(kunde);
 * 
 * list.add(auftrag);
 * 
 * return list;
 */

// Auftrag auftrag = new Auftrag();
// List<Auftragposition> list =
// getAuftragService().getAuftragsPositionenByAuftrag(auftrag);
// collection.addAll(list);
// return collection;
