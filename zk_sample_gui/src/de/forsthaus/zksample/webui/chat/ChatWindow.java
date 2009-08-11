package de.forsthaus.zksample.webui.chat;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.zksample.UserWorkspace;

public class ChatWindow extends Window implements Serializable {

	private static final long serialVersionUID = -7324785621820390012L;
	private transient final static Logger logger = Logger.getLogger(ChatWindow.class);

	private transient String sender;

	private transient ChatRoom chatroom;

	private transient Chatter chatter;

	private transient Desktop desktop;

	private transient boolean isLogin;

	/**
	 * setup initilization
	 * 
	 */
	public void init() {
		desktop = Executions.getCurrent().getDesktop();

		chatroom = (ChatRoom) desktop.getWebApp().getAttribute("chatroom");
		if (chatroom == null) {
			chatroom = new ChatRoom();
			desktop.getWebApp().setAttribute("chatroom", chatroom);
		}
	}

	public void onCreate() {
		init();
	}

	public void onOK() {
		if (isLogin())
			onSendMsg();
		else
			onLogin();
	}

	/**
	 * used for longin
	 * 
	 */
	public void onLogin() {
		// enable server push for this desktop
		desktop.enableServerPush(true);

		sender = ((Textbox) getFellow("nickname")).getValue();

		// start the chatter thread
		chatter = new Chatter(chatroom, sender, getFellow("msgBoard"));
		chatter.start();

		// change state of user
		setLogin(true);

		// refresh UI
		this.setWidth("100%");
		getFellow("dv").setVisible(true);
		getFellow("input").setVisible(true);
		getFellow("login").setVisible(false);
		((Textbox) getFellow("nickname")).setRawValue("");

		int i = UserWorkspace.getInstance().getCurrentDesktopWidth();
		i = i - 240;
		((Div) getFellow("divTextbox")).setWidth(String.valueOf(i) + "px");
		((Textbox) getFellow("msg")).setWidth(String.valueOf(i - 150) + "px");

	}

	/**
	 * used for exit
	 * 
	 */
	public void onExit() {
		// clean up
		chatter.setDone();

		setLogin(false);

		// refresh the UI
		this.setWidth("300px");
		this.setHeight("200px");

		getFellow("msgBoard").getChildren().clear();
		getFellow("login").setVisible(true);
		getFellow("dv").setVisible(false);
		getFellow("input").setVisible(false);

		// disable server push
		desktop.enableServerPush(false);

		// new
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		Center center = bl.getCenter();
		center.setFlex(true);
		center.getChildren().clear();
		Executions.createComponents("/WEB-INF/pages/chat/chat.zul", center, null);

	}

	/**
	 * used to send messages
	 * 
	 */
	public void onSendMsg() {
		// add comment
		Label message = new Label();
		message.setValue(getDateTime() + " / " + sender + ": " + ((Textbox) getFellow("msg")).getValue());
		getFellow("msgBoard").appendChild(message);
		chatter.sendMessage(((Textbox) getFellow("msg")).getValue());
		((Textbox) getFellow("msg")).setRawValue("");

		// scroll down the scrollbar
		((ComponentCtrl) getFellow("dv")).smartUpdate("scrollTop", "10000");

	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean bool) {
		isLogin = bool;
	}

	/**
	 * Get the actual date/time on server. <br>
	 * 
	 * @return String of date/time
	 */
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
