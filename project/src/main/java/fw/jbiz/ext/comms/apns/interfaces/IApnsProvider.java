package fw.jbiz.ext.comms.apns.interfaces;

import java.util.List;

import fw.jbiz.ext.comms.ICommsProvider;
import fw.jbiz.ext.comms.apns.bean.ZApnsBean;
import fw.jbiz.ext.comms.apns.bean.ZApnsResult;

public interface IApnsProvider extends ICommsProvider{

	public List<ZApnsResult> send(List<ZApnsBean> apnsBeanList);
	public List<ZApnsResult> getInactiveDevices();
	
}
