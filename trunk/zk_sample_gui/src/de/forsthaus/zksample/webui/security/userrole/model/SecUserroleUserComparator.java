package de.forsthaus.zksample.webui.security.userrole.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecUser;

public class SecUserroleUserComparator implements Comparator<SecUser>, Serializable {

	private static final long serialVersionUID = -5013590638298084706L;

	public enum FieldsEnum {
		USER_LOGINNAME, // user loginname
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecUserroleUserComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecUser o1, SecUser o2) {

		final int v;

		switch (columnIndex) {
		case USER_LOGINNAME:
			v = o1.getUsrLoginname().compareTo(o2.getUsrLoginname());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}