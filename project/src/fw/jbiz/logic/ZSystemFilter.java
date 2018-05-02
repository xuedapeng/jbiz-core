package fw.jbiz.logic;

import fw.jbiz.ZObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public abstract class ZSystemFilter extends ZObject {
	
	public abstract void doFilterBefore(ZLogicParam logicParam, ZSimpleJsonObject res) ;
	public abstract void doFilterAfter(ZLogicParam logicParam, ZSimpleJsonObject res) ;
}
