package fw.jbiz.ext.websocket.carry;

import java.io.IOException;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.helper.JsonHelper;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.websocket.ZWsContainer;
import fw.jbiz.ext.websocket.ZWsEventChannel;
import fw.jbiz.ext.websocket.ZWsHandler;
import fw.jbiz.ext.websocket.ZWsHandlerManager;
import fw.jbiz.ext.websocket.ZWsHandlerParam;
import fw.jbiz.logic.interfaces.IResponseObject;

public class ZWsEndpoint extends Endpoint {

	static Logger logger = Logger.getLogger(ZWsEndpoint.class);
	
    private Session _session;
    private ZWsHandler _wsHandler;
    private boolean isSignIn = false;
    
    private class ZWsMessageHandler implements MessageHandler.Whole<String> {
    	
		@Override
		public void onMessage(String message) {

			logger.debug(message);
			
	    	IResponseObject response = new ZGsonObject();
	    	response.add(IResponseObject.RSP_KEY_STATUS, IResponseObject.RSP_CD_OK_SILENT);
	    	
			dispatchMessage(message, response);
			Integer status =  (Integer)response.get(IResponseObject.RSP_KEY_STATUS);
			if (status != IResponseObject.RSP_CD_OK_SILENT) {
				// 需要返回消息
				respond(response.toString());
			}
			
			response.ending();
		}
	};

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		logger.debug(String.format("sessionId=%s", session.getId()));
		
		_session = session;
		_session.addMessageHandler(new ZWsMessageHandler());
		
		ZWsContainer.add(this);
	}

    @Override
    public void onClose(Session session, CloseReason closeReason) {

		logger.debug(String.format("sessionId=%s", session.getId()));
		
		ZWsEventChannel.clean(session.getId());
    	ZWsContainer.remove(this);
    	_wsHandler.onClose(closeReason);
    }
    
    @Override
    public void onError(Session session, Throwable throwable) {
		logger.debug(String.format("sessionId=%s", session.getId()));
    	logger.error(ZObject.trace(throwable));
    }
    
    public Session getSession() {
    	return _session;
    }
    
    public ZWsHandler getWsHandler() {
    	return _wsHandler;
    }
    
    public void respond(String message) {

		logger.debug(message);
		
		try {
			_session.getBasicRemote().sendText(message);
		} catch (IOException e) {

	    	logger.error("", e);
		}
    }
    
    public void close() {

		try {
			_session.close();
		} catch (IOException e) {

	    	logger.error(ZObject.trace(e));
		}
    }
    
    private void dispatchMessage(String message, IResponseObject response) {

		logger.debug(message);
		
    	// 格式校验
    	if (!validateMessage(message, response)) {
    		
    		return;
    	}
    	
    	String msgType = (String)JsonHelper.getStrValFromJsonStr(ZWsHandlerParam.MSG_KEY_MSGTYPE, message);
    	
    	// 登录消息
    	if (!isSignIn) {
    		
    		// 第一条必须是登录消息
    		if (ZWsHandlerParam.MSG_TYPE_SIGN_IN.equals(msgType)) {
    			
    			signIn(message, response);
    			return;
    		} else {

        		response.add("status", ZWsHandler.RSP_CD_ERR_SIGN_IN);
        		response.add("msg", "need sign in.");
        		return;
    		}
    		
    	}
    	
    	// 退出消息
		if (ZWsHandlerParam.MSG_TYPE_SIGN_OUT.equals(msgType)) {
			
			signOut(message, response);
			return;
		}
    	
    	// 处理消息
		handleMessage(message, response);
    	return;
    }
    

	// 登录
    private void signIn(String message, IResponseObject response) {

		logger.debug(message);
		
    	// 查找handler
    	String path = JsonHelper.getStrValFromJsonStr(ZWsHandlerParam.MSG_KEY_PATH, message);
    	Class<? extends ZWsHandler> clz = ZWsHandlerManager.getHandlerClass(path);
    	
    	// path 未定义
    	if (clz == null) {

    		logger.error(String.format("invalid path [%s]", path));
    		response.add(IResponseObject.RSP_KEY_STATUS, IResponseObject.RSP_CD_ERR_PARAM);
    		response.add(IResponseObject.RSP_KEY_MSG, "invalid path");
    		return;
    	}
    	
    	// 生成handler
    	try {
    		if (_wsHandler == null) {
	    		_wsHandler = clz.newInstance();
	    		_wsHandler.setSession(_session);
    		}
		} catch (InstantiationException | IllegalAccessException e) {

    		logger.error(ZObject.trace(e));
    		response.add(IResponseObject.RSP_KEY_STATUS, IResponseObject.RSP_CD_ERR_UNKNOWN);
    		response.add(IResponseObject.RSP_KEY_MSG, ZObject.trace(e));
    		return;
			
		}

    	ZWsHandlerParam handlerParam = msg2Param(message);
    	
    	// 身份验证
    	if (!_wsHandler.auth(handlerParam, response)) {
    		isSignIn = false;
    		response.add(IResponseObject.RSP_KEY_STATUS, IResponseObject.RSP_CD_ERR_AUTH);
    		response.add(IResponseObject.RSP_KEY_MSG, "authentication failed.");
    		return;
    	}
    	
    	isSignIn =true;
    	_wsHandler.onSignIn(handlerParam, response);
    }
    
    // 退出
    private void signOut(String message, IResponseObject response) {

		logger.debug(message);
		
		response.add(IResponseObject.RSP_KEY_CALLBACK,
				new IResponseObject.Ending() {

					@Override
					public void run() {
						close();
					}
					
				});
		
    	_wsHandler.onSignOut(msg2Param(message), response);
    	
    	
    }
    
    // 处理消息
    private void handleMessage(String message, IResponseObject response) {

		logger.debug(message);
		
    	ZWsHandlerParam handlerParam = msg2Param(message);
    	
    	if (!_wsHandler.validate(handlerParam, response)) {
    		return;
    	}
    	
    	_wsHandler.onMessage(handlerParam, response);
    	
    }
    
    private ZWsHandlerParam msg2Param(String message) {
    	ZWsHandlerParam handlerParam = _wsHandler.getHandlerParam().fromJson(message);
    	return handlerParam;
    }
    
    /*
     * 消息格式校验
     */
    private boolean validateMessage(String message, IResponseObject response) {

    	Map<String, Object> msgMap = JsonHelper.jsonStr2Map(message);
    	if (msgMap.isEmpty()) {
    		
    		logger.error(String.format("invalid message [%s]", message));
    		response.add(IResponseObject.RSP_KEY_STATUS, IResponseObject.RSP_CD_ERR_PARAM);
    		response.add(IResponseObject.RSP_KEY_MSG, "invalid message:[no json]");
    		return false;
    	}
    	
    	String msgType = (String)msgMap.get(ZWsHandlerParam.MSG_KEY_MSGTYPE);
    	String path = (String)msgMap.get(ZWsHandlerParam.MSG_KEY_PATH);
    	
    	// sign in, path needed.
    	if (ZWsHandlerParam.MSG_TYPE_SIGN_IN.equals(msgType)) {
	    	if (StringUtils.isEmpty(path)) {

	    		logger.error(String.format("invalid message [%s]", message));
				
	    		response.add("status", IResponseObject.RSP_CD_ERR_PARAM);
	    		response.add("msg", "invalid message:[no msgType or path]");
	    		return false;
	    	}
    	}
    	
    	return true;
    }
}
