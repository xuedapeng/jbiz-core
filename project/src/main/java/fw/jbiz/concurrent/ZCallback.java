package fw.jbiz.concurrent;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;

public abstract class ZCallback extends ZObject {

	public abstract void onSuccess(Long increment);
	public abstract void onError(ZException e);
}
