package fw.jbiz.ext.comms.apns.bean;

import fw.jbiz.ext.comms.ZCommsResult;

public class ZApnsResult extends ZCommsResult {

	private String invalidToken;

	public String getInvalidToken() {
		return invalidToken;
	}

	public void setInvalidToken(String invalidToken) {
		this.invalidToken = invalidToken;
	}
	
	
}
