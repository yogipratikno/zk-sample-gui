/**
 * 
 */
package de.forsthaus.zksample.policy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.zksample.policy.model.UserImpl;

/**
 * This class implements the spring-security UserDetailService.<br>
 * It's been configured in the spring security xml contextfile.<br>
 * 
 * @author bj
 * @see package de.forsthaus.policy
 */
public class PolicyManager implements UserDetailsService, Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(PolicyManager.class);

	private transient UserService userService;

	public SecUser getUserByLoginname(final String userName) {
		return getUserService().getUserByLoginname(userName);
	}

	@Override
	public UserDetails loadUserByUsername(String userId) {

		SecUser user = null;
		GrantedAuthority[] grantedAuthorities = null;
		try {
			user = getUserByLoginname(userId);

			if (user == null) {
				throw new UsernameNotFoundException("Invalid User");
			}

			// TEST
			String context = user.getUsrLocale(); // i.e. 'en_EN' or 'de_DE'

			// if (!StringUtils.isEmpty(context)) {
			// Labels.register(new GeneralLabelLocator(context));
			// }

			grantedAuthorities = getGrantedAuthority(user);
		} catch (NumberFormatException e) {
			throw new DataRetrievalFailureException("Cannot loadUserByUsername userId:" + userId + " Exception:" + e.getMessage(), e);
		}

		UserDetails userDetails = new UserImpl(user, grantedAuthorities);

		if (logger.isDebugEnabled()) {
			logger.debug("Rechte für '" + user.getUsrLoginname() + "' (ID: " + user.getUsrId() + ") ermittelt. ("
					+ Arrays.toString(grantedAuthorities) + ") [" + this + "]");

			for (GrantedAuthority grantedAuthority : grantedAuthorities) {
				logger.debug(grantedAuthority.getAuthority());
			}
		}

		return userDetails;

	}

	private GrantedAuthority[] getGrantedAuthority(SecUser user) {

		List<SecRight> rights = getUserService().getRightsByUser(user);

		GrantedAuthority[] grantedAuthorities = null;
		grantedAuthorities = new GrantedAuthority[rights.size()];

		for (int i = 0; i < rights.size(); i++) {

			SecRight right = rights.get(i);

			GrantedAuthority authority = new GrantedAuthorityImpl(right.getRigName());
			grantedAuthorities[i] = authority;
		}

		return grantedAuthorities;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void test() {
		System.out.println("PolicyManager.test() -> " + loadUserByUsername("user"));
	}
}
