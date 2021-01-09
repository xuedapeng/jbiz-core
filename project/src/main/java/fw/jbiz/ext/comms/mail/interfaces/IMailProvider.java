package fw.jbiz.ext.comms.mail.interfaces;

import java.util.List;

import fw.jbiz.ext.comms.ICommsProvider;
import fw.jbiz.ext.comms.mail.bean.ZMailBean;
import fw.jbiz.ext.comms.mail.bean.ZMailResult;

public interface IMailProvider extends ICommsProvider{
	
	public List<ZMailResult> send(List<ZMailBean> mailBeanList);
	public ZMailResult send(ZMailBean mailBean);

}
