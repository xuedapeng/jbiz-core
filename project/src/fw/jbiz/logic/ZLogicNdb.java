package fw.jbiz.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.logic.interfaces.IResponseObject;


public abstract class ZLogicNdb extends ZLogicTop {
	
	static Logger logger = Logger.getLogger(ZLogicNdb.class);

	protected List<ZLogicNdbFilter> logicFilterList = new ArrayList<ZLogicNdbFilter>();
	
	@Override
	protected IResponseObject res() {
		return new ZGsonObject();
	}
	
	@Override
   protected String processLogic(ZLogicParam logicParam, IResponseObject res) {
	  
	   try {

		   logger.info("processLogic 4");
		   
		   // 执行前置过滤器
		   if (!doLogicFilterChainsBefore(logicParam, res)) {
			   return breakProcess(logicParam, res);
		   }
		   logger.info("processLogic 5");

		   if (!validate(logicParam, res)) {
			   return breakProcess(logicParam, res);
		   }
		   logger.info("processLogic 6");

		   // 身份认证
		   if (!auth(logicParam, res)) {
			   return breakProcess(logicParam, res);
		   }
		   logger.info("processLogic 7");

		   
		   // 执行主逻辑
		   execute(logicParam, res);
		   String resStr = res.toString();
		   
		   logger.info("processLogic 8");
		   // 执行后置过滤器
		   doLogicFilterChainsAfter(logicParam, res);
		   logger.info("processLogic 9");
		   
		   return resStr;
		   
	   } catch (Exception e) {
		   logger.error(trace(e));
		   logger.info("processLogic 13");
		   setErrorMessage(res, e);
		   return res.toString();
	   } 
   }

   protected void addFilter(ZLogicNdbFilter filter) {
	   this.logicFilterList.add(filter);
   }

   
   private boolean doLogicFilterChainsBefore(ZLogicParam logicParam, IResponseObject res) {
	   for (ZLogicNdbFilter filter: this.logicFilterList) {
		   if (!filter.doFilterBefore(logicParam, res)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   protected boolean doLogicFilterChainsAfter(ZLogicParam logicParam, IResponseObject res) {
	   for (ZLogicNdbFilter filter: this.logicFilterList) {
		   if (!filter.doFilterAfter(logicParam, res)) {
			   return false;
		   }
	   }
	   
	   return true;
   }
   
   private String breakProcess(ZLogicParam logicParam, IResponseObject res) {

	   try {
		   logger.info("processLogic 21");
		   // 执行后置过滤器
		   doLogicFilterChainsAfter(logicParam, res);
		   logger.info("processLogic 22");
		   
	   } catch(Exception e) {
		   logger.error(trace(e));
	   }
	   
	   return res.toString();
	   
   }
   
   // end add

   // 身份认证
   protected abstract boolean auth(ZLogicParam logicParam, IResponseObject res) throws Exception;
	
   protected abstract boolean execute(ZLogicParam logicParam, IResponseObject res) throws Exception;

   protected abstract boolean validate(ZLogicParam logicParam, IResponseObject res) throws Exception;
   
}
