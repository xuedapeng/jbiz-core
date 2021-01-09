package fw.jbiz.db;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger; 

import fw.jbiz.ZObject;
import fw.jbiz.concurrent.ZCallback;

public class ZDao extends ZObject{

	static Logger logger = Logger.getLogger(ZDao.class);
	
	protected String tableName = "";
	protected EntityManager em = null;
	
	public ZDao(EntityManager _em) {
		this.em = _em;
	}
	
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * 生成主键ID
	 */
	public  static String genPkId() {

		UUID uuid = UUID.randomUUID();

		return uuid.toString();
	}

	public static void saveAsy(ZEntity entity){
		ZDaoAsyManager.addEntity(entity, null, null);
	}
	public static void saveAsy(ZEntity entity ,ZCallback callback) {
		ZDaoAsyManager.addEntity(entity, null, callback);
	}
	public static void saveAsy(ZEntity entity, ZDao dao){
		ZDaoAsyManager.addEntity(entity, dao, null);
	}
	public static void saveAsy(ZEntity entity, ZDao dao, ZCallback callback) {
		ZDaoAsyManager.addEntity(entity, dao, callback);
	}

	
	
	/**
	 * 保存
	 */
	public void save(ZEntity entity) {
		logger.debug("saving " + tableName + " instance");
		
		try{
		
			em.persist(entity);

			logger.info("save successful");
			
		} catch (RuntimeException re) {
			logger.error("save failed. " + trace(re));
			throw re;
		}
	}

	/**
	 * 删除情报
	 */
	
	public void delete(ZEntity entity) {
		
		logger.debug("deleting " + tableName + " instance");
		try {
			
			em.remove(entity);
			
			logger.debug("delete successful");
			
		} catch (RuntimeException re) {
			logger.error("delete failed. " + trace(re));
			throw re;
		}
	}


	public ZEntity update(ZEntity entity) {
		
		logger.debug("updating " + tableName + " instance");
		try {
			
			ZEntity result = em.merge(entity);
			
			logger.debug("update successful");
			return result;
		} catch (RuntimeException re) {
			logger.error("update failed. " + trace(re));
			throw re;
		}
	}
	

	public final static void beginTrans(EntityManager _em) {
		_em.getTransaction().begin();

	}
	
	public final static void commit(EntityManager _em) {
		_em.getTransaction().commit();

	}


	public final static void rollback(EntityManager _em) {
		_em.getTransaction().rollback();

	}

	/**
	 * 根据操作表的属性进行查询
	 * @Deprecated from 201804
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	public final List findByProperty(String propertyName, final Object value,
			final int... rowStartIdxAndCount) {
		logger.info("finding " + tableName
				+ " instance with property: " + propertyName + ", value: "
				+ value);
		try {
			final String queryString = "from " + tableName
					+ " model where model." + propertyName + "= "
					+ ":propertyValue";
			Query query = em.createQuery(queryString);
			query.setParameter("propertyValue", value);
			if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
				int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
				if (rowStartIdx > 0) {
					query.setFirstResult(rowStartIdx);
				}

				if (rowStartIdxAndCount.length > 1) {
					int rowCount = Math.max(0, rowStartIdxAndCount[1]);
					if (rowCount > 0) {
						query.setMaxResults(rowCount);
					}
				}
			}
			return query.getResultList();
		} catch (RuntimeException re) {
			logger.error("find by property name failed. " + trace(re));
			throw re;
		}
	}

	/**
	 * 全表查询
	 * @Deprecated from 201804
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	public final List findAll(final int... rowStartIdxAndCount) {
		logger.info("finding all " + tableName + " instances");
		try {
			final String queryString = "select model from " + tableName
					+ " model";
			Query query = em.createQuery(queryString);
			if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
				int rowStartIdx = Math.max(0, rowStartIdxAndCount[0]);
				if (rowStartIdx > 0) {
					query.setFirstResult(rowStartIdx);
				}

				if (rowStartIdxAndCount.length > 1) {
					int rowCount = Math.max(0, rowStartIdxAndCount[1]);
					if (rowCount > 0) {
						query.setMaxResults(rowCount);
					}
				}
			}
			return query.getResultList();
		} catch (RuntimeException re) {
			logger.error("find all failed. " + trace(re));
			throw re;
		}
	}

	
}
