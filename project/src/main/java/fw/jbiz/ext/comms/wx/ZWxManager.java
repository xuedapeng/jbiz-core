package fw.jbiz.ext.comms.wx;

import java.util.List;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.wx.bean.ZWxBean;
import fw.jbiz.ext.comms.wx.bean.ZWxResult;
import fw.jbiz.ext.comms.wx.interfaces.IWxProvider;

public class ZWxManager extends ZObject {

	static Logger logger = Logger.getLogger(ZWxManager.class);
	
	public static final String URL_TEMPLATE_SEND = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	public static final String URL_CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	public static final String URL_ACCESSTOKEN_GET = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

	private static final String PROP_PROVIDER_CLASS = "wx_provider_class";
	private static IWxProvider mProvider = null;
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	public static void setConfig(IConfig config) {
		mConfig = config;
	}
	
	// 设置提供商的实现类
	public static void setProvider(IWxProvider provider) {
		mProvider = provider;
	}
	
	public static IWxProvider getProvider()  {
		if (mProvider == null) {
			String className = mConfig.getProp(PROP_PROVIDER_CLASS);
			try {
				mProvider = (IWxProvider)Class.forName(className).newInstance();
				mProvider.setConfig(mConfig);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				
				logger.error(trace(e));
			}
		}
		
		return mProvider;
	}

	// 批量、同步
	public static List<ZWxResult> send(List<ZWxBean> wxBeanList) {
		
		List<ZWxResult> resultList = getProvider().send(wxBeanList);
		return resultList;
	}

	// 单个、同步
	public static ZWxResult send(ZWxBean wxBean) {
		
		ZWxResult result = getProvider().send(wxBean);
		
		return result;
	}
	
	// 批量、异步
	public static void sendAsync(final List<ZWxBean> wxBeanList) {

		final IWxProvider thisProvider = getProvider();
		
		new Thread(new Runnable() {
			public void run() {
				thisProvider.send(wxBeanList);
			}
		}).start();
		
	}
	
	// 单个、异步
	public static void sendAsync(final ZWxBean wxBean) {

		final IWxProvider thisProvider = getProvider();
		
		new Thread(new Runnable() {
			public void run() {
				thisProvider.send(wxBean);
			}
		}).start();
		
	}
	
	// 获取accessToken
	public static String getAccessToken() {
		String accessToken = getProvider().getAccessToken();
		return accessToken;
	}
	
}
