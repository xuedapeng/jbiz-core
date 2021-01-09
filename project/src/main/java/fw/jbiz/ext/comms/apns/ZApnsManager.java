package fw.jbiz.ext.comms.apns;

import java.util.List;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.apns.bean.ZApnsBean;
import fw.jbiz.ext.comms.apns.bean.ZApnsResult;
import fw.jbiz.ext.comms.apns.interfaces.IApnsProvider;

public class ZApnsManager extends ZObject {

	static Logger logger = Logger.getLogger(ZApnsManager.class);
	
	private static final String PROP_PROVIDER_CLASS = "apns_provider_class";
	private static IApnsProvider mProvider = null;
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	public static void setConfig(IConfig config) {
		mConfig = config;
	}
	
	// 设置提供商的实现类
	public static void setProvider(IApnsProvider provider) {
		mProvider = provider;
	}
	
	private static IApnsProvider getProvider()  {
		if (mProvider == null) {
			String className = mConfig.getProp(PROP_PROVIDER_CLASS);
			try {
				mProvider = (IApnsProvider)Class.forName(className).newInstance();
				mProvider.setConfig(mConfig);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				
				logger.error(trace(e));
			}
		}
		
		return mProvider;
	}
	
	// 批量 同步
	public static List<ZApnsResult> send(List<ZApnsBean> apnsBeanList) {
		 List<ZApnsResult> resultList = getProvider().send(apnsBeanList);
		 return resultList;
	}

	// 批量 异步
	public static List<ZApnsResult> asyncSend(final List<ZApnsBean> apnsBeanList) {
		
		final IApnsProvider thisProvider = getProvider();
		
		new Thread(new Runnable() {
			public void run() {
				thisProvider.send(apnsBeanList);
			}
		}).start();
		
		 List<ZApnsResult> resultList = getProvider().getInactiveDevices();
		 return resultList;
	}
	

}
