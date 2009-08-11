/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treechildren;

/**
 * @author bj
 * 
 */
class DefaultTreechildren extends Treechildren implements Serializable {

	private static final long serialVersionUID = -3196075413623639125L;

	/**
	 * default constructor.<br>
	 */
	DefaultTreechildren(Component parent) {
		super();
		parent.appendChild(this);
	}

	DefaultTreeitem addTreeitem() {
		return new DefaultTreeitem(this);
	}
}
