package fw.jbiz.common.bean;

public class ZResult {

	private boolean isSuccess;
	private String statusCode;
	private String statusMsg;
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public String toString() {
		String format = "Result: [isSucess=%s, statusCode=%s, statusMsg=%s]";
		return String.format(format, isSuccess, statusCode, statusMsg);
	}
	
}
