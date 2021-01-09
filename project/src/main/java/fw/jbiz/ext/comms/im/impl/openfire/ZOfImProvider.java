//package fw.jbiz.ext.comms.im.impl.openfire;
//
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import fw.jbiz.ZObject;
//import fw.jbiz.common.conf.IConfig;
//import fw.jbiz.ext.comms.im.bean.ZImAccount;
//import fw.jbiz.ext.comms.im.bean.ZImMsgBean;
//import fw.jbiz.ext.comms.im.bean.ZImResult;
//import fw.jbiz.ext.comms.im.interfaces.IImProvider;
//import fw.jbiz.ext.json.ZGsonObject;
//import fw.jbiz.ext.json.ZSimpleJsonObject;
//
//public class ZOfImProvider extends ZObject implements IImProvider {
//
//	static Logger logger = Logger.getLogger(ZOfImProvider.class);
//	
//	@Override
//	public void setConfig(IConfig config) {
//		ZOfConnectionManager.setConfig(config);
//	}
//	
//	public ZOfImProvider() {
//		logger.info(getProviderInfo());
//	}
//	
//	@Override
//	public ZImResult sendChatMsg(ZImMsgBean imMsgBean, ZImAccount account) {
//		
//		String username = account.getUsername();
//		String password = account.getPassword();
//		boolean keepLogin = account.isKeepLogin();
//		
//		ZOfImManager imMan = null;
//		
//		if (keepLogin) {
//			imMan = ZOfImManager
//					.getInstanceKeepLogin(username, password);
//		} else {
//			imMan = ZOfImManager.getInstanceOneTimeLogin(username, password);
//		}
//
//		boolean success = imMan.sendChatMsg(imMsgBean);
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//		
//	}
//
//	@Override
//	public ZImResult registerAccount(ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		
//		ZOfImManager imMan = ZOfImManager.getInstanceNoLogin();
//		
//		// 1、注册成功 0、服务器没有返回结果 2、这个账号已经存在 3、注册失败
//		int rtn = imMan.accountRegist(username, password);
//		
//		boolean success = (rtn==1);
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		result.setStatusCode(String.valueOf(rtn));
//		return result;
//	}
//
//	@Override
//	public ZImResult removeAccount(ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		
//		ZOfImManager imMan = 
//				ZOfImManager.getInstanceOneTimeLogin(username, password);
//
//		boolean success = imMan.accountDelete();
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//	}
//
//	@Override
//	public ZImResult createRoom(String roomName, ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		boolean keepLogin = account.isKeepLogin();
//		
//		ZOfImManager imMan = null;
//		boolean success;
//		
//		if (keepLogin) {
//			imMan = ZOfImManager
//					.getInstanceKeepLogin(username, password);
//			success = imMan.createRoom(roomName);
//			
//		} else {
//			imMan = ZOfImManager.getInstanceOneTimeLogin(username, password);
//			success = imMan.createRoom(roomName);
//			imMan.disconnect();
//		}
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//		
//	}
//
//	@Override
//	public ZImResult destroyRoom(String roomName, ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		boolean keepLogin = account.isKeepLogin();
//		
//		ZOfImManager imMan = null;
//		boolean success;
//		
//		if (keepLogin) {
//			imMan = ZOfImManager
//					.getInstanceKeepLogin(username, password);
//			success = imMan.destroyRoom(roomName);
//			
//		} else {
//			imMan = ZOfImManager.getInstanceOneTimeLogin(username, password);
//			success = imMan.destroyRoom(roomName);
//			imMan.disconnect();
//		}
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//		
//	}
//
//	@Override
//	public ZImResult inviteParticipant(List<String> users, String roomName,
//			ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		boolean keepLogin = account.isKeepLogin();
//		
//		ZOfImManager imMan = null;
//		boolean success;
//		
//		if (keepLogin) {
//			imMan = ZOfImManager
//					.getInstanceKeepLogin(username, password);
//			success = imMan.inviteParticipant(users, roomName);
//			
//		} else {
//			imMan = ZOfImManager.getInstanceOneTimeLogin(username, password);
//			success = imMan.inviteParticipant(users, roomName);
//			imMan.disconnect();
//		}
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//	}
//
//	@Override
//	public ZImResult kickParticipant(List<String> users, String roomName,
//			ZImAccount account) {
//
//		String username = account.getUsername();
//		String password = account.getPassword();
//		boolean keepLogin = account.isKeepLogin();
//		
//		ZOfImManager imMan = null;
//		boolean success;
//		
//		if (keepLogin) {
//			imMan = ZOfImManager
//					.getInstanceKeepLogin(username, password);
//			success = imMan.kickParticipant(users, roomName);
//			
//		} else {
//			imMan = ZOfImManager.getInstanceOneTimeLogin(username, password);
//			success = imMan.kickParticipant(users, roomName);
//			imMan.disconnect();
//		}
//		
//		ZImResult result = new ZImResult();
//		result.setSuccess(success);
//		
//		return result;
//	}
//
//	@Override
//	public String getProviderInfo() {
//
//		ZSimpleJsonObject res = new ZGsonObject();
//		res.add("provider_class", this.getClass().getName());
//		res.add("version", "v1.0.20150921");
//		res.add("author", "JbizFw");
//		res.add("website", "https://github.com/xuedapeng/jbiz");
//		return res.toString();
//	}
//
//}
