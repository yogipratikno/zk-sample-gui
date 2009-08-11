package de.forsthaus.zksample.webui.security.userrole.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecRole;

public class SecUserroleRoleComparator implements Comparator<SecRole>, Serializable {

	private static final long serialVersionUID = -5013590638298084706L;

	public enum FieldsEnum {
		ROLE_NAME, // role short name
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecUserroleRoleComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecRole o1, SecRole o2) {

		final int v;

		switch (columnIndex) {
		case ROLE_NAME:
			v = o1.getRolShortdescription().compareTo(o2.getRolShortdescription());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}