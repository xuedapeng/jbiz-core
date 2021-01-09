package fw.jbiz.ext.memcache;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;
import fw.jbiz.ext.memcache.bean.ZCacheProp;
import fw.jbiz.ext.memcache.interfaces.ICache;

public class ZCacheImpl extends ZObject implements ICache {

	static Logger logger = Logger.getLogger(ZCacheImpl.class);
	
	private Map<Object, Object> _cacheMap = new HashMap<Object, Object>();
	private ZCacheProp _cacheProp;
	
	@Override
	public final void init(ZCacheProp cacheProp, EntityManager em) {
		_cacheProp = cacheProp;
		
		// check
		if (_cacheProp.isFromDb()) {
			if (em == null) {
				throw new ZException("MEMCACHE", "db cache needs a EntityManager!");
			}
		}
		
		this.refresh(em);
		
	}

	@Override
	public final void add(Object key, Object value) {
		_cacheMap.put(key, value);
		
	}

	@Override
	public final void remove(Object key) {
		
		_cacheMap.remove(key);
		
	}

	@Override
	public final Object get(Object key) {
		
		return _cacheMap.get(key);
	}

	@Override
	public final boolean contains(Object key) {
	
		return _cacheMap.containsKey(key);
	}

	@Override
	public final boolean hit(Object key, Object value) {

		logger.info(String.format("ZCacheImpl#hit:key=%s,value=%s", key, value));
		
		if (key == null || value == null) {
			logger.info("ZCacheImpl#hit:false");
			return false;
		}
		
		if (_cacheMap.containsKey(key) 
				&& value.equals(_cacheMap.get(key))) {
			
			logger.info("ZCacheImpl#hit:true");
			
			return true;
		}

		logger.info("ZCacheImpl#hit:false");
		return false;
	}

	@Override
	public final void clear() {
		_cacheMap.clear();
		
	}

	@Override
	public void refresh(EntityManager em) {
		
		// 不是db
		if (!_cacheProp.isFromDb()) {
			this.clear();
			return;
		}
		
		// db缓存
		ZCacheDao dao = new ZCacheDao(em);
		_cacheMap = dao.getBySql(genSql(_cacheProp));
		
	}
	
	private String genSql(ZCacheProp cacheProp) {
		
		StringBuffer sb = new StringBuffer("");
		sb.append(" select ");
		sb.append(cacheProp.getKeyField());
		sb.append(" , ");
		sb.append(cacheProp.getValueField());
		sb.append(" from ");
		sb.append(cacheProp.getTableName());
		if (!StringUtils.isEmpty(cacheProp.getCondition())) {
			sb.append(" where ");
			sb.append( cacheProp.getCondition());
		}
		
		return sb.toString();
	}

	@Override
	public ZCacheProp getCacheProp() {
		
		return _cacheProp;
	}

	@Override
	public int size() {
		return _cacheMap.size();
	}
	
	

}
