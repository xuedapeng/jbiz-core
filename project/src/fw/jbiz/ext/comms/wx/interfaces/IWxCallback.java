package fw.jbiz.ext.comms.wx.interfaces;

public interface IWxCallback {
	
	// accesstoken 失效时，内部更新后回调通知外部
	public void refreshAccessToken(String accessToken);

}
