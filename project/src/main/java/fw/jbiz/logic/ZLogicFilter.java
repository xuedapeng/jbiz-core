package fw.jbiz.logic;

import javax.persistence.EntityManager;

import fw.jbiz.ZObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public abstract class ZLogicFilter extends ZObject {
	
	public abstract boolean doFilterBefore(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) ;
	public abstract boolean doFilterAfter(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) ;
}
