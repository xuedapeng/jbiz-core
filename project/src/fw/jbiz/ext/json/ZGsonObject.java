package fw.jbiz.ext.json;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import fw.jbiz.logic.interfaces.IResponseObject;


public class ZGsonObject extends ZSimpleJsonObject implements IResponseObject {

	static Logger logger = Logger.getLogger(ZGsonObject.class);
	
	private Map<String, Object> mMap = new HashMap<String, Object>();

	@SuppressWarnings("deprecation")
	public ZGsonObject() {

	}
	
	@Override
	public void add(String prop, Object value) {
		mMap.put(prop, value);
		
	}
	

	@Override
	public void clear() {
		mMap.clear();
		
	}

	@Override
	public String toString() {

		Gson son = new Gson();
		String jsonStr = son.toJson(mMap);
		
		return jsonStr;
	}
}
