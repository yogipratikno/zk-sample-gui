package de.forsthaus.zksample;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import de.forsthaus.zksample.policy.model.UserImpl;

/**
 * Workspace for the user. <b>ONE</b> workspace per userSession. <br>
 * <br>
 * Every logged in user have his own workspace. <br>
 * Here are stored several properties for the user. <br>
 * <br>
 * 1. Access the rights that the user have. <br>
 * 2. The office for that the user are logged in. <br>
 * 
 * author: bj
 * 
 */
public class UserWorkspace implements Serializable {

	private static final long serialVersionUID = -3936210543827830197L;
	private transient final static Logger logger = Logger.getLogger(UserWorkspace.class);

	static private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Get a logged-in users WorkSpace which holds all necessary vars. <br>
	 * 
	 * @return the users WorkSpace
	 */
	public static UserWorkspace getInstance() {
		return ((UserImpl) getAuthentication().getPrincipal()).getWorkspace();
	}

	private String userLanguage;
	private Properties userLanguageProperty;

	private int currentDesktopHeight; // actual Desktop Height
	private int currentDesktopWidth; // actual Desktop Width
	private boolean treeMenu = true;

	private Set<String> grantedAuthoritySet = null;

	/**
	 * Default Constructor
	 */
	public UserWorkspace() {
		if (logger.isDebugEnabled()) {
			logger.debug("create new User Workspace [" + this + "]");
		}

		// speed up the ModalDialogs while disabling the animation
		Window.setDefaultActionOnShow("");
	}

	/**
	 * Logout with the spring-security logout action-URL.<br>
	 * Therefore we make a sendRedirect() to the logout uri we <br>
	 * have configured in the spring-config.br>
	 */
	public void doLogout() {
		// Sessions.getCurrent().invalidate();
		Executions.sendRedirect("/j_spring_logout");
	}

	/**
	 * Copied the grantedAuthorities to a Set of strings <br>
	 * for a faster searching in it.
	 * 
	 * @return String set of GrantedAuthorities (rightNames)
	 */
	private Set<String> getGrantedAuthoritySet() {

		if (grantedAuthoritySet == null) {

			GrantedAuthority[] list = getAuthentication().getAuthorities();
			grantedAuthoritySet = new HashSet<String>(list.length);

			for (GrantedAuthority grantedAuthority : list) {
				grantedAuthoritySet.add(grantedAuthority.getAuthority());
			}
		}
		return grantedAuthoritySet;
	}

	/**
	 * Checks if a right is in the <b>granted rights</b> that the logged in user
	 * have. <br>
	 * 
	 * @param rightName
	 * @return true, if the right is in the granted user rights.<br>
	 *         false, if the right is not granted to the user.<br>
	 */
	public boolean isAllowed(String rightName) {

		if (getGrantedAuthoritySet().contains(rightName)) {
			return true;
		} else {
			return false;
		}
	}

	public void setCurrentDesktopHeight(int currentDesktopHeight) {
		this.currentDesktopHeight = currentDesktopHeight;
	}

	public int getCurrentDesktopHeight() {
		if (isTreeMenu() == true) {
			return currentDesktopHeight;
		}

		// menuBar for DropDown menu
		return currentDesktopHeight - 25;
	}

	public void setCurrentDesktopWidth(int currentDesktopWidth) {
		this.currentDesktopWidth = currentDesktopWidth;
	}

	public int getCurrentDesktopWidth() {
		return currentDesktopWidth;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguageProperty(Properties userLanguageProperty) {
		this.userLanguageProperty = userLanguageProperty;
	}

	public Properties getUserLanguageProperty() {

		// // TODO only for testing. we must get the language from
		// // the users table field
		// userLanguageProperty =
		// ApplicationWorkspace.getInstance().getPropEnglish();
		// userLanguageProperty =
		// ApplicationWorkspace.getInstance().getPropGerman();
		//
		// return userLanguageProperty;
		return null;
	}

	public void setTreeMenu(boolean treeMenu) {
		this.treeMenu = treeMenu;
	}

	public boolean isTreeMenu() {
		return treeMenu;
	}

}
