package fw.jbiz.ext.attach;

import fw.jbiz.common.conf.IConfig;

public interface IAttachProvider {

	public String getProviderInfo();
	public void setConfig(IConfig config);
}
