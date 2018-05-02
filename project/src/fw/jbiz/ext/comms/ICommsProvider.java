package fw.jbiz.ext.comms;

import fw.jbiz.common.conf.IConfig;

public interface ICommsProvider {

	public String getProviderInfo();
	public void setConfig(IConfig config);
}
