package fw.jbiz.ext.memcache.bean;

import java.util.Map;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;

public class ZCacheProp extends ZObject {

	public static String PROP_CACHE_NAME = "cacheName";
	public static String PROP_FROM_DB = "fromDb";
	public static String PROP_TABLE_NAME = "tableName";
	public static String PROP_KEY_FIELD = "keyField";
	public static String PROP_VALUE_FIELD = "valueField";
	public static String PROP_CONDITION = "condition";
	public static String PROP_AUTO_REFRESH = "autoRefresh";
	public static String PROP_IMPL_CLASS = "implClass";
	
	public ZCacheProp(Map<String, String> config) {
		
		String err = this.validateConfig(config);
		if (err != null) {
			throw new ZException("MEMCACHE", String.format("Incorrect memcache config:%s", err));
		}
		
		cacheName= config.get(PROP_CACHE_NAME);
		fromDb = config.get(PROP_FROM_DB).equals("true")?true:false;
		tableName = config.get(PROP_TABLE_NAME);
		keyField = config.get(PROP_KEY_FIELD);
		valueField = config.get(PROP_VALUE_FIELD);
		condition =config.get(PROP_CONDITION);
		autoRefresh = config.get(PROP_AUTO_REFRESH).equals("true")?true:false;
		implClass = config.get(PROP_IMPL_CLASS);
		
	}
	
	// 返回错误信息
	private String validateConfig(Map<String, String> config) {
		if (config.get(PROP_CACHE_NAME) == null) {
			return String.format("%s not found.", PROP_CACHE_NAME);
		}
		if (config.get(PROP_FROM_DB) == null) {
			return String.format("%s not found.", PROP_FROM_DB);
		}

		if (config.get(PROP_AUTO_REFRESH) == null) {
			return String.format("%s not found.", PROP_AUTO_REFRESH);
		}
		
		if (config.get(PROP_FROM_DB).equals("true")) {

			if (config.get(PROP_TABLE_NAME) == null) {
				return String.format("%s not found.", PROP_TABLE_NAME);
			}
			if (config.get(PROP_KEY_FIELD) == null) {
				return String.format("%s not found.", PROP_KEY_FIELD);
			}
			if (config.get(PROP_VALUE_FIELD) == null) {
				return String.format("%s not found.", PROP_VALUE_FIELD);
			}
		}
		
		
		return null;
	}
	
	private String cacheName;
	private boolean fromDb;
	private String tableName;
	private String keyField;
	private String valueField;
	private String condition;
	private boolean autoRefresh;
	private String implClass;
	
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	public boolean isFromDb() {
		return fromDb;
	}
	public void setFromDb(boolean fromDb) {
		this.fromDb = fromDb;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public String getValueField() {
		return valueField;
	}
	public void setValueField(String valueField) {
		this.valueField = valueField;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public boolean isAutoRefresh() {
		return autoRefresh;
	}
	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}
	public String getImplClass() {
		return implClass;
	}
	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}
	
	
}
