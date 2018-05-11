package fw.jbiz.logic;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.jpa.ZJpaHelper;


public abstract class ZLogic extends ZObject {
	
	static Logger logger = Logger.getLogger(ZLogic.class);
			
	// start add  2015.7.23 by xuedp [过滤器]
	private List<ZLogicFilter> logicFilterList = new ArrayList<ZLogicFilter>();
	private List<ZSystemFilter> systemFilterList = new ArrayList<ZSystemFilter>();
	
	
	// end add 

	protected void setErrorMessage(ZSimpleJsonObject res){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		res.add("exception", "");
	}
	
	protected void setErrorMessage(ZSimpleJsonObject res, String errDetail){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		res.add("exception", errDetail);
	}
	protected void setErrorMessage(ZSimpleJsonObject res, Exception e){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		if ("true".equals(ZSystemConfig.getProperty("dev_error_detail"))) {
			res.add("exception", trace(e));
		}
	}
	
	protected ZSimpleJsonObject res() {
		return new ZGsonObject();
	}
	
	protected final EntityManager getEntityManager() {
		return ZJpaHelper.getEntityManager(getPersistenceUnitName());
	}

	public final String process(ZLogicParam logicParam) {
		
		ZSimpleJsonObject res = res();
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsBefore(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		String result = this.processLogic(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsAfter(logicParam, res);
		
		return result;
	}
	
   private String processLogic(ZLogicParam logicParam, ZSimpleJsonObject res) {
	   logger.info("processLogic 1");
	   EntityManager em = getEntityManager();
	   logger.info("processLogic 2");

	   try {

		   logger.info("processLogic 3");
		   ZJpaHelper.beginTransaction(em);
		   logger.info("processLogic 4");
		   
		   // 执行前置过滤器
		   if (!doLogicFilterChainsBefore(logicParam, res, em)) {
			   return breakProcess(logicParam, res, em);
		   }
		   logger.info("processLogic 5");

		   if (!validate(logicParam, res, em)) {
			   return breakProcess(logicParam, res, em);
		   }
		   logger.info("processLogic 6");

		   // 身份认证
		   if (!auth(logicParam, res, em)) {
			   return breakProcess(logicParam, res, em);
		   }
		   logger.info("processLogic 7");

		   
		   // 执行主逻辑
		   boolean result = execute(logicParam, res, em);
		   String resStr = res.toString();
//		   resStr = ZDao.strReplace(resStr);
		   
		   // unicode编码转换成java字符
//		   resStr = StringEscapeUtils.unescapeJava(resStr); // 此处不能还原，客户端取出value后，需要再做转换

		   logger.info("processLogic 8");
		   // 执行后置过滤器
		   doLogicFilterChainsAfter(logicParam, res, em);
		   logger.info("processLogic 9");
		   
		   if (result) {
			   logger.info("processLogic 10");
			   ZJpaHelper.commit(em);
		   } else {
			   logger.info("processLogic 11");
			   ZJpaHelper.rollback(em);
		   }

		   logger.info("processLogic 12");
		   return resStr;
		   
	   } catch (Exception e) {
		   logger.error(trace(e));
		   logger.info("processLogic 13");
		   try {
			   ZJpaHelper.rollback(em); // 可能em事物没有启动成功
		   } catch (Exception e2) {
			   logger.error(trace(e2));
		   }
		   
		   setErrorMessage(res, e);
		   return res.toString();
	   } finally {
		   logger.info("processLogic 14");
		   ZJpaHelper.closeEntityManager(em);
	   }
   }

	// start add  2015.7.23 by xuedp [过滤器]
   protected void addFilter(ZLogicFilter filter) {
	   this.logicFilterList.add(filter);
   }

   protected void addFilter(ZSystemFilter filter) {
	   this.systemFilterList.add(filter);
   }
   
   private boolean doLogicFilterChainsBefore(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {
	   for (ZLogicFilter filter: this.logicFilterList) {
		   if (!filter.doFilterBefore(logicParam, res, em)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   private void doSystemFilterChainsBefore(ZLogicParam logicParam, ZSimpleJsonObject res) {
	   for (ZSystemFilter filter: this.systemFilterList) {
		   // 系统过滤器不处理异常，异常发生时不影响业务
		   try {
			   filter.doFilterBefore(logicParam, res);
		   } catch(Exception e) {
			   logger.error(trace(e));
		   }
	   }
   }
   private boolean doLogicFilterChainsAfter(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {
	   for (ZLogicFilter filter: this.logicFilterList) {
		   if (!filter.doFilterAfter(logicParam, res, em)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   private void doSystemFilterChainsAfter(ZLogicParam logicParam, ZSimpleJsonObject res) {
	   for (ZSystemFilter filter: this.systemFilterList) {
		   // 系统过滤器不处理异常，异常发生时不影响业务
		   try {
			   filter.doFilterAfter(logicParam, res);
		   } catch(Exception e) {
			   logger.error(trace(e));
		   }
	   }
   }
   
   private String breakProcess(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {

	   try {
		   logger.info("processLogic 21");
		   // 执行后置过滤器
		   doLogicFilterChainsAfter(logicParam, res, em);
		   logger.info("processLogic 22");
		   
		   ZJpaHelper.rollback(em);
	   } catch(Exception e) {
		   logger.error(trace(e));
	   } finally {
		   // ZJpaHelper.closeEntityManager(em);  process 中close
	   }
	   
	   return res.toString();
	   
   }
   
   // end add

   // 身份认证
   protected abstract boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception;
	
   protected abstract boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception;

   protected abstract boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception;
   
   protected abstract String getPersistenceUnitName();
}
