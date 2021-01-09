package fw.jbiz.logic;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.jpa.ZJpaHelper;
import fw.jbiz.logic.interfaces.IResponseObject;


public abstract class ZLogic extends ZLogicTop {
	
	static Logger logger = Logger.getLogger(ZLogic.class);
			
	// start add  2015.7.23 by xuedp [过滤器]
	private List<ZLogicFilter> logicFilterList = new ArrayList<ZLogicFilter>();
	// end add 

	@Override
	protected ZSimpleJsonObject res() {
		return new ZGsonObject();
	}
	
	protected final EntityManager getEntityManager() {
		return ZJpaHelper.getEntityManager(getPersistenceUnitName());
	}

	@Override
   protected String processLogic(ZLogicParam logicParam, IResponseObject res) {
	   logger.info("processLogic 1");
	   EntityManager em = getEntityManager();
	   logger.info("processLogic 2");

	   try {

		   logger.info("processLogic 3");
		   ZJpaHelper.beginTransaction(em);
		   logger.info("processLogic 4");
		   
		   // 执行前置过滤器
		   if (!doLogicFilterChainsBefore(logicParam, (ZSimpleJsonObject)res, em)) {
			   return breakProcess(logicParam,  (ZSimpleJsonObject)res, em);
		   }
		   logger.info("processLogic 5");

		   if (!validate(logicParam,  (ZSimpleJsonObject)res, em)) {
			   return breakProcess(logicParam,  (ZSimpleJsonObject)res, em);
		   }
		   logger.info("processLogic 6");

		   // 身份认证
		   if (!auth(logicParam,  (ZSimpleJsonObject)res, em)) {
			   return breakProcess(logicParam,  (ZSimpleJsonObject)res, em);
		   }
		   logger.info("processLogic 7");

		   
		   // 执行主逻辑
		   boolean result = execute(logicParam,  (ZSimpleJsonObject)res, em);
		   String resStr = res.toString();

		   logger.info("processLogic 8");
		   // 执行后置过滤器
		   doLogicFilterChainsAfter(logicParam,  (ZSimpleJsonObject)res, em);
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

   
   private boolean doLogicFilterChainsBefore(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {
	   for (ZLogicFilter filter: this.logicFilterList) {
		   if (!filter.doFilterBefore(logicParam, res, em)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   private boolean doLogicFilterChainsAfter(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {
	   for (ZLogicFilter filter: this.logicFilterList) {
		   if (!filter.doFilterAfter(logicParam, res, em)) {
			   return false;
		   }
	   }
	   
	   return true;
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
