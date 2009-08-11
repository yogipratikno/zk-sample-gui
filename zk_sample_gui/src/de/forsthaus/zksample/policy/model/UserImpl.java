package de.forsthaus.zksample.policy.model;

import java.io.Serializable;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;

import de.forsthaus.backend.model.SecUser;
import de.forsthaus.zksample.UserWorkspace;

//import de.daibutsu.token.Md5Token;

/**
 * The user implementation of spring-security framework user class. <br>
 * Extends for our simulation of a one-time-password .<br>
 * <br>
 * Great thanks to Bjoern. <br>
 * Good work. OnTimePassword Tokenizer is not for everybody, sorry. <br>
 * <br>
 * Extends for a user-id (type long). <br>
 * <br>
 * 
 * @author bj
 * 
 */
public class UserImpl extends User implements Serializable {

	private static final long serialVersionUID = 7682359879431168931L;

	final transient private UserWorkspace workspace = new UserWorkspace();

	final transient private long userId;

	/**
	 * Constructor
	 * 
	 * @param username
	 * @param password
	 * @param enabled
	 * @param accountNonExpired
	 * @param credentialsNonExpired
	 * @param accountNonLocked
	 * @param authorities
	 * @throws IllegalArgumentException
	 */
	public UserImpl(SecUser user, GrantedAuthority[] grantedAuthorities) throws IllegalArgumentException {

		super(user.getUsrLoginname(), user.getUsrPassword(), user.isUsrEnabled(), user.isUsrAccountnonexpired(), user
				.isUsrCredentialsnonexpired(), user.isUsrAccountnonlocked(), grantedAuthorities);

		this.userId = user.getUsrId();
	}

	public UserWorkspace getWorkspace() {

		return workspace;
	}

	public long getUserId() {
		return this.userId;
	}
}
