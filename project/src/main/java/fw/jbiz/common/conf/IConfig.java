package fw.jbiz.common.conf;

public interface IConfig {

	public String getProp(String name);
	public void setProp(String name, String value);
	public boolean isValid();
}
