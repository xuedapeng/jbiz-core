package fw.jbiz.ext.websocket;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.common.helper.AnnotationHelper;
import fw.jbiz.ext.websocket.annotation.WsHandler;
import fw.jbiz.ext.websocket.carry.ZWsEndpoint;
import fw.jbiz.logic.interfaces.IResponseObject;

public class ZWsHandlerManager extends ZObject {

	static Logger logger = Logger.getLogger(ZWsHandlerManager.class);
	
	public static final String PROP_WS_HANDLER_PKG = "websocket_handler_package";
	public static final String PROP_WS_ROOT_PATH = "websocket_root_path";
	
	static String WS_ROOT_PATH_DEFAULT = "/zws";

	// path, handlerClass
	static Map<String, Class<? extends ZWsHandler>> _handlerPathMap = new HashMap<String, Class<? extends ZWsHandler>>();
	
	public static String getWsRootPath() {
	
		String wsRoot = ZSystemConfig.getProperty(PROP_WS_ROOT_PATH);
		if (StringUtils.isEmpty(wsRoot)) {
			wsRoot = WS_ROOT_PATH_DEFAULT;
		}
			
		return wsRoot;
	}
	
	

	public static void broadcast(IResponseObject response) {
		
		String message = response.toString();
		for (ZWsEndpoint ep: ZWsContainer.getAll()) {
			ep.respond(message);
		}
		
	}
	
	public static void send(IResponseObject response, String sessionId) {
		ZWsEndpoint ep = ZWsContainer.get(sessionId);
		if (ep != null) {
			ep.respond(response.toString());
		}
	}
	
	public static boolean assertSession(String sessionId) {
		ZWsEndpoint ep = ZWsContainer.get(sessionId);
		if (ep == null || ep.getSession() == null) {
			return false;
		}
		return true;
	}
	
	public static void send(IResponseObject response, List<String> sessionIdList) {
		for (String sessionId: sessionIdList) {
			send(response, sessionId);
		}
	}
	
	public static void closeSession(String sessionId) {

		ZWsEndpoint ep = ZWsContainer.get(sessionId);
		ep.close();
	}

	public static Class<? extends ZWsHandler> getHandlerClass(String path) {
		
		Class<? extends ZWsHandler> clz = _handlerPathMap.get(path);
		return clz;
	}
	
	/*
	 *  扫描注解WsHandler,并保存 path／handlerClass 关系Map 。
	 */
	@SuppressWarnings("unchecked")
	public static void initialize() {
		
		logger.info("jbiz websocket manager init.");
		
		String packageName = ZSystemConfig.getProperty(PROP_WS_HANDLER_PKG);
		Map<String, Annotation> annMap = AnnotationHelper.getAnnotationOnClass(packageName, WsHandler.class);
		
		for(String clsName: annMap.keySet()) {
			WsHandler annotation = (WsHandler)annMap.get(clsName);
			String path = annotation.path();
			
			// path 不可重复
			if (_handlerPathMap.containsKey(path)) {
				String message = String.format("duplicate path for Websocket Handler Class: class=%s, path=%s", 
						clsName, path);
						
				logger.error(message);
				throw new ZException("JBIZ_WEBSOCKET", message);
			}
			
			try {
				_handlerPathMap.put(path, (Class<? extends ZWsHandler>) Class.forName(clsName));

				logger.info(String.format("handler class found:\n path=%s, class=%s", path, clsName));
			} catch (ClassNotFoundException e) {
						
				logger.error(trace(e));
				throw new ZException("JBIZ_WEBSOCKET", e);
			}
		}
	}
	
	// test
	public static void main(String[] args) throws SQLException {
		String packageName = "fw.jbiz.ext.websocket";
		Map<String, Annotation> map = AnnotationHelper.getAnnotationOnClass(packageName, WsHandler.class);
		
		WsHandler annotation = (WsHandler)map.get(ZWsHandler.class.getName());

		// System.out.println(ZWsHandler.class.getName());
		System.out.println(annotation.path());
	}
}
