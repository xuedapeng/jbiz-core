package fw.jbiz.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.ZSystemConfig;

/**
 * @author MyEclipse Persistence Tools
 */
public final class ZJpaHelper extends ZObject {

	static Logger logger = Logger.getLogger(ZJpaHelper.class);
	
	public static final String PROP_KEY_DB_USER = "hibernate.connection.username"; 
	public static final String PROP_KEY_DB_PWD = "hibernate.connection.password"; 
	
	
	private static  EntityManagerFactory emf = null;

	private static EntityManagerFactory getSingleEmf(String persistenceUnitName) {
		
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName, getDbAuth());
		}
		return emf;
	}
	
	public static EntityManager getEntityManager(String persistenceUnitName) {
		EntityManager manager = getSingleEmf(persistenceUnitName).createEntityManager();
		return manager;
	}

	public static void closeEntityManager(EntityManager em) {
		if (em != null)
			em.close();
	}

	public static void beginTransaction(EntityManager em) {
		em.getTransaction().begin();
	}

	public static void commit(EntityManager em) {
		em.getTransaction().commit();
	}

	public static void rollback(EntityManager em) {
		try {
			em.getTransaction().rollback();
		} catch (Exception e) {
			logger.error(trace(e));
		}
	}

	public static Query createQuery(String query, EntityManager em) {
		return em.createQuery(query);
	}
	
	private static Map<String, String> getDbAuth() {
		
		String username = ZSystemConfig.getProperty(PROP_KEY_DB_USER);
		String password = ZSystemConfig.getProperty(PROP_KEY_DB_PWD);
		
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return null;
		}
		
		Map<String, String> map = new HashMap<String,String>();
		map.put(PROP_KEY_DB_USER, username);
		map.put(PROP_KEY_DB_PWD, password);
		return map;
	}


	@Deprecated
	public static void log(String info, Level level, Throwable ex, Logger logger) {
		
		if (ex == null) {
			logger.info(info);
		} else {
			logger.error(info);
			logger.error(trace(ex));
		}
	}

	@Deprecated
	public static void log(String info, Level level, Throwable ex) {
		
		if (ex == null) {
			log().info(info);
		} else {
			log().error(info);
			log().error(ex);
		}
	}
}
