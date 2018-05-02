package fw.jbiz.ext.memcache;


import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZCustomConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.common.helper.JsonHelper;
import fw.jbiz.ext.memcache.bean.ZCacheProp;
import fw.jbiz.ext.memcache.interfaces.ICache;
import fw.jbiz.logic.ZDbProcessor;

public class ZCacheManager extends ZObject {

	static Logger logger = Logger.getLogger(ZCacheManager.class);
	
	private static final String CONFIG_FILE_KEY = "memcache_config_file";
	private static final String KEY_REFRESH_FREQ = "REFRESH_FREQ";
	private static final String KEY_CACHE_INFO = "CACHE_INFO";
	
	private static Map<String, ICache> _cacheObjectMap = new HashMap<String, ICache>();
	private static Map<String, ZCacheProp> _cachePropMap = new HashMap<String, ZCacheProp>();
	
	private static IConfig mConfig = null;
	
	static {
		if (ZSystemConfig.getProperty(CONFIG_FILE_KEY) != null) {
			mConfig = new ZCustomConfig(
				Paths.get(ZSystemConfig.getSystemConfigPath(), 
						ZSystemConfig.getProperty(CONFIG_FILE_KEY)).toString());
		}
	}
	
	private static int _refreshFreq = Integer.valueOf(mConfig.getProp(KEY_REFRESH_FREQ));
	private static String _cacheInfo = mConfig.getProp(KEY_CACHE_INFO);
	
	private static boolean refreshTaskRunning = false;
	private static boolean isLoaded = false;
	
	private final static int WAIT_FOR_LOAD = 60*1000; //60秒 
	private final static int IDLE_FOR_LOAD = 100; //0.1秒 
	
	
	// 使用时获取
	public static ICache getInstance(String cacheName) {
		
		if (!isLoaded) {
			loadAllCache();
		} else {
			// 其它请求正在加载，最多等待60秒
			 long startTime = new Date().getTime();
			 while(_cacheObjectMap.get(cacheName) == null) {
				 
				 long nowTime = new Date().getTime();
				 long waitMs = nowTime - startTime;
				 if (waitMs > WAIT_FOR_LOAD) {
					 logger.info(String.format("give up. waited for %d ms", waitMs));
					 break;
				 } else {
					 logger.info(String.format("waiting for %d ms", waitMs));
				 }
				 
				 try {
					 
					Thread.sleep(IDLE_FOR_LOAD); // 休眠10毫秒
					logger.info(String.format("idle sleep:%d", IDLE_FOR_LOAD));
					
				} catch (InterruptedException e) {
					
					logger.error(trace(e));
				}
			 }
		}
		
		return _cacheObjectMap.get(cacheName);
	}
	
	// 第一次调用getInstance时执行
	public static void loadAllCache() {
		
		// 防止重复加载
		if (isLoaded) {
			return;
		}

		isLoaded = true;

		try {
			new ZDbProcessor(){
				@Override
				public void execute(EntityManager em) {
					
					loadAllCache(em);
				}
			}.run();
			
		} catch(Exception e) {
			isLoaded = false;
			logger.error(e);
			throw new ZException("MEMCACHE", e);
		}
		
		// 加载成功后，启动自动刷新
		startRefreshTask();
		
	}
	
	public static void reloadAllCache() {

		_cacheObjectMap.clear();
		_cachePropMap.clear();
		isLoaded = false;
		loadAllCache();
		
	}
	
	private static void startRefreshTask() {
		
		// 防止重复执行
		if (refreshTaskRunning) {
			return;
		}

		TimerTask task = new TimerTask() {
			@Override  
			public void run() {
				logger.info(String.format("#START refresh#: [ZCacheManager#autoRefresh]"));
				autoRefresh();
				logger.info(String.format("#END refresh#: [ZCacheManager#autoRefresh]"));
			}
		};

		refreshTaskRunning = true; 
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(task, 10, _refreshFreq,
				TimeUnit.SECONDS);
		
	}
	
	private static void autoRefresh() {

		new ZDbProcessor(){
			@Override
			public void execute(EntityManager em) {
				
				autoRefresh(em);
			}
		}.run();
		
	}
	
	private static void autoRefresh(EntityManager em) {

		logger.info("autoRefresh start");
		printCacheInfo();
		
		for (String key: _cacheObjectMap.keySet()) {
			ICache cacheObject = _cacheObjectMap.get(key);
			if (cacheObject.getCacheProp().isAutoRefresh()) {
				cacheObject.refresh(em);
			}
		}
		
		logger.info("autoRefresh end");
		printCacheInfo();
	}
	
	private static void loadAllCache(EntityManager em) {

		logger.info("loadAllCache start");
		printCacheInfo();
		
		List<Object> cacheConfigList = JsonHelper.jsonStr2List(_cacheInfo);
		
		for(Object item: cacheConfigList) {
			@SuppressWarnings("unchecked")
			Map<String, String> config = (Map<String, String>)item;
			ZCacheProp cacheProp = new ZCacheProp(config);
			String cacheName = cacheProp.getCacheName();
			String implClass = cacheProp.getImplClass();
			
			ICache cacheObject = new ZCacheImpl();
			if (StringUtils.isEmpty(implClass)) {
				// 暂不支持自定义缓存实现
			}
			cacheObject.init(cacheProp, em);
			
			_cacheObjectMap.put(cacheName, cacheObject);
			_cachePropMap.put(cacheName, cacheProp);
		}

		logger.info("loadAllCache end");
		printCacheInfo();
	}
	
	private static void printCacheInfo() {
		
		String info = String.format("_cacheObjectMap.size = %d", _cacheObjectMap.size());
		String detail = "[";
		
		for(String cacheName: _cacheObjectMap.keySet()) {

			detail += String.format("{cacheName=%s, size=%d},", 
					cacheName, _cacheObjectMap.get(cacheName).size());
		}
		
		detail += "]";
		
		info += "\n";
		info += detail;
		
		logger.info(info);
				
	}
		

}
