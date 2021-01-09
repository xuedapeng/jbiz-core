package fw.jbiz.ext.verifycode.bean;

import java.util.Date;

public class ZVerifyCodeBean {
	private String sessionKey;
	private String code;
	private String destType;
	private String destAddress;
	private Date expiredTime;
	private Date createTime;
	private Integer result;
	
	
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDestType() {
		return destType;
	}
	public void setDestType(String destType) {
		this.destType = destType;
	}
	public String getDestAddress() {
		return destAddress;
	}
	public void setDestAddress(String destAddress) {
		this.destAddress = destAddress;
	}
	public Date getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
	
}
