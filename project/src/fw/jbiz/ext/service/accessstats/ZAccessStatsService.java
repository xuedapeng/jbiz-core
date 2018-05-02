package fw.jbiz.ext.service.accessstats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import fw.jbiz.ZObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZDbProcessor;

public abstract class ZAccessStatsService extends ZObject {

	static Logger logger = Logger.getLogger(ZAccessStatsService.class);
	
	// 待办列表池数量
	private static final int POOL_SIZE = 10;
	
	private static List<List<ZAccessBean>> mTodoListPool = new ArrayList<List<ZAccessBean>>();
	private static List<ZAccessBean> _tobeSaveList = new ArrayList<ZAccessBean>();
	private static int[][] mPoolUseInfo = new int[POOL_SIZE][2]; // 使用次数，当前缓存个数
	private static int lastHit = -1;
	private static int totalCount = 0; // 启动后的调用总数
	
	private static boolean statsTaskRunning = false;

	static final long IDLE_SLEEP = 10*1000;// 休眠10秒；
	static final long REST_SLEEP = 1*1000;// 每个事务之间休息1秒；
	static final int COUNT_PER_TRANS = 100; // 每个事务最多执行100条
	
	static {
		// 初始化[使用中]数组，待处理List池
		for (int i=0; i< POOL_SIZE; i++) {
			mPoolUseInfo[i][0] = 0;
			mPoolUseInfo[i][1] = 0;
			mTodoListPool.add(
					Collections.synchronizedList(new LinkedList<ZAccessBean>())); // LinkedList 增减效率高
		}
	}
	
	public void startService() {

		// 防止重复执行
		if (statsTaskRunning) {
			return;
		}

		Thread thread = new Thread() {
			@Override  
			public void run() {
				logger.info(String.format("listenForSaveQueues start"));

				while(true) {
					try {

						logger.info("start while:" + printPoolUseInfo());
						
						int size = retrieveTobeSaveList();
						logger.info(String.format("_tobeSaveList.size=%d", size));
						if (size > COUNT_PER_TRANS) {
							size = COUNT_PER_TRANS;
						}
						logger.info(String.format("tobeSave size=%d", size));

						if ( size == 0) {
							sleep(IDLE_SLEEP);
							logger.info(String.format("idle sleep:%d", IDLE_SLEEP));
						} else {
							saveQueues(size);
							sleep(REST_SLEEP); // 每个事务执行完休息1秒
							logger.info(String.format("rest sleep:%d", REST_SLEEP));
							
						}

						logger.info("end while:" +printPoolUseInfo());
						
					} catch(Exception e) {

						logger.error(e);
					}
				}
				
			}
		};

		// 考虑线程同步，确保不重复
		if (!statsTaskRunning) {
			statsTaskRunning = true;
			thread.start();
		}
	}
	
	private int retrieveTobeSaveList() {

		int tobeSize = _tobeSaveList.size();
		if ( tobeSize > 0) {
			return tobeSize;
		}
		
		// 对待办列表池中的所有待办列表，循环处理
		for (int poolIdx = 0; poolIdx < POOL_SIZE; poolIdx++) {

			List<ZAccessBean> todoList = mTodoListPool.get(poolIdx);
			int size = todoList.size();
			
			mPoolUseInfo[poolIdx][1] = size; // 使用次数
			totalCount = totalCount + size;
			
			if (size == 0) {
				continue;
			}
			
			// 释放线程同步对象
			for (int todoIdx=0; todoIdx<size; todoIdx++) {
				_tobeSaveList.add(todoList.get(todoIdx));
			}
			for (int todoIdx=0; todoIdx<size; todoIdx++) {
				todoList.remove(0);
			}
		}
		
		return _tobeSaveList.size();
	}
		
	private void saveQueues(int size) {
		
		List<ZAccessBean> tobeSaveList = new ArrayList<ZAccessBean>();
		
		for (int i=0; i< size; i++) {
			tobeSaveList.add(_tobeSaveList.get(i));
		}
		for (int i=0; i< size; i++) {
			_tobeSaveList.remove(0);
		}

		final List<ZAccessBean>  tobeSaveListForProcessor = tobeSaveList;
		
		// 保存操作
		new ZDbProcessor() {
			@Override
			public void execute(EntityManager em) {

				for (ZAccessBean accessBean: tobeSaveListForProcessor) {
					doSave(accessBean, em);
				}
			}
		}.run();
		
	}
	/**
	 * 
	 * @param item
	 */
	public static void addItemToQueue(ZAccessBean item) {
		getTodoListFromPool().add(item);
	}
	
	// 从待办List池中获取待办List
	private synchronized static List<ZAccessBean> getTodoListFromPool() {
		lastHit = (lastHit < POOL_SIZE-1? lastHit+1 : 0); // 循环使用pool中的待办列表
		mPoolUseInfo[lastHit][0]++; // 使用次数
		
		return mTodoListPool.get(lastHit);
	}
	
	
	
	
	public abstract void doSave(ZAccessBean accessBean, EntityManager em);

	protected static String getState(ZSimpleJsonObject res) {
		
		String json = res.toString();
		JSONObject jsonObj = JSONObject.fromObject(json);
		String status = "";
		if (jsonObj.containsKey("status")) {
			status = jsonObj.getString("status");

		}
		return status;
	}
	
	private static String printPoolUseInfo(){
		
		int lastCount = 0;
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<mPoolUseInfo.length; i++) {
			sb.append(String.format("[%d: %d,%d] ", i, mPoolUseInfo[i][0], mPoolUseInfo[i][1]));
			lastCount += mPoolUseInfo[i][1];
		}
		
		String result = String.format("{ mPoolUseInfo: [total: %d, last: %d] %s }", totalCount, lastCount, sb.toString());
	
		return result;
	}

}
