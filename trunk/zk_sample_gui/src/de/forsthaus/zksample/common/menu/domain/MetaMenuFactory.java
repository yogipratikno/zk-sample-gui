package de.forsthaus.zksample.common.menu.domain;

import java.lang.ref.SoftReference;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetaMenuFactory {

	static private SoftReference<RootMenuDomain> referenceRootMenuDomain = new SoftReference<RootMenuDomain>(null);
	static String menuXMLRootPath = "/de/forsthaus/zksample/common/menu/domain/";

	static public RootMenuDomain getRootMenuDomain() {
		RootMenuDomain rootMenuDomain = referenceRootMenuDomain.get();
		if (rootMenuDomain == null) {
			try {
				Unmarshaller unmarshaller = JAXBContext.newInstance(RootMenuDomain.class).createUnmarshaller();
				rootMenuDomain = (RootMenuDomain) unmarshaller.unmarshal(MetaMenuFactory.class
						.getResource(menuXMLRootPath + "mainmenu.xml"));
				referenceRootMenuDomain = new SoftReference<RootMenuDomain>(rootMenuDomain);
				final Log LOGGER = LogFactory.getLog(MetaMenuFactory.class);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Menü-Metamodel geladen");
				}

			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
		return rootMenuDomain;
	}
}
