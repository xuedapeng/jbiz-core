package fw.jbiz.ext.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.ext.websocket.carry.ZWsEndpoint;
import fw.jbiz.logic.interfaces.IResponseObject;

public class ZWsEventChannel extends ZObject {

	static Logger logger = Logger.getLogger(ZWsEventChannel.class);
	
	// <channelId, List<sessionId>
	static Map<String, List<String>> _channelMap = new ConcurrentHashMap<String, List<String>>();
	// <sessionId, List<channelId>
	static Map<String, List<String>> _sessionMap = new ConcurrentHashMap<String, List<String>>();
	
	public static  void subscribe(String channelId, String sessionId) {
		
		synchronized(_channelMap) {
			if (!_channelMap.containsKey(channelId)) {
				_channelMap.put(channelId, new ArrayList<String>());
			}

			if (!_sessionMap.containsKey(sessionId)) {
				_sessionMap.put(sessionId, new ArrayList<String>());
			}
		}

		List<String> sessionIdList = _channelMap.get(channelId);
		sessionIdList.add(sessionId);
		
		List<String> channeIdlList = _sessionMap.get(sessionId);
		channeIdlList.add(channelId);
		
		logger.info(String.format("subscribed ok. channelId=%s, sessionId=%s", channelId, sessionId));
	}
	
	public static void unSubscribe(String channelId, String sessionId) {
		
		if (_channelMap.containsKey(channelId)) {
			List<String> sessionIdList = _channelMap.get(channelId);
			sessionIdList.remove(sessionId);
		}


		if (_sessionMap.containsKey(sessionId)) {
			List<String> channelIdList = _sessionMap.get(sessionId);
			channelIdList.remove(channelId);
		}

		logger.info(String.format("unsubscribed ok. channelId=%s, sessionId=%s", channelId, sessionId));
		
	}
	
	/*
	 * @return List<channelId>
	 */
	public static List<String> getChannelIdList(String sessionId) {
		
		return _sessionMap.get(sessionId);
		
	}
	
	/*
	 * @return List<sessionId>
	 */
	public static List<String> getSessionIdList(String channelId) {

		return _channelMap.get(channelId);
		
	}
	
	public static void publish(String channelId, IResponseObject response) {
		List<String> sessionIdList = _channelMap.get(channelId);
		ZWsHandlerManager.send(response, sessionIdList);

		logger.info(String.format("published ok. channelId=%s, response=%s", channelId, response.toString()));
	}
	
	// 清理已经断开的session
	public static void clean(String sessionId) {
		
		List<String> channelIdList = _sessionMap.get(sessionId);
		
		if (channelIdList != null) {
			for (String channelId: channelIdList) {
				_channelMap.get(channelId).remove(sessionId);
			}
		}
		_sessionMap.remove(sessionId);
		
	}
}
