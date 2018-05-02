package fw.jbiz.ext.comms.apns.impl.javapns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.apns.bean.ZApnsBean;
import fw.jbiz.ext.comms.apns.bean.ZApnsResult;
import fw.jbiz.ext.comms.apns.interfaces.IApnsProvider;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public class ZJavaPnsApnsProvider extends ZObject implements IApnsProvider {

	static Logger logger = Logger.getLogger(ZJavaPnsApnsProvider.class);
	
	private static final String PROP_CERT_PATH = "apns_cert_path";
	private static final String PROP_CERT_PWD = "apns_cert_pwd";
	private static final String PROP_PRODUCT = "apns_product";
	
	private static String mApnsCertificatePath = null;
	private static String mApnsCertificatePassword = null;
	private static Boolean mApnsProduction = null;
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	private static List<String> mInactiveDevices = Collections.synchronizedList(new ArrayList<String>());

	public ZJavaPnsApnsProvider() {
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

		for (ZApnsBean apnsBean: apnsBeanList) {
			sendMsg(apnsBean);
		}

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
	
	// javaPns 实现
	private void sendMsg(ZApnsBean apnsBean) {

		String token = apnsBean.getDeviceToken();

        String alert = apnsBean.getAlertBody();
        int badge = apnsBean.getBadge();
        String sound = apnsBean.getSound();


        List<String> failTokenList = new ArrayList<String>();
        try
        {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(alert);
            payLoad.addBadge(badge); 
            if (!StringUtils.isEmpty(sound)) {
                payLoad.addSound(sound);
            }
            
            PushNotificationManager pushManager = new PushNotificationManager();
            pushManager.initializeConnection(
            		new AppleNotificationServerBasicImpl(
            				getApnsCertificatePath(), getApnsCertificatePassword(), getApnsProduction()));
            
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            
            List<Device> device = new ArrayList<Device>();

            device.add(new BasicDevice(token));
            
            notifications = pushManager.sendNotifications(payLoad, device);

            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            
            for (PushedNotification failedNotification: failedNotifications) {
            	failTokenList.add(failedNotification.getDevice().getToken());
            }

            
            pushManager.stopConnection();
            
        } catch(Exception e) {
        	logger.error(trace(e));
        	failTokenList.add(token);
        }
		
        mInactiveDevices.addAll(failTokenList);
	}
	@Override
	public String getProviderInfo() {

		ZSimpleJsonObject res = new ZGsonObject();
		res.add("provider_class", this.getClass().getName());
		res.add("version", "v1.0.20150921");
		res.add("author", "unknown");
		res.add("website", "https://code.google.com/p/javapns/");
		return res.toString();
	}
	
}
