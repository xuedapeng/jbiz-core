package fw.jbiz.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.logic.interfaces.IResponseObject;


public abstract class ZLogicNdb extends ZObject {
	
	static Logger logger = Logger.getLogger(ZLogicNdb.class);

	private List<ZLogicNdbFilter> logicFilterList = new ArrayList<ZLogicNdbFilter>();
	private List<ZSystemFilter> systemFilterList = new ArrayList<ZSystemFilter>();

	protected void setErrorMessage(IResponseObject res){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		res.add("exception", "");
	}
	
	protected void setErrorMessage(IResponseObject res, String errDetail){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		res.add("exception", errDetail);
	}
	protected void setErrorMessage(IResponseObject res, Exception e){
		res.clear();
		res.add("status", -1);
		res.add("msg", "内部异常");
		if ("true".equals(ZSystemConfig.getProperty("dev_error_detail"))) {
			res.add("exception", trace(e));
		}
	}
	
	protected IResponseObject res() {
		return new ZGsonObject();
	}

	public final String process(ZLogicParam logicParam) {
		
		IResponseObject res = res();
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsBefore(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		String result = this.processLogic(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsAfter(logicParam, res);
		
		return result;
	}
	
   private String processLogic(ZLogicParam logicParam, IResponseObject res) {
	  
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

   protected void addFilter(ZSystemFilter filter) {
	   this.systemFilterList.add(filter);
   }
   
   private boolean doLogicFilterChainsBefore(ZLogicParam logicParam, IResponseObject res) {
	   for (ZLogicNdbFilter filter: this.logicFilterList) {
		   if (!filter.doFilterBefore(logicParam, res)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   private void doSystemFilterChainsBefore(ZLogicParam logicParam, IResponseObject res) {
	   for (ZSystemFilter filter: this.systemFilterList) {
		   // 系统过滤器不处理异常，异常发生时不影响业务
		   try {
			   filter.doFilterBefore(logicParam, (ZGsonObject)res);
		   } catch(Exception e) {
			   logger.error(trace(e));
		   }
	   }
   }
   private boolean doLogicFilterChainsAfter(ZLogicParam logicParam, IResponseObject res) {
	   for (ZLogicNdbFilter filter: this.logicFilterList) {
		   if (!filter.doFilterAfter(logicParam, res)) {
			   return false;
		   }
	   }
	   
	   return true;
   }

   private void doSystemFilterChainsAfter(ZLogicParam logicParam, IResponseObject res) {
	   for (ZSystemFilter filter: this.systemFilterList) {
		   // 系统过滤器不处理异常，异常发生时不影响业务
		   try {
			   filter.doFilterAfter(logicParam, (ZGsonObject)res);
		   } catch(Exception e) {
			   logger.error(trace(e));
		   }
	   }
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
