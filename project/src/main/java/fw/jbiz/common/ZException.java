package fw.jbiz.common;


public class ZException extends RuntimeException {

	public static final String ERR_CD_ROLLBACK = "ERR_CD_ROLLBACK";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errorCode;

	public ZException(String errorCode, Exception e) {
		
		super(e);
		this.errorCode = errorCode;
		
	}
	public ZException(String errorCode, String errorMsg) {
		
		super(errorMsg);
		this.errorCode = errorCode;
		
	}
	public ZException(String errorCode) {
		
		this.errorCode = errorCode;
		
	}
	
	public ZException(Exception e) {
		super(e);
	}

	public String getErrorCode() {
		return this.errorCode;
	}

}
