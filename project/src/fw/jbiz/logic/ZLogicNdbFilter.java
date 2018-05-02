package fw.jbiz.logic;

import fw.jbiz.ZObject;
import fw.jbiz.logic.interfaces.IResponseObject;

public abstract class ZLogicNdbFilter extends ZObject {
	
	public abstract boolean doFilterBefore(ZLogicParam logicParam, IResponseObject res) ;
	public abstract boolean doFilterAfter(ZLogicParam logicParam, IResponseObject res) ;
}
