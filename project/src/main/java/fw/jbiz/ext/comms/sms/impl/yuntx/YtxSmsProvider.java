//package fw.jbiz.ext.comms.sms.impl.yuntx;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
//import org.apache.log4j.Logger;
//
//import com.cloopen.rest.sdk.CCPRestSmsSDK;
//
//import fw.jbiz.ZObject;
//import fw.jbiz.common.conf.IConfig;
//import fw.jbiz.common.conf.ZSystemConfig;
//import fw.jbiz.ext.comms.sms.bean.ZSmsBean;
//import fw.jbiz.ext.comms.sms.bean.ZSmsResult;
//import fw.jbiz.ext.comms.sms.interfaces.ISmsProvider;
//import fw.jbiz.ext.json.ZGsonObject;
//import fw.jbiz.ext.json.ZSimpleJsonObject;
//
//public class YtxSmsProvider extends ZObject implements ISmsProvider {
//
//	static Logger logger = Logger.getLogger(YtxSmsProvider.class);
//	
//	// 初始化SDK
//	private static CCPRestSmsSDK smsRestAPI = null;
//
//	private static String smsServer;
//	private static String smsPort;
//	private static String smsAccount;
//	private static String smsToken;
//	private static String smsAppid;
//	
//	private static IConfig mConfig = ZSystemConfig.getInstance();
//	
//	public YtxSmsProvider() {
//		logger.info(getProviderInfo());
//		initSetting();
//	}
//	
//	public void setConfig(IConfig config) {
//		mConfig = config;
//		initSetting();
//	}
//	
//	private static void initSetting() {
//		if (!mConfig.isValid()) {
//			return;
//		}
//		smsServer = mConfig.getProp("sms_send_server");
//		smsPort = mConfig.getProp("sms_send_port");
//		smsAccount = mConfig.getProp("sms_send_account");
//		smsToken = mConfig.getProp("sms_send_token");
//		smsAppid = mConfig.getProp("sms_send_appid");
//		
//
//		smsRestAPI = new CCPRestSmsSDK();
//		// ******************************注释*********************************************
//		// *初始化服务器地址和端口 *
//		// *沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
//		// *生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883"); *
//		// *******************************************************************************
//		smsRestAPI.init(smsServer, smsPort);
//
//		// ******************************注释*********************************************
//		// *初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN *
//		// *ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
//		// *参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。 *
//		// *******************************************************************************
//		smsRestAPI.setAccount(smsAccount, smsToken);
//
//		// ******************************注释*********************************************
//		// *初始化应用ID *
//		// *测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID *
//		// *应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
//		// *******************************************************************************
//		smsRestAPI.setAppId(smsAppid);
//	}
//	
//	@Override
//	public List<ZSmsResult> send(List<ZSmsBean> smsBeanList) {
//		List<ZSmsResult> smsResultList = new ArrayList<ZSmsResult>();
//		for (ZSmsBean bean: smsBeanList) {
//			ZSmsResult result = smsRun((YtxSmsBean)bean);
//			smsResultList.add(result);
//		}
//		
//		return smsResultList;
//	}
//
//	@Override
//	public ZSmsResult send(ZSmsBean smsBean) {
//		ZSmsResult result = smsRun((YtxSmsBean)smsBean);
//		return result;
//	}
//
//	private ZSmsResult smsRun(YtxSmsBean bean) {
//
//		logger.info(bean.toString());
//		
//		String mobile = bean.getMobileNumber();
//		String templateId = bean.getTemplateId();
//		String[] params = bean.getParams();
//		
//		ZSmsResult smsResult = new ZSmsResult();
//		
//		logger.info(
//				String.format("smsRun start:[mobile:%s, code:%s]", mobile, params[0]));
//
//
//		HashMap<String, Object> result = null;
//
//		// ******************************注释****************************************************************
//		// *调用发送模板短信的接口发送短信 *
//		// *参数顺序说明： *
//		// *第一个参数:是要发送的手机号码，可以用逗号分隔，一次最多支持100个手机号 *
//		// *第二个参数:是模板ID，在平台上创建的短信模板的ID值；测试的时候可以使用系统的默认模板，id为1。 *
//		// *系统默认模板的内容为“【云通讯】您使用的是云通讯短信模板，您的验证码是{1}，请于{2}分钟内正确输入”*
//		// *第三个参数是要替换的内容数组。 *
//		// **************************************************************************************************
//
//		// **************************************举例说明***********************************************************************
//		// *假设您用测试Demo的APP ID，则需使用默认模板ID 1，发送手机号是13800000000，传入参数为6532和5，则调用方式为
//		// *
//		// *result = restAPI.sendTemplateSMS("13800000000","1" ,new
//		// String[]{"6532","5"}); *
//		// *则13800000000手机号收到的短信内容是：【云通讯】您使用的是云通讯短信模板，您的验证码是6532，请于5分钟内正确输入 *
//		// *********************************************************************************************************************
//		result = smsRestAPI.sendTemplateSMS(mobile, templateId, params);
//
//		logger.info("SDKTestGetSubAccounts result=" + result);
//
//		String statusCode = (String)result.get("statusCode");
//		smsResult.setStatusCode(statusCode);
//		
//		if ("000000".equals(result.get("statusCode"))) {
//			
//			String msg = "";
//			// 正常返回输出data包体信息（map）
//			@SuppressWarnings("unchecked")
//			HashMap<String, Object> data = (HashMap<String, Object>) result
//					.get("data");
//			Set<String> keySet = data.keySet();
//			for (String key : keySet) {
//				Object object = data.get(key);
//				logger.info(key + " = " + object);
//				msg += (key + " = " + object + ";");
//			}
//
//			smsResult.setSuccess(true);
//			smsResult.setStatusMsg(msg);
//			
//		} else {
//			// 异常返回输出错误码和错误信息
//			String err = "错误码=" + result.get("statusCode") + " 错误信息= "
//					+ result.get("statusMsg");
//			logger.error(err);
//			smsResult.setSuccess(false);
//			
//			String statusMsg = (String)result.get("statusMsg");
//			smsResult.setStatusMsg(statusMsg);
//		}
//
//		logger.info(
//				String.format("smsRun end:[mobile:%s, code:%s]", mobile, params[0]));
//		
//		logger.info(smsResult.toString());
//
//		return smsResult;
//
//	}
//
//	@Override
//	public String getProviderInfo() {
//		ZSimpleJsonObject res = new ZGsonObject();
//		res.add("provider_class", this.getClass().getName());
//		res.add("version", "v1.0.20150921");
//		res.add("author", "容联云通讯");
//		res.add("website", "http://www.yuntongxun.com/");
//		return res.toString();
//	}
//
//}
