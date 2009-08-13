package de.forsthaus.zksample.policy;

import java.io.Serializable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.Authentication;
import org.springframework.security.ui.WebAuthenticationDetails;

import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.zksample.policy.model.UserImpl;

/**
 * This class is called from spring aop as an aspect and is for logging <br>
 * the Login of a user. It is configurated in the <br>
 * '/WebContent/WEB-INF/SpringSecurityContext.xml' <br>
 * Logs success and fails, sessionID, timestamp and remoteIP. <br>
 * 
 * @author bj
 * 
 */
public class LoginLoggingPolicyService implements Serializable {

	private static final long serialVersionUID = -8009761216967245783L;

	private transient LoginLoggingService loginLoggingService;

	/**
	 * default constructor.<br>
	 */
	public LoginLoggingPolicyService() {
	}

	private void logAuthPass(Authentication authentication) {
		final String user = authentication.getName();
		final long userId = ((UserImpl) authentication.getPrincipal()).getUserId();
		final String clientAddress = convertClientAddress(authentication);
		final String sessionId = convertClientSessionId(authentication);
		getLoginLoggingService().logAuthPass(user, userId, clientAddress, sessionId);
	}

	private void logAuthFail(Authentication authentication) {
		final String user = authentication.getName();
		final String clientAddress = convertClientAddress(authentication);
		final String sessionId = convertClientSessionId(authentication);
		getLoginLoggingService().logAuthFail(user, clientAddress, sessionId);
	}

	public Authentication loginLogging(ProceedingJoinPoint call) throws Throwable {
		final Authentication authentication = (Authentication) call.getArgs()[0];

		final Authentication result;
		try {
			result = (Authentication) call.proceed();
		} catch (Exception e) {
			logAuthFail(authentication);
			throw e;
		}

		if (result != null) {
			logAuthPass(result);
		}

		return result;
	}

	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

	private String convertClientAddress(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getRemoteAddress();
		} catch (ClassCastException e) {
			// securitycontext ist vom falschen Typ!
			return "<unbekannt>";
		}
	}

	private String convertClientSessionId(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getSessionId();
		} catch (ClassCastException e) {
			// securitycontext ist vom falschen Typ!
			return "<unbekannt>";
		}
	}
}