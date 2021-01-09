package fw.jbiz.ext.comms.sms;

import java.util.List;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.sms.bean.ZSmsBean;
import fw.jbiz.ext.comms.sms.bean.ZSmsResult;
import fw.jbiz.ext.comms.sms.interfaces.ISmsProvider;

public class ZSmsManager extends ZObject {

	static Logger logger = Logger.getLogger(ZSmsManager.class);
	
	private static final String PROP_PROVIDER_CLASS = "sms_provider_class";
	private static ISmsProvider mProvider = null;
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	public static void setConfig(IConfig config) {
		mConfig = config;
	}
	
	// 设置提供商的实现类
	public static void setProvider(ISmsProvider provider) {
		mProvider = provider;
	}
	
	private static ISmsProvider getProvider()  {
		if (mProvider == null) {
			String className = mConfig.getProp(PROP_PROVIDER_CLASS);
			try {
				mProvider = (ISmsProvider)Class.forName(className).newInstance();
				mProvider.setConfig(mConfig);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				
				logger.error(trace(e));
			}
		}
		
		return mProvider;
	}

	// 批量、同步
	public static List<ZSmsResult> send(List<ZSmsBean> smsBeanList) {
		
		List<ZSmsResult> resultList = getProvider().send(smsBeanList);
		return resultList;
	}

	// 单个、同步
	public static ZSmsResult send(ZSmsBean smsBean) {
		
		ZSmsResult result = getProvider().send(smsBean);
		
		return result;
	}
	
	// 批量、异步
	public static void sendAsync(final List<ZSmsBean> smsBeanList) {

		final ISmsProvider thisProvider = getProvider();
		
		new Thread(new Runnable() {
			public void run() {
				thisProvider.send(smsBeanList);
			}
		}).start();
		
	}
	
	// 单个、异步
	public static void sendAsync(final ZSmsBean smsBean) {

		final ISmsProvider thisProvider = getProvider();
		
		new Thread(new Runnable() {
			public void run() {
				thisProvider.send(smsBean);
			}
		}).start();
		
	}
	
}
