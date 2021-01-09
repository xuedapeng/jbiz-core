package fw.jbiz.ext.memcache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fw.jbiz.common.ZException;
import fw.jbiz.db.ZDao;

public class ZCacheDao extends ZDao {

	public ZCacheDao(EntityManager _em) {
		super(_em);
	}
	
	public Map<Object, Object> getBySql(String sql) {
		
		Map<Object, Object> result = new HashMap<Object, Object>();
		
		Query query = this.getEntityManager().createNativeQuery(sql);
		
	    @SuppressWarnings("unchecked")
		List<Object[]> records = query.getResultList();

		
		if (records != null) {
			for(Object[] record : records){  
				Object key = record[0];  
				Object value = record[1]; 
				if (result.containsKey(key)) {
					throw new ZException("MEMCACHE", "ZCacheDao:key唯一性约束违反。");
				}
				result.put(key, value);
			}
		}
		
		return result;
		
	}
	
	

}
