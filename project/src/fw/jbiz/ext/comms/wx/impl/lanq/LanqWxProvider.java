package fw.jbiz.ext.comms.wx.impl.lanq;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.common.helper.JsonHelper;
import fw.jbiz.common.helper.httpclient.HttpHelper;
import fw.jbiz.ext.comms.wx.ZWxManager;
import fw.jbiz.ext.comms.wx.bean.ZWxBean;
import fw.jbiz.ext.comms.wx.bean.ZWxResult;
import fw.jbiz.ext.comms.wx.interfaces.IWxCallback;
import fw.jbiz.ext.comms.wx.interfaces.IWxProvider;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public class LanqWxProvider extends ZObject implements IWxProvider {

	static Logger logger = Logger.getLogger(LanqWxProvider.class);

	public static final String WX_STATUS_CODE_OK = "0";
	
	private static String appId;
	private static String appSecret;
	
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	private IWxCallback mCallback;
	private boolean isRetry = true;
	
	public LanqWxProvider() {
		logger.info(getProviderInfo());
		initSetting();
	}
	
	public void setConfig(IConfig config) {
		mConfig = config;
		initSetting();
	}
	
	private static void initSetting() {
		if (!mConfig.isValid()) {
			return;
		}
		
		appId = mConfig.getProp("wx_appid");
		appSecret = mConfig.getProp("wx_appsecret");
		

	}
	

	@Override
	public List<ZWxResult> send(List<ZWxBean> wxBeanList) {
		List<ZWxResult> wxResultList = new ArrayList<ZWxResult>();
		
		String errorCode = WX_STATUS_CODE_OK;
		
		boolean isFirstOfError = true;
		for (ZWxBean bean: wxBeanList) {
			ZWxResult result = wxRun(bean);
			
			errorCode = result.getStatusCode();
			if (!WX_STATUS_CODE_OK.equals(errorCode) && isRetry && isFirstOfError) {
				logger.info("wxRun:准备重试 " + errorCode + ", " + isRetry);
				break;
			}
			
			wxResultList.add(result);
			isFirstOfError = false;
		}
		
		// 更新accesstoken 重试
		if (!WX_STATUS_CODE_OK.equals(errorCode) && isRetry && isFirstOfError) {
			logger.info("wxRun:重试 " + errorCode + ", " + isRetry);
			
			// 下次不再重试
			isRetry = false;
			String accessToken = this.getAccessToken();
			
			// 回调通知刷新accessToken
			if (mCallback != null) {
				mCallback.refreshAccessToken(accessToken);
			}

			for (ZWxBean bean: wxBeanList) {
				bean.setAccessToken(accessToken);
			}
			
			this.send(wxBeanList);
			
			logger.info("wxRun:重试完成 " + errorCode + ", " + isRetry);
		}
		
		return wxResultList;
	}

	@Override
	public ZWxResult send(ZWxBean wxBean) {
		ZWxResult result = wxRun(wxBean);
		return result;
	}

	private ZWxResult wxRun(ZWxBean wxBean)  {
		
		ZWxResult wxResult = new ZWxResult();
		
		String url = wxBean.getUrl();
		String accessToken = wxBean.getAccessToken();
		url = String.format(url, accessToken);
		
		String params = wxBean.getPostData();
		
		// 请求微信服务器
		String result = HttpHelper.doPost(url, params);

		wxResult.setOriginResult(result);
		
		// 处理返回结果
		boolean success = (result!=null);
		wxResult.setSuccess(success);
		if (wxResult.isSuccess()) {
			Integer statusCode = JsonHelper.getIntValFromJsonStr("errcode", result);
			String statusMsg = JsonHelper.getStrValFromJsonStr("errmsg", result);
			wxResult.setStatusCode(String.valueOf(statusCode));
			wxResult.setStatusMsg(statusMsg);
		}
		
		logger.info(String.format("wxRun[params:%s, result:%s]", params, result));
		return wxResult;
		
	}

	// 返回json字符串中的access_token，{"access_token":"xxx", "expires":7200}
	@Override
	public String getAccessToken() {
		String url = String.format(ZWxManager.URL_ACCESSTOKEN_GET, appId, appSecret);
		String result = HttpHelper.doGet(url);
		
		if (!StringUtils.isEmpty(result)) {
			result = JsonHelper.getStrValFromJsonStr("access_token", result);
		}
		
		return result;
	}


	@Override
	public String getProviderInfo() {

		ZSimpleJsonObject res = new ZGsonObject();
		res.add("provider_class", this.getClass().getName());
		res.add("version", "v1.0.20151020");
		res.add("author", "JbizFw");
		res.add("website", "https://github.com/xuedapeng/jbiz");
		return res.toString();
	}

	@Override
	public void setCallback(IWxCallback cb) {
		mCallback = cb;
		
	}


}
