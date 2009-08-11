package de.forsthaus.zksample.webui.user.model;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.math.NumberUtils;

import de.forsthaus.backend.model.SecUser;

public class UserComparator implements Comparator<SecUser>, Serializable {

	private static final long serialVersionUID = 6238428560188676543L;

	public enum FieldsEnum {
		USER_ID, // user number (not 'id'), if needed
		USER_LOGINNAME, // user login name
		USER_FIRSTNAME, // user first name
		USER_EMAIL, // user email
		USER_LASTNAME, // user lastname
		USER_ENABLED, // user lastname
		USER_ACCOUNT_NON_EXPIRED, USER_CREDENTIALS_NON_EXPIRED, USER_ACCOUNT_NON_LOCKED;
	}

	final transient private FieldsEnum columnIndex;
	private transient boolean ascending;

	public UserComparator(boolean ascd, FieldsEnum columnIndex) {
		this.ascending = ascd;
		this.columnIndex = columnIndex;

		assert columnIndex != null;
	}

	@Override
	public int compare(SecUser o1, SecUser o2) {

		final int v;

		switch (columnIndex) {
		case USER_ID:
			v = NumberUtils.compare(o1.getUsrId(), o2.getUsrId());
			break;
		case USER_LOGINNAME:
			v = o1.getUsrLoginname().compareTo(o2.getUsrLoginname());
			break;
		case USER_FIRSTNAME:
			v = o1.getUsrFirstname().compareTo(o2.getUsrFirstname());
			break;
		case USER_EMAIL:
			v = o1.getUsrEmail().compareTo(o2.getUsrEmail());
			break;
		case USER_LASTNAME:
			v = o1.getUsrLastname().compareTo(o2.getUsrLastname());
			break;
		case USER_ENABLED:
			v = String.valueOf(o1.isUsrEnabled()).compareTo(String.valueOf(o2.isUsrEnabled()));
			break;
		case USER_ACCOUNT_NON_EXPIRED:
			v = String.valueOf(o1.isUsrAccountnonexpired()).compareTo(String.valueOf(o2.isUsrAccountnonexpired()));
			break;
		case USER_CREDENTIALS_NON_EXPIRED:
			v = String.valueOf(o1.isUsrCredentialsnonexpired()).compareTo(String.valueOf(o2.isUsrCredentialsnonexpired()));
			break;
		case USER_ACCOUNT_NON_LOCKED:
			v = String.valueOf(o1.isUsrAccountnonlocked()).compareTo(String.valueOf(o2.isUsrAccountnonlocked()));
			break;

		// In the case of unknown
		default:
			throw new IllegalArgumentException();
		}

		return ascending ? v : -v;
	}

}