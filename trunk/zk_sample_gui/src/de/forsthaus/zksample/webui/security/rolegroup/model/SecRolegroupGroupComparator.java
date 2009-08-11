package de.forsthaus.zksample.webui.security.rolegroup.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecGroup;

public class SecRolegroupGroupComparator implements Comparator<SecGroup>, Serializable {

	private static final long serialVersionUID = -5013590638298084706L;

	public enum FieldsEnum {
		GROUP_NAME, // group short name
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecRolegroupGroupComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecGroup o1, SecGroup o2) {

		final int v;

		switch (columnIndex) {
		case GROUP_NAME:
			v = o1.getGrpShortdescription().compareTo(o2.getGrpShortdescription());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}