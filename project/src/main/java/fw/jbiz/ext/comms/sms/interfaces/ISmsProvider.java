package fw.jbiz.ext.comms.sms.interfaces;

import java.util.List;

import fw.jbiz.ext.comms.ICommsProvider;
import fw.jbiz.ext.comms.sms.bean.ZSmsBean;
import fw.jbiz.ext.comms.sms.bean.ZSmsResult;

public interface ISmsProvider extends ICommsProvider{
	
	public List<ZSmsResult> send(List<ZSmsBean> smsBeanList);
	public ZSmsResult send(ZSmsBean smsBean);

}
