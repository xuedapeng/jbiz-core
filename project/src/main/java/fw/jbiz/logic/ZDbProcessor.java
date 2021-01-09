package fw.jbiz.logic;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.jpa.ZJpaHelper;

public abstract class ZDbProcessor extends ZObject {

	static Logger logger = Logger.getLogger(ZDbProcessor.class);
	
	public abstract void execute(EntityManager em);
	
	public void run() {
		
		logger.info("getEntityManager start");
		EntityManager em = ZJpaHelper.getEntityManager(ZSystemConfig
				.getProperty("persistence_unit_name")); 
		logger.info("getEntityManager end");
		
		try {

			logger.info("beginTransaction start");
			ZJpaHelper.beginTransaction(em);
			logger.info("beginTransaction end");

			logger.info("execute start");
			this.execute(em);
			logger.info("execute end");

			
			logger.info("commit start");
			ZJpaHelper.commit(em);
			logger.info("commit end");
			
		}catch(Exception e){
			logger.error(e);
			logger.info("rollback start");
			ZJpaHelper.rollback(em);
			logger.info("rollback end");
			throw new ZException(e);
		}finally {
			logger.info("closeEntityManager start");
			ZJpaHelper.closeEntityManager(em);
			logger.info("closeEntityManager end");
		}
	}
	
}
