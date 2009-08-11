package de.forsthaus.zksample.webui.security.role.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecRole;

public class SecRoleComparator implements Comparator<SecRole>, Serializable {

	private static final long serialVersionUID = 747459796070371450L;

	public enum FieldsEnum {
		ROLE_SHORT_DESCRIPTION; // role short name
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecRoleComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecRole o1, SecRole o2) {

		final int v;

		switch (columnIndex) {
		case ROLE_SHORT_DESCRIPTION:
			v = o1.getRolShortdescription().compareTo(o2.getRolShortdescription());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}