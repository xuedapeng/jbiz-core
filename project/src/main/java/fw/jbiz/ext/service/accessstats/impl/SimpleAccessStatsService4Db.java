package fw.jbiz.ext.service.accessstats.impl;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.db.ZDao;
import fw.jbiz.ext.service.accessstats.ZAccessBean;
import fw.jbiz.ext.service.accessstats.ZAccessStatsService;
import fw.jbiz.logic.ZLogicParam;

@SuppressWarnings("unchecked")
public class SimpleAccessStatsService4Db extends ZAccessStatsService {

	static Logger logger = Logger.getLogger(SimpleAccessStatsService4Db.class);
	
	static {
		
		String tableName = ZSystemConfig.getProperty("access_stats_service_table");

		logger.info(String.format("tableName=[%s]", tableName));
		
		if (StringUtils.isNotEmpty(tableName)) {
			
			// 动态修改表名注解
			Table table = (Table) SimpleAccessStatsEntity.class.getAnnotation(Table.class);
			InvocationHandler h = Proxy.getInvocationHandler(table);
			try {
				Field hField = h.getClass().getDeclaredField("memberValues");
				hField.setAccessible(true);
				@SuppressWarnings("rawtypes")
				Map memberValues = (Map) hField.get(h);

				logger.info(String.format("tableName_old=[%s]", memberValues.get("name")));
				memberValues.put("name", tableName);
				logger.info(String.format("tableName_new=[%s]", memberValues.get("name")));
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error(trace(e));
			}
		}
	}
	@Override
	public void doSave(ZAccessBean accessBean, EntityManager em) {

		ZLogicParam myParam = (ZLogicParam) accessBean.getLogicParam();
		SimpleAccessStatsEntity mAccessstatics = new SimpleAccessStatsEntity();
		
		mAccessstatics.setClient_ip(accessBean.getReqClientIP());
		mAccessstatics.setUser_agent(accessBean.getReqUserAgent());
		mAccessstatics.setServer_ip(accessBean.getReqServerIP());
		mAccessstatics.setApi_path(accessBean.getReqApiPath());
		mAccessstatics.setUserId(myParam.getUserId());

		if (myParam.dumpParams().length() > 65535) {
			String p = myParam.dumpParams().substring(0, 65534);
			mAccessstatics.setParams(p);
		} else {
			mAccessstatics.setParams(myParam.dumpParams());
		}
		mAccessstatics.setStarttime(accessBean.getStarttime());
		
		String domainId = ZSystemConfig.getProperty("application_id");
		mAccessstatics.setDomainId(domainId);

		mAccessstatics.setEndtime(accessBean.getEndtime());
		mAccessstatics.setResult_status(getState(accessBean.getResultResource()));

		mAccessstatics.setElapsedTime(accessBean.getEndtime().getTime() - accessBean.getStarttime().getTime());
		
		ZDao aDao = new ZDao(em);

		aDao.save(mAccessstatics);
		
	}
}
