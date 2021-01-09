package fw.jbiz.ext.comms.mail.bean;

public class ZMailBean {
	
	// 收件人邮箱号码
	private String address;
	
	// 邮件标题
	private String subject;
	
	// 邮件正文
	private String content;
	
	// 发件人邮箱（显示用）
	private String fromAddressDisp;
	
	// 发件人昵称（显示用）
	private String fromNickname;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromAddressDisp() {
		return fromAddressDisp;
	}

	public void setFromAddressDisp(String fromAddressDisp) {
		this.fromAddressDisp = fromAddressDisp;
	}

	public String getFromNickname() {
		return fromNickname;
	}

	public void setFromNickname(String fromNickname) {
		this.fromNickname = fromNickname;
	}
	
	
}
