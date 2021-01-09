package fw.jbiz.ext.comms.sms.bean;

public class ZSmsBean {

	// 目标手机号码
	private String mobileNumber;
	// 短信文本
	private String plainContent;
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getPlainContent() {
		return plainContent;
	}
	public void setPlainContent(String plainContent) {
		this.plainContent = plainContent;
	}
	
	
}
