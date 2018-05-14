package fw.jbiz.logic.interfaces;

import fw.jbiz.ZObject;

public interface IResponseObject {

	public static final int RSP_CD_INFO = 1;
	public static final int RSP_CD_OK = 0;
	public static final int RSP_CD_OK_SILENT = 9999;
	public static final int RSP_CD_ERR_UNKNOWN = -1;
	public static final int RSP_CD_ERR_AUTH = -2;
	public static final int RSP_CD_ERR_PARAM = -3;

	public static final String RSP_KEY_STATUS = "status";
	public static final String RSP_KEY_MSG = "msg";
	public static final String RSP_KEY_CALLBACK = "_callback";
	
	public IResponseObject add(String prop, Object value);
	public Object get(String prop);
	public void clear();
	public String toString();
	
	public void ending();
	
	public abstract static class Ending extends ZObject {
	
		public abstract void run();
		
		@Override
		public String toString() {
			return "";
		}
	}
	
}