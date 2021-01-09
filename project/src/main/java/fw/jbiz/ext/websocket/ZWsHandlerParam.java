package fw.jbiz.ext.websocket;

import fw.jbiz.ZObject;

public abstract class ZWsHandlerParam extends ZObject {
	
	public final static String MSG_TYPE_SIGN_IN = "sign_in";
	public final static String MSG_TYPE_SIGN_OUT = "sign_out";
	public final static String MSG_KEY_MSGTYPE = "msgType";
	public final static String MSG_KEY_PATH = "path";
	
	private String msgType; // sigin_up, sign_out, 其它自定义 
	private String userId;
	private String apiKey;
	private String path;
	
	public abstract ZWsHandlerParam fromJson(String jsonMsg);
	public abstract String toJson();
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
