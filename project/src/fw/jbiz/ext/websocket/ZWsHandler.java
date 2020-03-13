package fw.jbiz.ext.websocket;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.logic.interfaces.IResponseObject;

public abstract class ZWsHandler extends ZObject {

	static Logger logger = Logger.getLogger(ZWsHandler.class);
	
	public static final int RSP_CD_ERR_SIGN_IN = -11;

	private Session _session;
	
	public abstract boolean auth(ZWsHandlerParam handlerParam, IResponseObject response); // 报名消息用
	public abstract boolean validate(ZWsHandlerParam handlerParam, IResponseObject response); // 以后每条消息用	
	public abstract void onMessage(ZWsHandlerParam handlerParam, IResponseObject response);
	public abstract void onSignIn(ZWsHandlerParam handlerParam, IResponseObject response);
	public abstract void onSignOut(ZWsHandlerParam handlerParam, IResponseObject response);	
	public abstract void onClose(CloseReason closeReason);	

	public abstract ZWsHandlerParam getHandlerParam();	
	
	public Session getSession() {
		return _session ;
	}

	public void setSession(Session session) {
		_session = session;
	}
	
	protected void respond(IResponseObject response) {
		ZWsHandlerManager.send(response, _session.getId());
	}
}
