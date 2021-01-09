package fw.jbiz.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.service.accessstats.ZAccessStatsService;
import fw.jbiz.ext.service.accessstats.ZStatsFilter;
import fw.jbiz.logic.interfaces.IResponseObject;

public abstract class ZLogicTop extends ZObject {

	static Logger logger = Logger.getLogger(ZLogicTop.class);

	protected List<ZSystemFilter> systemFilterList = new ArrayList<ZSystemFilter>();
	
	// 如果定义了access_stats_service_provider_class，则启动service
	private static ZAccessStatsService _accessStatsService = null;
	static {
		String serviceClass = ZSystemConfig.getProperty("access_stats_service_provider_class");

		logger.info(String.format("serviceClass=[%s]", serviceClass));
		
		if (StringUtils.isNotEmpty(serviceClass)) {
			try {
				_accessStatsService = (ZAccessStatsService)Class.forName(serviceClass).newInstance();
				_accessStatsService.startService();

				logger.info("_accessStatsService started");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.error(trace(e));
			}
			
		}
	}

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

	
	public final String process(ZLogicParam logicParam) {
		
		IResponseObject res = res();

		// 以下调用，确保不会抛出异常
		this.addAccessStats();
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsBefore(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		String result = this.processLogic(logicParam, res);
		
		// 以下调用，确保不会抛出异常
		this.doSystemFilterChainsAfter(logicParam, res);
		
		return result;
	}

	protected void addFilter(ZSystemFilter filter) {
		this.systemFilterList.add(filter);
	}
	   
	protected void doSystemFilterChainsBefore(ZLogicParam logicParam, IResponseObject res) {
	   for (ZSystemFilter filter: this.systemFilterList) {
		   // 系统过滤器不处理异常，异常发生时不影响业务
		   try {
			   filter.doFilterBefore(logicParam, (ZGsonObject)res);
		   } catch(Exception e) {
			   logger.error(trace(e));
		   }
	   }
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


	protected abstract IResponseObject res();
	protected abstract String processLogic(ZLogicParam logicParam, IResponseObject res);

	private void addAccessStats() {
		
		if (_accessStatsService != null) {

			addFilter(new ZStatsFilter());
			logger.info("ZStatsFilter added.");
		}
		
	}
}
