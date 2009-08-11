package de.forsthaus.zksample.webui.security.right.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecRight;

public class SecRightComparator implements Comparator<SecRight>, Serializable {

	private static final long serialVersionUID = 5960853839436367587L;

	public enum FieldsEnum {
		RIGHT_NAME, // right name
		RIGHT_TYPID; // right type
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecRightComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecRight o1, SecRight o2) {

		final int v;

		switch (columnIndex) {
		case RIGHT_NAME:
			v = o1.getRigName().compareTo(o2.getRigName());
			break;
		case RIGHT_TYPID:
			v = o1.getRigType().compareTo(o2.getRigType());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}