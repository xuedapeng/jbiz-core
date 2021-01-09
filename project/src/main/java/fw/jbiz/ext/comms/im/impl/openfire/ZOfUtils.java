//package fw.jbiz.ext.comms.im.impl.openfire;
//
//import java.util.ArrayList;
//
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.util.StringUtils;
//import org.jivesoftware.smackx.packet.MUCUser;
//
//public class ZOfUtils {
//
//	/** 
//	 * 返回"user@jivesoftware.com/jbiz"格式的字串 
//	 * 
//	 * @param username
//	 * @param connection
//	 * 
//	 * @return String
//	 */
//	public static String getFullJIDWithResource(String username, XMPPConnection connection) {
//		return getFullJID(username, connection) + "/"
//				+ StringUtils.parseResource(connection.getUser());
//	}
//
//	/** 
//	 * 返回"user@jivesoftware.com"格式的字串
//	 * 
//	 * @param username
//	 * @param connection
//	 * 
//	 * @return String
//	 */
//	public static String getFullJID(String username, XMPPConnection connection) {
//		int atIndex = username.lastIndexOf("@");
//		if (atIndex >= 0) {
//			username = username.substring(0, atIndex);
//		}
//		return username + "@" + connection.getServiceName();
//	}
//
//	/** 
//	 * 返回"room@conference.jivesoftware.com"格式的字串 
//	 * 
//	 * @param username
//	 * @param connection
//	 * 
//	 * @return String
//	 */
//	public static String getFullRoomID(String roomname, XMPPConnection connection) {
//		int atIndex = roomname.lastIndexOf("@");
//		if (atIndex >= 0) {
//			roomname = roomname.substring(0, atIndex);
//		}
//		return roomname + "@conference." + connection.getServiceName();
//	}
//
//	/** 
//	 * 返回"user@jivesoftware.com/jbiz"格式中的"user"字串 
//	 * 
//	 * @param username
//	 * 
//	 * @return String
//	 */
//	public static String getBareJID(String username) {
//		int atIndex = username.lastIndexOf("@");
//		if (atIndex >= 0) {
//			username = username.substring(0, atIndex);
//		}
//		return username;
//	}
//
//	/** 
//	 * 判断一条消息是否是被踢出聊天室的通知 
//	 * 
//	 * @param packet
//	 * 
//	 * @return boolean
//	 */
//	public static boolean isKickedoutMessage(Packet packet) {
//		if (!(packet instanceof Presence)) {
//			return false;
//		}
//		
//		Presence presence = (Presence) packet;
//        String to = StringUtils.parseName(presence.getTo());
//		if (presence.getType() == Presence.Type.unavailable) {
//            MUCUser mucUser = (MUCUser) presence.getExtension("x", "http://jabber.org/protocol/muc#user");
//            if (mucUser != null && mucUser.getStatus() != null 
//            		&& "307".equals(mucUser.getStatus().getCode())) {
//            	String jid =  StringUtils.parseName(mucUser.getItem().getJid());
//            	if (to.equals(jid)) {
//            		// 是被踢出聊天室的通知
//            		return true;
//            	}
//            }
//		}
//		return false;
//	}
//
//	/** 
//	 * 判断一条消息是否是聊天室邀请的通知 
//	 * 
//	 * @param packet
//	 * 
//	 * @return boolean
//	 */
//	public static boolean isInviteMessage(Packet packet) {
//		// 不是消息，不处理
//		if (!(packet instanceof Message)) {
//			return false;
//		}
//
//        // Get the MUCUser extension
//        MUCUser mucUser =
//            (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user");
//        // Check if the MUCUser extension includes an invitation
//        if (mucUser != null && mucUser.getInvite() != null &&
//                ((Message) packet).getType() != Message.Type.error) {
//        	return true;
//        }
//		return false;
//	}
//
//	/** 
//	 * 判断一条消息是否是聊天消息（包括单聊和群聊） 
//	 * 
//	 * @param packet
//	 * 
//	 * @return boolean
//	 */
//	public static boolean isChatMessage(Packet packet) {		
//		// 不是消息，不处理
//		if (!(packet instanceof Message)) {
//			return false;
//		}
//		
//		Message msg = (Message) packet;
//		// 不是聊天消息，不处理（可能是聊天室的加入邀请）
//		if (msg.getBody() == null) {
//			return false;
//		}
//		
//		// 既不是单聊，也不是群聊消息，不处理。（可能是错误消息）
//		ArrayList<Message.Type> typeSet = new ArrayList<Message.Type>();
//		typeSet.add(Message.Type.chat);
//		typeSet.add(Message.Type.groupchat);
//		if (!(typeSet.contains(msg.getType()))) {
//			return false;
//		}
//		
//		return true;
//	}
//	
//}
