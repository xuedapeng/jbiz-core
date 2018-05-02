package fw.jbiz.ext.comms.apns.impl.notnoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.apns.bean.ZApnsBean;
import fw.jbiz.ext.comms.apns.bean.ZApnsResult;
import fw.jbiz.ext.comms.apns.interfaces.IApnsProvider;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.olib.com.notnoop.apns.APNS;
import fw.olib.com.notnoop.apns.ApnsService;
import fw.olib.com.notnoop.apns.ApnsServiceBuilder;

public class ZNotnoopApnsProvider extends ZObject implements IApnsProvider {

	static Logger logger = Logger.getLogger(ZNotnoopApnsProvider.class);
	
	private static final String PROP_CERT_PATH = "apns_cert_path";
	private static final String PROP_CERT_PWD = "apns_cert_pwd";
	private static final String PROP_PRODUCT = "apns_product";
	
	private static String mApnsCertificatePath = null;
	private static String mApnsCertificatePassword = null;
	private static Boolean mApnsProduction = null;
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	private static List<String> mInactiveDevices = Collections.synchronizedList(new ArrayList<String>());
	
	public ZNotnoopApnsProvider() {
		logger.info(getProviderInfo());
	}
	
	private static String getApnsCertificatePath() {
		if (mApnsCertificatePath == null) {
			mApnsCertificatePath = mConfig.getProp(PROP_CERT_PATH);
		}
		
		return mApnsCertificatePath;
	}
	
	private static String getApnsCertificatePassword() {
		
		if (mApnsCertificatePassword == null) {
			mApnsCertificatePassword = mConfig.getProp(PROP_CERT_PWD);
		}
		
		return mApnsCertificatePassword;
	}
	
	private static boolean getApnsProduction() {
		
		if (mApnsProduction == null) {
			mApnsProduction = mConfig.getProp(PROP_PRODUCT).equals("yes")? true: false;
		}
		
		return mApnsProduction;
	}
	
	// 可以设置配置，如果没有设置，则采用系统配置。
	public void setConfig(IConfig config) {
		mConfig = config;
		
	}
	
	// 同步发送,返回失败的token
	@Override
	public List<ZApnsResult> send(List<ZApnsBean> apnsBeanList){

		sendMsgNotnoop(apnsBeanList);
		List<ZApnsResult> resultList = getInactiveDevices();
		
		return resultList;
		
	};
	
	// 获取失效的token
	@Override
	public List<ZApnsResult> getInactiveDevices() {
		
		List<String> cloneList = new ArrayList<String>();
		for (String token: mInactiveDevices) {
			cloneList.add(token);
		}
		
		mInactiveDevices.clear();

		List<ZApnsResult> resultList = new ArrayList<ZApnsResult>();
		for (String invalidToken: cloneList) {
			ZApnsResult result = new ZApnsResult();
			result.setInvalidToken(invalidToken);
			resultList.add(result);
		}
		
		return resultList;
		
	}

	// java notnoop 实现
	private void sendMsgNotnoop(List<ZApnsBean> apnsBeanList) {

        List<String> failTokenList = new ArrayList<String>();
        
        
        ApnsServiceBuilder builder = APNS.newService()
			    .withCert(getApnsCertificatePath(), getApnsCertificatePassword());
        
        if (getApnsProduction()) {
        	builder.withProductionDestination();
        } else {
        	builder.withSandboxDestination();
        }
        
		ApnsService service = builder.build();

		for (ZApnsBean apnsBean: apnsBeanList) {
			
			String token = apnsBean.getDeviceToken();
			String title = apnsBean.getAlertTitle();
	        String alert = apnsBean.getAlertBody();
	        int badge = apnsBean.getBadge();
	        String sound = apnsBean.getSound();
	        String category = apnsBean.getCategory();
	        Map<String, Object> customInfo = apnsBean.getCustomInfo();
	        
			String payload = APNS.newPayload()
					.alertTitle(title)
					.alertBody(alert)
					.badge(badge)
					.sound(sound)
					.category(category)
					.customField("customInfo", customInfo)
					.build();
			
			service.push(token, payload);
			
			logger.info(payload);
		}
		
		Map<String, Date> inactiveDevices = service.getInactiveDevices();
		for (String deviceToken : inactiveDevices.keySet()) {
		    failTokenList.add(deviceToken);
		    logger.info("inactiveDevices:" + deviceToken);
		}
		
		logger.info("inactiveDevices size:" + inactiveDevices.size());
		
		mInactiveDevices.addAll(failTokenList);
	}

	@Override
	public String getProviderInfo() {

		ZSimpleJsonObject res = new ZGsonObject();
		res.add("provider_class", this.getClass().getName());
		res.add("version", "v1.0.20150921");
		res.add("author", "notnoop.com");
		res.add("website", "https://github.com/notnoop/java-apns");
		return res.toString();
	}

}
