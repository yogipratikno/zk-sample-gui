package de.forsthaus.zksample.webui.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the base controller for all zul-files that will extends <br>
 * from the window component.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author hkn
 * @changes 07/24/2009: sge changes for clustering
 * 
 */
public abstract class BaseCtrl extends Window implements AfterCompose, Serializable {

	private static final long serialVersionUID = -2179229704315045689L;

	protected transient AnnotateDataBinder binder;
	protected transient Map<String, Object> args;

	public void doOnCreateCommon(Window w) throws Exception {
		binder = new AnnotateDataBinder(w);
		binder.loadAll();
	}

	public void doOnCreateCommon(Window w, Event fe) throws Exception {
		doOnCreateCommon(w);
		CreateEvent ce = (CreateEvent) ((ForwardEvent) fe).getOrigin();
		args = (Map<String, Object>) ce.getArg();
	}

	/**
	 * default constructor.<br>
	 */
	public BaseCtrl() {
		super();
	}

	@Override
	public void afterCompose() {
		processRecursive(this, this);

		Components.wireVariables(this, this); // auto wire variables
		Components.addForwards(this, this); // auto forward
	}

	/*
	 * Are there inner window components than wire these too.
	 */
	private void processRecursive(Window main, Window child) {
		Components.wireVariables(main, child);
		Components.addForwards(main, this);
		List<Component> winList = (List<Component>) child.getChildren();
		for (Component window : winList) {
			if (window instanceof Window) {
				processRecursive(main, (Window) window);
			}
		}
	}

}
