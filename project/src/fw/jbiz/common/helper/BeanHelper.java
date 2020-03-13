package fw.jbiz.common.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;

public class BeanHelper extends ZObject {

	static Logger logger = Logger.getLogger(BeanHelper.class);

	/*
	 * 通过json的key／value来设置object的属性值。
	 * 只能设置json词典的第一层
	 */
	public static void setValuesFromJson(ZObject obj, String jsonStr) {

		// 获取 参数名－方法 map
		Map<String, Method> methodMap = new HashMap<String, Method>();
		Method[] methods = obj.getClass().getMethods();
		 for (Method method : methods) {  
			 
	            String methodName = method.getName();  

	            if (methodName.startsWith("set")) {

		            String paramName = methodName.substring(3); // 去掉set
		            paramName = paramName.substring(0, 1).toLowerCase() + paramName.substring(1); 
		            methodMap.put(paramName, method);
	            }
		 }

		// 遍历参数
		Map<String, Object> jsonMap = JsonHelper.jsonStr2Map(jsonStr);
 		for(String key: jsonMap.keySet()) {
 			String paramName = key;
 			Object paramValue = jsonMap.get(key);
 			Method method = methodMap.get(paramName);
 			if (method == null) {
 				// 没找到，忽略
 			} else {

 				try {
					method.invoke(obj, paramValue);
					
				} catch (Exception e) {
					logger.error(trace(e));
				}
 			}
 		}
	}
	
	public static Map<String, Object> bean2Map(ZObject obj) {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		Field[] fields = obj.getClass().getDeclaredFields();  
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
				method = obj.getClass()  
				        .getMethod(methodName); 
	            value = method.invoke(obj);  
            	
            	rtnMap.put(name, value==null?"":value);
			} catch (Exception e) {
				logger.error(trace(e));
			} 
            
        }
		
		return rtnMap;
		
		
	}
	
	/*
	 * dump bean to string
	 */
	public static String dumpBean(ZObject obj) {
		
		return dumpBean(obj, null);
		
	}
	public static String dumpBean(ZObject obj, List<String> mask) {
		
		StringBuffer sb = new StringBuffer();
		
		Field[] fields = obj.getClass().getDeclaredFields();  
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
				method = obj.getClass()  
				        .getMethod(methodName); 
	            value = method.invoke(obj);  
	            
	            if (sb.length() > 0) {
	            	sb.append(", ");
	            }
	            
	            if (mask != null) {
		            List<String> maskNames = mask;
		            if (maskNames != null && maskNames.contains(name)) {
		            	value ="***";
		            }
	            }
	            
            	sb.append("\"");
            	sb.append(name);
            	sb.append("\"");
            	sb.append(": ");
            	sb.append("\"");
            	sb.append(value==null?"":value);
            	sb.append("\"");
			} catch (Exception e) {
				logger.error(trace(e));
			} 
            
        }
		
		String ret = sb.toString();

		return ret;
	}
}
