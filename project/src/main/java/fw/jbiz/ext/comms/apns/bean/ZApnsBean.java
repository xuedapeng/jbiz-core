package fw.jbiz.ext.comms.apns.bean;

import java.util.Map;

public class ZApnsBean {

	String deviceToken;
	String alertTitle;
	String alertBody;
	String sound;
	int badge;
	String category;
	Map<String, Object> customInfo;
	
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public String getAlertTitle() {
		return alertTitle;
	}
	public void setAlertTitle(String alertTitle) {
		this.alertTitle = alertTitle;
	}
	public String getAlertBody() {
		return alertBody;
	}
	public void setAlertBody(String alertBody) {
		this.alertBody = alertBody;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public int getBadge() {
		return badge;
	}
	public void setBadge(int badge) {
		this.badge = badge;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Map<String, Object> getCustomInfo() {
		return customInfo;
	}
	public void setCustomInfo(Map<String, Object> customInfo) {
		this.customInfo = customInfo;
	}
	
}
