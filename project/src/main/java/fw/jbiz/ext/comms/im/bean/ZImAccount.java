package fw.jbiz.ext.comms.im.bean;

public class ZImAccount {

	private String username;
	private String password;
	private boolean keepLogin = false;
	
	public ZImAccount() {
		
	}
	
	public ZImAccount(String username, String password) {
		this.username = username;
		this.password = password;
		
	}
	public ZImAccount(String username, String password, boolean keepLogin) {
		this.username = username;
		this.password = password;
		this.keepLogin = keepLogin;
		
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isKeepLogin() {
		return keepLogin;
	}
	public void setKeepLogin(boolean keepLogin) {
		this.keepLogin = keepLogin;
	}
	
	
	
	
}
