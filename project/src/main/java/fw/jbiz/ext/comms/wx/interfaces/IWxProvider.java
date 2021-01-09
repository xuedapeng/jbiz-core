package fw.jbiz.ext.comms.wx.interfaces;

import java.util.List;

import fw.jbiz.ext.comms.ICommsProvider;
import fw.jbiz.ext.comms.wx.bean.ZWxBean;
import fw.jbiz.ext.comms.wx.bean.ZWxResult;

public interface IWxProvider extends ICommsProvider{
	
	public String getAccessToken();
	public List<ZWxResult> send(List<ZWxBean> wxBeanList);
	public ZWxResult send(ZWxBean wxBean);
	
	public void setCallback(IWxCallback cb);
	
	

}