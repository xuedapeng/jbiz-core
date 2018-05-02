//package fw.jbiz.ext.comms.im.bean;
//
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
//
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.util.StringUtils;
//
//
//public class ZImMsgBean {
//
//	/**
//	 * 消息发送者
//	 */
//	private String from;
//
//	/**
//	 * 消息接收者。可以是用户ID，也可以是群房间ID
//	 */
//	private String to;
//
//	/**
//	 * 群房间ID
//	 */
//	private String room;
//
//	/**
//	 * 消息内容 
//	 */
//	private String body;
//
//	/**
//	 * 消息发送时间
//	 */
//	private String serverTime;
//	/**
//	 * 是否为群消息
//	 */
//	private boolean isGroup;
//
//	private final Map<String, Object> properties = new HashMap<String, Object>();
//
//	public String getFrom() {
//		return from;
//	}
//
//	public void setFrom(String from) {
//		this.from = from;
//	}
//
//	public String getTo() {
//		return to;
//	}
//
//	public void setTo(String to) {
//		this.to = to;
//	}
//
//	public String getRoom() {
//		return room;
//	}
//
//	public void setRoom(String room) {
//		this.room = room;
//	}
//
//	public String getBody() {
//		return body;
//	}
//
//	public void setBody(String body) {
//		this.body = body;
//	}
//
//	public String getServerTime() {
//		return serverTime;
//	}
//
//	public Date getServerTimeByDate() {
//		return new Date(Long.parseLong(this.serverTime));
//	}
//
//	public void setServerTime(String serverTime) {
//		this.serverTime = serverTime;
//	}
//
//	/**
//	 * Returns an unmodifiable collection of all the property names that are set.
//	 *
//	 * @return all property names.
//	 */
//	public synchronized Collection<String> getPropertyNames() {
//		if (properties == null) {
//			return Collections.emptySet();
//		}
//		return Collections.unmodifiableSet(new HashSet<String>(properties.keySet()));
//	}
//
//	/**
//	 * Returns the packet property with the specified name or <tt>null</tt> if the
//	 * property doesn't exist. Property values that were originally primitives will
//	 * be returned as their object equivalent. For example, an int property will be
//	 * returned as an Integer, a double as a Double, etc.
//	 *
//	 * @param name the name of the property.
//	 * @return the property, or <tt>null</tt> if the property doesn't exist.
//	 */
//	public synchronized Object getProperty(String name) {
//		if (properties == null) {
//			return null;
//		}
//		return properties.get(name);
//	}
//
//	/**
//	 * Sets a property with an Object as the value. The value must be Serializable
//	 * or an IllegalArgumentException will be thrown.
//	 *
//	 * @param name the name of the property.
//	 * @param value the value of the property.
//	 */
//	public synchronized void setProperty(String name, Object value) {
//		if (!(value instanceof Serializable)) {
//			throw new IllegalArgumentException("Value must be serialiazble");
//		}
//		properties.put(name, value);
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		boolean flg = false;
//		try {
//			if (getRoom() != null && getRoom().length() > 0)
//				sb.append("(" + getRoom() + ")");
//			sb.append(getFrom() + "-->" + getTo() + ": " + getBody());
//			sb.append("  (properties: ");
//			Iterator<String> iter = this.getPropertyNames().iterator();
//			while (iter.hasNext()) {
//				String name = iter.next();
//				Object value = this.getProperty(name);
//				if (flg) {
//					sb.append(",");
//				}
//				sb.append("(" + name + ": " + value + ")");
//				flg = true;
//			}
//			sb.append(")");
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//		return sb.toString();
//	}
//
//	/**
//	 * 把一个Message对象转换成OfMessage对象
//	 * 
//	 * @param msg
//	 * 
//	 * @return ofMsg
//	 */
//	public static ZImMsgBean parseOfMessage(Message msg) {
//		ZImMsgBean ofMsg = new ZImMsgBean();
//		try {
//			if (Message.Type.groupchat.equals(msg.getType())) {
//				// from user
//				ofMsg.setFrom(StringUtils.parseResource(msg.getFrom()));
//				// chat room
//				ofMsg.setRoom(StringUtils.parseName(msg.getFrom()));
//				ofMsg.setGroup(true);
//			} else {
//				// from user
//				ofMsg.setFrom(StringUtils.parseName(msg.getFrom()));
//				// chat room
//				ofMsg.setRoom("");
//			}
//			// to user
//			ofMsg.setTo(StringUtils.parseName(msg.getTo()));
//			// message body
//			ofMsg.setBody(msg.getBody());
//
//			// properties
//			Iterator<String> iter = msg.getPropertyNames().iterator();
//			while (iter.hasNext()) {
//				String name = iter.next();
//				Object value = msg.getProperty(name);
//				ofMsg.setProperty(name, value);
//			}
//
//			// serverTime property
//			String serverTime = (String) ofMsg.getProperty("timeModifiedbyServer");
//			ofMsg.setServerTime(serverTime);
//
//			return ofMsg;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		return null;
//	}
//
//	/**
//	 * @return the isGroup
//	 */
//	public boolean isGroup() {
//		return isGroup;
//	}
//
//	/**
//	 * @param isGroup the isGroup to set
//	 */
//	public void setGroup(boolean isGroup) {
//		this.isGroup = isGroup;
//	}
//}
