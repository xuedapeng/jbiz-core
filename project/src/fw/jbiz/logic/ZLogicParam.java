package fw.jbiz.logic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;

public class ZLogicParam extends ZObject {
	
	static Logger logger = Logger.getLogger(ZLogicParam.class);
	
	public ZLogicParam(String _userId, String _apiKey) {
		this.userId = _userId;
		this.apiKey = _apiKey;
	}

	public ZLogicParam(String _userId, String _apiKey, HttpServletRequest request) {
		this.userId = _userId;
		this.apiKey = _apiKey;
		this.request = request;
	}
	
	protected HttpServletRequest request;
    private String apiKey;
    private String userId;
    
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	// dump 时需要掩码的字段
	public List<String> getMaskNamesForDump() {
		return null;
	}

	public String dumpParams() {
		
		StringBuffer sb = new StringBuffer();
		
		Field[] fields = this.getClass().getDeclaredFields();  
        for (Field field : fields) {  
            Method method = null;  
            Object value = null;  
            String name = field.getName();  
            String upperName = name.substring(0, 1).toUpperCase()  
                    + name.substring(1); 
            
            String methodName = "get" + upperName;
            if (name.startsWith("is")) {
            	methodName = name;
            }
            
            try {
				method = this.getClass()  
				        .getMethod(methodName); 
	            value = method.invoke(this);  
	            
	            if (sb.length() > 0) {
	            	sb.append(", ");
	            }
	            
	            List<String> maskNames = getMaskNamesForDump();
	            if (maskNames != null && maskNames.contains(name)) {
	            	value ="***";
	            }
	            
            	sb.append("\"");
            	sb.append(name);
            	sb.append("\"");
            	sb.append(": ");
            	sb.append("\"");
            	sb.append(value);
            	sb.append("\"");
			} catch (Exception e) {
				logger.error(trace(e));
			} 
            
        }
		
		String ret = sb.toString();

		return ret;
	}

}
