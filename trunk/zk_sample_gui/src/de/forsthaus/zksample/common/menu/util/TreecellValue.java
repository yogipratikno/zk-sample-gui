/**
 * 
 */
package de.forsthaus.zksample.common.menu.util;

import java.io.Serializable;

/**
 * @author bj
 * 
 */
public class TreecellValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient String id;
	private transient boolean visible = true;
	private transient String zulNavigation;
	private transient String label;
	private transient String rightName = null;
	private transient boolean withOnClickAction = true;

	public TreecellValue() {
	}

	public TreecellValue(String id, String zulNavigation, String label, boolean visible) {
		super();
		this.id = id;
		this.zulNavigation = zulNavigation;
		this.label = label;
		this.visible = visible;
	}

	public TreecellValue setAll(String id, String zulNavigation, String rightName, String label) {
		this.id = id;
		this.label = label;
		this.rightName = rightName;
		this.zulNavigation = zulNavigation;
		return this;
	}

	public TreecellValue setAll(String id, String zulNavigation, String rightName, String label, boolean visible) {
		this.id = id;
		this.label = label;
		this.visible = visible;
		this.rightName = rightName;
		this.zulNavigation = zulNavigation;
		return this;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isWithOnClickAction() {
		return this.withOnClickAction;
	}

	public void setWithOnClickAction(boolean withOnClickAction) {
		this.withOnClickAction = withOnClickAction;
	}

	public String getRightName() {
		return this.rightName;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public String getZulNavigation() {
		return this.zulNavigation;
	}

	public void setZulNavigation(String zulNavigation) {
		this.zulNavigation = zulNavigation;
	}
}
