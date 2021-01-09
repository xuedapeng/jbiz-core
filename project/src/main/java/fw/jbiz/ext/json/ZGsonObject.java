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
	public IResponseObject add(String prop, Object value) {
		mMap.put(prop, value);
		
		return this;
		
	}
	

	@Override
	public Object get(String prop) {
		
		return mMap.get(prop);
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
	

	@Override
	public void ending() {
		if (!mMap.containsKey(IResponseObject.RSP_KEY_CALLBACK)) {
			return;
		}
		
		Object ending = get(IResponseObject.RSP_KEY_CALLBACK);
		
		if (ending instanceof IResponseObject.Ending) {
			IResponseObject.Ending cb = (IResponseObject.Ending)ending;
			cb.run();
		}
	}
}
