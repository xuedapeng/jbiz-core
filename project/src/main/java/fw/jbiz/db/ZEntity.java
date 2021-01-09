package fw.jbiz.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import fw.jbiz.ZObject;
import fw.jbiz.common.helper.JsonHelper;

public abstract class ZEntity extends ZObject {

	static Logger logger = Logger.getLogger(ZEntity.class);
	
	
	private Integer recordCount = null;

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}
	

	// 获取 @id 注解的字段名／值
	public Object[] getPkInfo() {
		
		Object[] ret = {null, null};
		
		Method[] methods = this.getClass().getMethods(); 
		
		for (Method method : methods) {  
            Annotation[] annotations = method.getAnnotations();  
            for (Annotation annotation : annotations) {  
                // 获取注解的具体类型  
                Class<? extends Annotation> annotationType = annotation.annotationType();  
                // @id注解
                if (javax.persistence.Id.class == annotationType) {  
                    try {
                    	
                    	String name = getFieldNameFromMethod(method.getName());
						Object value = method.invoke(this);

						// 如果是类型int且值为0，则返回null
						if (value instanceof Integer) {
							if (value.equals(0)) {
								value = null;
							}
						}
						ret[0] = name;
						ret[1] = value;
						break;
					} catch (IllegalAccessException | IllegalArgumentException |InvocationTargetException e) {
						logger.error(e);
						throw new RuntimeException(e);
					}
                }  
            }  
        }  
		
		return ret;
	}

	// getXxxx -> xxxx
	private String getFieldNameFromMethod(String methodName) {
		String fieldName = methodName.replace("get", "");
		fieldName = fieldName.toLowerCase();
		
		return fieldName;
	}
	
	public String dump() {

		Map<String, Object> retMap = new HashMap<String, Object>();
		
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
	            retMap.put(name, value);
	            
			} catch (Exception e) {
				logger.error(e);
			} 
            
        }
		
		String ret = JsonHelper.map2JsonStr(retMap);

		return ret;
	}

}
