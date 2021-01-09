package fw.jbiz.ext.memcache.interfaces;

import javax.persistence.EntityManager;

import fw.jbiz.ext.memcache.bean.ZCacheProp;

public interface ICache {
	
	public void init(ZCacheProp cacheProp, EntityManager em);
	
	public void add(Object key, Object value);
	
	public void remove(Object key);
	
	public Object get(Object key);
	
	public boolean contains(Object key);
	
	public boolean hit(Object key, Object value);
	
	public void clear();
	
	public void refresh(EntityManager em);
	
	public ZCacheProp getCacheProp();
	
	public int size();

}
