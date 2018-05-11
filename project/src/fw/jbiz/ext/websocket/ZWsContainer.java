package fw.jbiz.ext.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fw.jbiz.ZObject;
import fw.jbiz.ext.websocket.carry.ZWsEndpoint;

public class ZWsContainer extends ZObject {

	// <sessionId, ZWsEndpoint>
	private final static Map<String, ZWsEndpoint> _connectionMap = new ConcurrentHashMap<String, ZWsEndpoint>();

	public static void add(ZWsEndpoint endpoint) {
		
		String sessionId = endpoint.getSession().getId();
		_connectionMap.put(sessionId, endpoint);
	}

	public static void remove(ZWsEndpoint endpoint) {

		String sessionId = endpoint.getSession().getId();
		_connectionMap.remove(sessionId);
	}
	
	public static void remove(String sessionId) {
		
		_connectionMap.remove(sessionId);
	}
	
	public static ZWsEndpoint get(String sessionId) {
		
		return _connectionMap.get(sessionId);
	}
	
	protected static List<ZWsEndpoint> getAll() {
		
		List<ZWsEndpoint> pointList = new ArrayList<ZWsEndpoint>();
		
		for(String sessionId: _connectionMap.keySet()) {
			pointList.add(_connectionMap.get(sessionId));
			
		}
		
		return pointList;
	}
	
	
	
}
