package fw.jbiz.common.helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fw.jbiz.ZObject;
import fw.olib.org.json.JSONException;
import fw.olib.org.json.JSONObject;

public class JsonHelper extends ZObject {

	static Logger logger = Logger.getLogger(JsonHelper.class);
	
	/*
	 * 获取简单的一维json元素值
	 * @param key
	 * @param jsonStr
	 */
	public static String getStrValFromJsonStr(String key, String jsonStr) {
		
		String result = null;
		
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			result = jsonObj.getString(key);
			
		} catch (JSONException e) {
			
			logger.debug(trace(e));
		}
		
		return result;
	}
	
	// 获取简单的一维json元素值
	public static Integer getIntValFromJsonStr(String key, String jsonStr) {
		
		Integer result = null;
		
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			result = jsonObj.getInt(key);
			
		} catch (JSONException e) {

			logger.debug(trace(e));
		}
		
		return result;
	}
	
	//  获取json数组
	public static net.sf.json.JSONArray getJSONArrayFromJsonArrStr(String jsonArrStr) {
		net.sf.json.JSONArray objectArr = null;
		
		String jsonDicStr = "{key:" + jsonArrStr + "}";
		net.sf.json.JSONObject jsonobject = net.sf.json.JSONObject.fromObject(jsonDicStr);
		objectArr = jsonobject.getJSONArray("key");

		return objectArr;
	}
	
	// HashMap 转换为json字符串
	public static String map2JsonStr(Map<String, Object> map) {
		String jsonStr = null;
		jsonStr = new GsonBuilder().disableHtmlEscaping().create().toJson(map);
		
		return jsonStr;
		
	}

	// ArrayList 转换为json字符串
	public static String list2JsonStr(List<Object> list) {
		String jsonStr = null;
		
		jsonStr = new GsonBuilder().disableHtmlEscaping().create().toJson(list);
		
		return jsonStr;
		
	}

	// json字符串 转换为 HashMap
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonStr2Map(String jsonStr) {
//		Map<String, Object> retMap = new HashMap<String, Object>();
//		
//		if (StringUtils.isEmpty(jsonStr)) {
//			return retMap;
//		}
//
//		net.sf.json.JSONObject jsonobject = net.sf.json.JSONObject.fromObject(jsonStr);
//		
//		retMap = (Map<String, Object>) jsonObjectToJavaObject(jsonobject);
		
		return json2map(jsonStr);
		
	}

	// json字符串 转换为 ArrayList
	@SuppressWarnings("unchecked")
	public static List<Object> jsonStr2List(String jsonStr) {
//		List<Object> retList = new ArrayList<Object>();
//		
//		if (StringUtils.isEmpty(jsonStr)) {
//			return retList;
//		}
//
//		net.sf.json.JSONArray jsonobject = net.sf.json.JSONArray.fromObject(jsonStr);
//		
//		retList = (List<Object>) jsonObjectToJavaObject(jsonobject);
		
		return json2list(jsonStr);
		
	}

	@SuppressWarnings("rawtypes")
	private static Object jsonObjectToJavaObject(Object pJsonObject) {
		
		Object javaObject = null;
		
		if (pJsonObject instanceof net.sf.json.JSONArray) {
			List<Object> list = new ArrayList<Object>();

			net.sf.json.JSONArray jsonArray = (net.sf.json.JSONArray)pJsonObject;
			for (int i = 0; i < jsonArray.size(); i++) {
				Object sObject = jsonArray.get(i);
				Object vObject = jsonObjectToJavaObject(sObject);
				list.add(vObject);
			}
			
			javaObject = list;
		
		} else if (pJsonObject instanceof net.sf.json.JSONObject) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject)pJsonObject;
			Iterator it = jsonObject.keys();  
            while (it.hasNext()) {  
                String key = (String) it.next();  
                Object value = jsonObject.get(key);  
                Object vObject = jsonObjectToJavaObject(value);
                map.put(key, vObject);
                
                javaObject = map;
            }
		} else {
			javaObject = pJsonObject;
		}
		
		
		return javaObject;
	}
	

	public static String map2json(Map<String, Object> map) {
		return new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create().toJson(map);
	}

	public static String list2json(List<Object> list) {

		return new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create().toJson(list);
	}

	public static Map<String, Object> json2map(String json) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();

        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> map = gson.fromJson(json, type);
        
        return map;
	}

	public static List<Object> json2list(String json) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();

        Type type = new TypeToken<List<Object>>() {}.getType();
        List<Object> list = gson.fromJson(json, type);
        
        return list;
	}
}
