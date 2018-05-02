//package fw.jbiz.ext.comms.im.interfaces;
//
//import java.util.List;
//
//import fw.jbiz.ext.comms.ICommsProvider;
//import fw.jbiz.ext.comms.im.bean.ZImAccount;
//import fw.jbiz.ext.comms.im.bean.ZImMsgBean;
//import fw.jbiz.ext.comms.im.bean.ZImResult;
//
//public interface IImProvider extends ICommsProvider {
//
//	// 发送消息
//	public ZImResult sendChatMsg(ZImMsgBean imMsgBean, ZImAccount account);
//	
//	// 注册账号
//	public ZImResult registerAccount(ZImAccount account);
//	
//	// 注销账号
//	public ZImResult removeAccount(ZImAccount account);
//	
//	// 创建聊天室
//	public ZImResult createRoom(String roomName, ZImAccount account);
//	
//	// 删除聊天室
//	public ZImResult destroyRoom(String roomName, ZImAccount account);
//
//	// 邀请加入聊天室
//	public ZImResult inviteParticipant(List<String> users, String roomName, ZImAccount account);
//	
//	// 踢出聊天室
//	public ZImResult kickParticipant(List<String> users, String roomName, ZImAccount account);
//
//}
