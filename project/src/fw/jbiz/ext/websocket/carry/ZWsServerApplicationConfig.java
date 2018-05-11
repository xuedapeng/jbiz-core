package fw.jbiz.ext.websocket.carry;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.log4j.Logger;

import fw.jbiz.ext.websocket.ZWsHandlerManager;

public class ZWsServerApplicationConfig implements ServerApplicationConfig {

	static Logger logger = Logger.getLogger(ZWsServerApplicationConfig.class);

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		
		logger.info(String.format("scanned.size=:%s", scanned.size()));
		return scanned;
	}

	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> scanned) {

		logger.info(String.format("scanned.size=%s", scanned.size()));
		
        Set<ServerEndpointConfig> result = new HashSet<>();
        if (scanned.contains(ZWsEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
                    ZWsEndpoint.class,
                    ZWsHandlerManager.getWsRootPath()).build());

    		logger.info(String.format("config:pointClass=%s, rootPath=%s", 
    				ZWsEndpoint.class.getName(), ZWsHandlerManager.getWsRootPath()));
        }
        
        return result;
	}

}
