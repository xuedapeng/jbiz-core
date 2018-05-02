package fw.jbiz.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.concurrent.ZCallback;
import fw.jbiz.logic.ZDbProcessor;

public class ZDaoAsyManager extends ZObject {

	static Logger logger = Logger.getLogger(ZDaoAsyManager.class);
	
	public final static String KEY_ENTITY = "entity";
	public final static String KEY_CALLBACK = "callback";
	public final static String KEY_DAO_CLASS = "daoClass";
	
	private static String CLZ_ZDAO = ZDao.class.getName();
	
	static final long IDLE_SLEEP = 10*1000;// 休眠10秒；
	static final long REST_SLEEP = 1*1000;// 每个事务之间休息1秒；
	static final int COUNT_PER_TRANS = 100; // 每个事务最多执行100条
	
	private static boolean asyTaskRunning = false;
	// Map: entity=ZEntity, callback=ZCallback, daoClass=dao.getClass.getName
	private static List<Map<String, Object>> _entityList = Collections.synchronizedList(new LinkedList<Map<String, Object>>());
	
	public static void addEntity(ZEntity entity, ZDao dao, ZCallback callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_ENTITY, entity);
		map.put(KEY_CALLBACK, callback);

		String daoClass = CLZ_ZDAO; // 默认
		if (dao != null) {
			daoClass = dao.getClass().getName();
		}
		
		map.put(KEY_DAO_CLASS, daoClass);
		
		_entityList.add(map);
		
		if (!asyTaskRunning) {
			startTask();
		}
	}
	
	public static void startTask() {

		// 防止重复执行
		if (asyTaskRunning) {
			return;
		}

		Thread thread = new Thread() {
			@Override  
			public void run() {
				while(true) {

					try {
						int size = _entityList.size();
						logger.info(String.format("_entityList.size=%d", size));
						if (size > COUNT_PER_TRANS) {
							size = COUNT_PER_TRANS;
						}
						logger.info(String.format("todo size=%d", size));
						
						if ( size == 0) {
							sleep(IDLE_SLEEP);
							logger.info(String.format("idle sleep:%d", IDLE_SLEEP));
						} else {
							doSave(size);
							sleep(REST_SLEEP); // 每个事务执行完休息1秒
							logger.info(String.format("rest sleep:%d", REST_SLEEP));
							
						}
					} catch(Exception e) {
						logger.error(e);
					}
					
				}
			}
		};

		// 考虑线程同步，确保不重复
		if (!asyTaskRunning) {
			asyTaskRunning = true;
			thread.start();
		}
	}

	private static void doSaveDb(int size, EntityManager em) {

		logger.info(String.format("size=%d", size));
		
		List<Map<String, Object>> todoList = new ArrayList<Map<String, Object>>();
		
		for (int i=0 ; i< size; i++) {
			todoList.add(_entityList.get(i));
		}
		for (int i=0 ; i< size; i++) {
			_entityList.remove(0);
		}
		
		// start
		Map<String, ZDao> daoMap = new HashMap<String, ZDao>();
		daoMap.put(CLZ_ZDAO, new ZDao(em));
		
		// 生成其它dao实例
		for (int i=0 ; i< size; i++) {
			Map<String, Object> map = todoList.get(i);
			String daoClass = (String)map.get(KEY_DAO_CLASS);
			if (!daoMap.containsKey(daoClass)) {
				ZDao dao;
				try {
					dao = (ZDao)Class.forName(daoClass).getConstructor(EntityManager.class).newInstance(em);
					daoMap.put(daoClass, dao);
				} catch (Exception e) {
					logger.error(e);
					daoMap.put(daoClass, daoMap.get(CLZ_ZDAO)); // 使用默认dao
				}
			}
		}
		
		for (int i=0 ; i< size; i++) {
			Map<String, Object> map = todoList.get(i);
			ZEntity entity = (ZEntity)map.get(KEY_ENTITY);
			ZCallback callback = (ZCallback)map.get(KEY_CALLBACK);
			String daoClass = (String)map.get(KEY_DAO_CLASS);
			try {
				daoMap.get(daoClass).save(entity);
				if (callback != null) {
					callback.onSuccess((Long)entity.getPkInfo()[1]);
				}
				
				logger.info(String.format("entity saved. index=%d/%d, detail={%s}", i+1, size, entity.dump()));
				logger.info(String.format("daoClass=%s", daoClass));
			} catch(Exception e) {
				logger.error(e);
			}
		}
		
		logger.info(String.format("%d entities saved", size));
	}
	
	private  static void doSave(final int size) {
		
		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {
				
				doSaveDb(size, em);
			}
			
		}.run();

	}
}
