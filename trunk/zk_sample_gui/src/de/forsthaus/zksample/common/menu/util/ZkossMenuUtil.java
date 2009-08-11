/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;

import de.forsthaus.zksample.UserWorkspace;

/**
 * @author bj
 * 
 */
public class ZkossMenuUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Log LOGGER = LogFactory.getLog(ZkossMenuUtil.class);

	final transient private LinkedList<Component> stack;

	private transient UserWorkspace workspace = null;

	/**
	 * @Deprecated Nur für getZulBaum(Component) verwenden!
	 */
	@Deprecated
	public ZkossMenuUtil() {
		this(null);
	}

	public ZkossMenuUtil(Component component) {
		super();
		if (component == null) {
			this.stack = null;
		} else {
			this.stack = new LinkedList<Component>();
			push(component);
		}
	}

	public boolean addSubMenu(TreecellValue treecellValue) {
		if (isAllowed(treecellValue)) {
			DefaultTreeitem treechildren = new DefaultTreeitem(getCurrentComponent());

			DefaultTreecell defaultTreecell = treechildren.addTreerow().addTreecell();

			setAttributesWithoutAction(treecellValue, defaultTreecell);

			push(treechildren.addTreechildren());
			return true;
		}
		return false;
	}

	private boolean isAllowed(TreecellValue treecellValue) {
		return isAllowed(treecellValue.getRightName());
	}

	public boolean addTreeitem(TreecellValue treecellValue) {
		if (isAllowed(treecellValue)) {
			DefaultTreeitem treeitem = new DefaultTreeitem(getCurrentComponent());

			DefaultTreecell defaultTreecell = treeitem.addTreerow().addTreecell();

			setAttributes(treecellValue, defaultTreecell);

			return true;
		}
		return false;
	}

	public void ebeneHoch() {
		poll();
	}

	private Component getCurrentComponent() {
		return peek();
	}

	private Log getLogger() {
		return this.LOGGER;
	}

	public UserWorkspace getWorkspace() {
		if (this.workspace == null) {
			this.workspace = UserWorkspace.getInstance();
		}
		return this.workspace;
	}

	public String getZulBaum(Component component) {
		return getZulBaumImpl(component, new StringBuilder(1000), 0).toString();
	}

	@SuppressWarnings("unchecked")
	private StringBuilder getZulBaumImpl(Component component, StringBuilder result, int tiefe) {
		++tiefe;
		result.append(StringUtils.leftPad("", tiefe << 2));
		result.append(component);
		result.append('\n');
		if (component.getChildren() != null) {
			for (Iterator iterator = component.getChildren().iterator(); iterator.hasNext();) {
				getZulBaumImpl((Component) iterator.next(), result, tiefe);
			}
		}
		return result;
	}

	private boolean isAllowed(String rightName) {
		if (StringUtils.isEmpty(rightName)) {
			return true;
		}
		return getWorkspace().isAllowed(rightName);
	}

	private Component peek() {
		return this.stack.peek();
	}

	private Component poll() {
		try {
			return this.stack.poll();
		} finally {
			if (this.stack.isEmpty()) {
				throw new RuntimeException("Root ist nicht mehr vorhanden!");
			}
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("current component: " + getCurrentComponent());
			}
		}
	}

	private void push(Component e) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("current component: " + e);
		}
		this.stack.push(e);
	}

	private void setAttributes(TreecellValue treecellValue, DefaultTreecell defaultTreecell) {
		if (treecellValue.isWithOnClickAction()) {
			defaultTreecell.setZulNavigation(treecellValue.getZulNavigation());
		}

		setAttributesWithoutAction(treecellValue, defaultTreecell);
	}

	private void setAttributesWithoutAction(TreecellValue treecellValue, DefaultTreecell defaultTreecell) {

		defaultTreecell.setId(treecellValue.getId());
		defaultTreecell.setLabel(" " + treecellValue.getLabel());
		defaultTreecell.setVisible(treecellValue.isVisible());
	}
}
