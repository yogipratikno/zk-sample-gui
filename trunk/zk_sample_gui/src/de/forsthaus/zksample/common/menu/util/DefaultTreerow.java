/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treerow;

/**
 * @author bj
 * 
 */
class DefaultTreerow extends Treerow implements Serializable {

	private static final long serialVersionUID = -5426895050052286054L;

	/**
	 * default constructor.<br>
	 */
	DefaultTreerow(Component parent) {
		super();
		parent.appendChild(this);
		// this.setStyle("background-color: #F8F8F8");
	}

	DefaultTreecell addTreecell() {
		return new DefaultTreecell(this);
	}
}
