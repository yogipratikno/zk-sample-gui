/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treeitem;

/**
 * @author bj
 * 
 */
public class DefaultTreeitem extends Treeitem implements Serializable {

	private static final long serialVersionUID = -2813840859147955432L;

	/**
	 * default constructor.<br>
	 */
	DefaultTreeitem(Component parent) {
		super();
		parent.appendChild(this);
	}

	DefaultTreerow addTreerow() {
		return new DefaultTreerow(this);
	}

	DefaultTreechildren addTreechildren() {
		return new DefaultTreechildren(this);
	}

}
