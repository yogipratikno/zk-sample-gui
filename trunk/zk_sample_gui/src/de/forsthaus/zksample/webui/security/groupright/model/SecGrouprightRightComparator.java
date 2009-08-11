package de.forsthaus.zksample.webui.security.groupright.model;

import java.io.Serializable;
import java.util.Comparator;

import de.forsthaus.backend.model.SecRight;

public class SecGrouprightRightComparator implements Comparator<SecRight>, Serializable {

	private static final long serialVersionUID = -5013590638298084706L;

	public enum FieldsEnum {
		RIGHT_GRANTED, // 
		RIGHT_NAME, // right name
		RIGHT_TYPE;
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public SecGrouprightRightComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecRight o1, SecRight o2) {

		final int v;

		switch (columnIndex) {
		// case RIGHT_GRANTED:
		// v =
		// o1.getSecGroup().getGrpShortdescription().compareTo(o2.getSecGroup
		// ().getGrpShortdescription());
		// break;
		case RIGHT_NAME:
			v = o1.getRigName().compareTo(o2.getRigName());
			break;
		case RIGHT_TYPE:
			v = o1.getRigType().compareTo(o2.getRigType());
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}