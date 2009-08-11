package de.forsthaus.zksample.webui.security.group.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecGroup;

public class SecGroupComparator implements Comparator<SecGroup>, Serializable {

	private static final long serialVersionUID = 7637338195485238030L;

	public enum FieldsEnum {
		GROUP_SHORT_DESCRIPTION; // GROUP short name
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecGroupComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecGroup o1, SecGroup o2) {

		final int v;

		switch (columnIndex) {
		case GROUP_SHORT_DESCRIPTION:
			v = o1.getGrpShortdescription().compareTo(o2.getGrpShortdescription());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}