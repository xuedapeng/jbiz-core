//package fw.jbiz.ext.comms.im.impl.openfire;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.jivesoftware.smack.PacketCollector;
//import org.jivesoftware.smack.SmackConfiguration;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.filter.PacketIDFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.packet.Registration;
//import org.jivesoftware.smack.util.StringUtils;
//import org.jivesoftware.smackx.Form;
//import org.jivesoftware.smackx.FormField;
//import org.jivesoftware.smackx.muc.DiscussionHistory;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//
//
//import fw.jbiz.ZObject;
//import fw.jbiz.ext.comms.im.bean.ZImMsgBean;
//
///**
// * <pre>
// * 
// * 1.取得实例的时候要指定用户名，密码。
// *     ZOfChatManager test = ZOfChatManager.getInstance("test01", "test01");
// *   取得上述实例后可以用指定的用户名和密码注册一个新用户。
// *     test.accountRegist(account, password)
// *     
// * 2.取得上述实例后可以创建一个新的聊天室。
// *     test.createRoom(roomName)
// *   可以邀请成员加入一个指定聊天室
// *     test.inviteParticipant(userList, roomName)
// *   可以把成员从一个指定聊天室中踢出去
// *     test.kickParticipant(userList, roomName)
// *     
// * 3.也可以不指定用户名，密码来取得实例。在注册用户的时候可以使用。注册用户之外的操作都无法进行。
// *     ZOfChatManager test = ZOfChatManager.getInstance();
// *   取得上述实例后可以用指定的用户名和密码注册一个新用户。
// *     test.accountRegist(account, password)
// * 
// * </pre>
// */
//public class ZOfImManager extends ZObject {
//
//	static Logger logger = Logger.getLogger(ZOfImManager.class);
//	
//	private final static int  POOL_SIZE_NO_LOGIN = 5;
//	private final static int  POOL_SIZE_ONE_TIME_LOGIN = 10;
//	private final static int  POOL_SIZE_KEEP_LOGIN = 5;
//	
//	
//	public ZOfImManager() {
//	}
//
//	/** 实例： 不登录操作, 操作完成后保持连接 */
//	private static List<ZOfImManager> instanceNoLoginPool;
//	/** 实例： 需登录操作，操作完成后断开，必须断开否则干扰离线消息 */
//	private static List<ZOfImManager> instanceOneTimeLoginPool;
//	/** 实例： 需登录操作，操作完成后保持连接和登录状态，下次可继续用的 */
//	private static List<ZOfImManager> instanceKeepLoginPool;
//	// 保持登录连接实例的account
//	private static String[] accountArrayOfKeepLogin;
//	
//	private static int lastHitOfNoLogin = -1; 
//	private static int lastHitOfOneTimeLogin = -1; 
//	private static int lastHitOfKeepLogin = -1; 
//	
//	private static synchronized int getNextHitOfNoLogin() {
//		
//		lastHitOfNoLogin =  
//				(lastHitOfNoLogin < POOL_SIZE_NO_LOGIN-1? lastHitOfNoLogin+1 : 0);
//		return lastHitOfNoLogin;
//		
//	}
//
//	private static synchronized int getNextHitOfOneTimeLogin() {
//
//		lastHitOfOneTimeLogin =  
//				(lastHitOfOneTimeLogin < POOL_SIZE_ONE_TIME_LOGIN-1? lastHitOfOneTimeLogin+1 : 0);
//		return lastHitOfOneTimeLogin;
//		
//	}
//	
//	private static synchronized int getNextHitOfKeepLogin() {
//
//		lastHitOfKeepLogin =  
//				(lastHitOfKeepLogin < POOL_SIZE_KEEP_LOGIN-1? lastHitOfKeepLogin+1 : 0);
//		return lastHitOfKeepLogin;
//		
//	}
//	
//	// 将实例放入实例池
//	static {
//		instanceNoLoginPool = makeInstance(POOL_SIZE_NO_LOGIN);
//		instanceOneTimeLoginPool = makeInstance(POOL_SIZE_ONE_TIME_LOGIN);
//		instanceKeepLoginPool = makeInstance(POOL_SIZE_KEEP_LOGIN);
//		accountArrayOfKeepLogin = new String[POOL_SIZE_KEEP_LOGIN];
//		
//	}
//	
//	// 生成实例
//	private static List<ZOfImManager> makeInstance(int num) {
//		List<ZOfImManager> list = new ArrayList<ZOfImManager>();
//		for (int i=0; i<num; i++) {
//			list.add(new ZOfImManager());
//		}
//		
//		return list;
//	}
//
//
//
//	// 获取无登录实例，按顺序从池中拿取
//	private static synchronized ZOfImManager getInstanceNoLoginFromPool() {
//		
//		int index = getNextHitOfNoLogin();
//		
//		logger.info(String.format(
//						"nextHitOfNoLogin:%d",
//						index));
//		
//		return instanceNoLoginPool.get(index);
//	}
//	
//	// 获取临时登录实例
//	private static synchronized ZOfImManager getInstanceOneTimeLoginFromPool() {
//
//		ZOfImManager result = null;
//
//		int index = getNextHitOfOneTimeLogin();
//		
//		logger.info(String.format(
//				"nextHitOfOneTimeLogin:%d",
//				index));
//		
//		result = instanceOneTimeLoginPool.get(index);
//		result.disconnect();
//		
//		return result;
//	}
//	
//	
//	// 获取持续登录实例
//	private static synchronized ZOfImManager getInstanceKeepLoginFromPool(String account) {
//		
//		ZOfImManager result = null;
//		
//		// 如果池中有相同用户的连接，返回该连接
//		for (int i=0; i< accountArrayOfKeepLogin.length; i++) {
//			if (account.equals(accountArrayOfKeepLogin[i])) {
//				result = instanceKeepLoginPool.get(i);
//				
//				logger.info(String.format(
//						"account: %s arrayIndex:%d",
//						account, i));
//				
//				break;
//			}
//		}
//		
//		// 如果池中没有相同用户的连接，按顺序抽取,并强制切断
//		if (result == null) {
//			int index = getNextHitOfKeepLogin();
//			
//			logger.info(String.format(
//					"nextHitOfKeepLogin:%d",
//					index));
//			
//			result = instanceKeepLoginPool.get(index);
//			accountArrayOfKeepLogin[index] = account;
//			result.disconnect();
//		}
//		
//		return result;
//	}
//	
//
//	// 不登录操作, 操作完成后保持连接的实例
//	public static ZOfImManager getInstanceNoLogin() {
//		ZOfImManager instance = getInstanceNoLoginFromPool();
//		instance.getConnection();
//		return instance;
//	}
//	
//    // 登录并操作完成后，断开连接的实例
//	public static ZOfImManager getInstanceOneTimeLogin(String account, String password) {
//
//		if (account==null) {
//			return null;
//		}
//		
//		ZOfImManager instance = getInstanceOneTimeLoginFromPool();
//		
//		// 保存用户名，密码，聊天室
//		instance.username = ZOfUtils.getBareJID(account);
//		instance.password = password;
//
//		// 连接服务器
//		instance.getConnection();
//		// 尝试登录
//		boolean result = instance.login();
//		
//		logger.info(String.format(
//				"info: login result:%s",
//				result?"true":"false"));
//
//		return instance;
//	}
//
//	// 获得用户 如果断线就重新连接
//
//	public static ZOfImManager getInstanceKeepLogin(String account,
//			String password) {
//		
//		if (account==null) {
//			return null;
//		}
//
//		ZOfImManager instance = getInstanceKeepLoginFromPool(account);
//		
//		// 保存用户名，密码，聊天室
//		instance.username = ZOfUtils.getBareJID(account);
//		instance.password = password;
//
//		// 如果已经登录，直接使用(一般情况下，已经登录)
//		String loginUser = instance.getConnection().getUser();
//		if (loginUser != null) {
//			return instance;
//		} else {
//			// 连接服务器
//			instance.getConnection();
//			// 尝试登录
//			instance.login();
//			
//			logger.info(String.format(
//					"warning: No connecting user! account:%s",
//					account));
//		}
//		return instance;
//	}
//
//
//	/** 设置资源名 */
//	private String resourceName = "jbiz-server";
//
//	/** 与openfire服务器连接对象 */
//	private XMPPConnection connection = null;
//
//	/** 用户名称。不能带@符，不能带斜杠 */
//	private String username;
//
//	/** 用户密码 */
//	private String password;
//	
//	/**
//	 * 返回登陆中的用户名。必须在用户登录后执行。 返回"user@jbiz.net.cn/jbiz"格式中的"user"字串
//	 */
//	public String getMyname() {
//		return username;
//	}
//
//	public void disconnect() {
//		if (this.connection == null || !this.connection.isConnected()) {
//
//		} else {
//			this.connection.disconnect();
//			this.connection = null;
//		}
//	}
//
//	/**
//	 * 取得OPENFIRE服务器的连接
//	 * 
//	 * @return boolean
//	 */
//	private XMPPConnection getConnection() {
//		// 取得服务器连接对象
//		if (this.connection == null || !this.connection.isConnected()) {
//			try {
//				ZOfConnectionManager xmppConnectionManager = new ZOfConnectionManager();
//				this.connection = xmppConnectionManager.getConnection();
//			} catch (Exception e) {
//				logger.error("Openfire服务器连接失败");
//				logger.error(trace(e));
//			}
//		}
//		return this.connection;
//	}
//
//	/**
//	 * 账户登录
//	 * 
//	 * @return boolean
//	 */
//	private boolean login() {
//		if (getConnection() == null) {
//			return false;
//		}
//
//		// 未设置用户名，密码，无法登陆
//		if (this.username == null || this.password == null) {
//			logger.error("未设置用户名，密码，无法登陆");
//			return false;
//		}
//
//		// 已经登陆
//		if (getConnection().getUser() != null) {
//			return true;
//		}
//
//		try {
//			// 指定资源名登陆
//			getConnection().login(this.username, this.password, resourceName);
//
//			// 更改在线状态
//			Presence presence = new Presence(Presence.Type.available);
//			getConnection().sendPacket(presence);
//
//		} catch (XMPPException e) {
//			logger.error("账户登录失败。用户名：" + username);
//			logger.error(trace(e));
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 发送单聊消息。ofMsg.to和body必须要设置。</br>
//	 * 
//	 * @param ofMsg
//	 * 
//	 * @return boolean
//	 */
//	public boolean sendChatMsg(ZImMsgBean ofMsg) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		if ((ofMsg.getTo() == null || "".equals(ofMsg.getTo()))) {
//			return false;
//		}
//
//		// 生成一个新的消息
//		Message message = new Message();
//		// 增加ID长度，避免重复
//		String chatIdPrefix = StringUtils.randomString(25);
//
//		message.setPacketID(chatIdPrefix + message.getPacketID());
//		// 设置消息类型为单聊
//		message.setType(Message.Type.chat);
//		message.setTo(getFullJID(ofMsg.getTo(), this.connection));
//		message.setBody(ofMsg.getBody());
//
//		// properties
//		Iterator<String> iter = ofMsg.getPropertyNames().iterator();
//		while (iter.hasNext()) {
//			String name = iter.next();
//			Object value = ofMsg.getProperty(name);
//			message.setProperty(name, value);
//		}
//
//		// serverTime property
//		message.setProperty("timeModifiedbyServer", "");
//		connection.sendPacket(message);
//
//		return true;
//	}
//
//	/**
//	 * 账户注册。在没有用户登录的情况下也能执行。
//	 * 
//	 * @param account
//	 *            注册帐号（userId）
//	 * @param password
//	 *            注册密码
//	 * @return 1、注册成功 0、服务器没有返回结果 2、这个账号已经存在 3、注册失败
//	 */
//	public int accountRegist(String account, String password) {
//		if (getConnection() == null) {
//			return 0;
//		}
//
//		Map<String, String> attributes = new HashMap<String, String>();
//		Registration reg = new Registration();
//		reg.setType(IQ.Type.SET);
//		reg.setTo(getConnection().getServiceName());
//		// 注意这里createAccount注册时，参数是username，不是jid。是“@”前面的部分
//		attributes.put("username", account);
//		attributes.put("password", password);
//		attributes.put("jbizserver", "createUser_jbizserver");
//		reg.setAttributes(attributes);
//		PacketFilter filter = new AndFilter(new PacketIDFilter(
//				reg.getPacketID()), new PacketTypeFilter(IQ.class));
//		PacketCollector collector = getConnection().createPacketCollector(
//				filter);
//		getConnection().sendPacket(reg);
//		IQ result = (IQ) collector.nextResult(SmackConfiguration
//				.getPacketReplyTimeout());
//		// Stop queuing results停止请求results返回是否成功
//		collector.cancel();
//		if (result == null) {
//			// No response from server
//			return 0;
//		}
//		logger.debug(result.toXML());
//		if (result.getType() == IQ.Type.RESULT) {
//			// regist success
//			return 1;
//		}
//		// if (result.getType() == IQ.Type.ERROR)
//		if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
//			// IQ.Type.ERROR: conflict(409)
//			return 2;
//		}
//		// IQ.Type.ERROR: result.getError().toString());
//		return 3;
//	}
//
//	/**
//	 * 删除当前用户。必须在用户登录后执行。
//	 * 
//	 * @return boolean
//	 */
//	public boolean accountDelete() {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//		try {
//			getConnection().getAccountManager().deleteAccount();
//		} catch (XMPPException e) {
//			logger.error("{login:" + this.username + "}删除用户失败。用户名：" + username);
//			logger.error(trace(e));
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 创建指定群聊房间。必须在用户登录后执行。
//	 * 
//	 * @param roomName
//	 *            房间名称（群ID）
//	 * 
//	 * @return boolean
//	 */
//	public boolean createRoom(String roomName) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		roomName = ZOfUtils.getFullRoomID(roomName, this.connection);
//		// 创建MultiUserChat
//		MultiUserChat muc = new MultiUserChat(getConnection(), roomName);
//		try {
//			// 创建聊天室昵称
//			muc.create(this.username);
//			// 获得聊天室配置表单
//			Form form = muc.getConfigurationForm();
//			// 根据原始表单创建要提交的表单
//			Form submitForm = form.createAnswerForm();
//			// 向要提交的表单添加确认答复
//			for (Iterator<FormField> fields = form.getFields(); fields
//					.hasNext();) {
//				FormField field = (FormField) fields.next();
//				if (!FormField.TYPE_HIDDEN.equals(field.getType())
//						&& field.getVariable() != null) {
//					// 设置默认值作为答复
//					submitForm.setDefaultAnswer(field.getVariable());
//				}
//			}
//			// 设置聊天室的拥有者
//			List<String> owners = new ArrayList<String>();
//			owners.add(getConnection().getUser());// 用户JID
//			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
//			// 设置聊天室是持久天室。即会被保存下来
//			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//			// 房间成员
//			submitForm.setAnswer("muc#roomconfig_membersonly", false);
//			// 允许邀请其他人
//			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
//			// 登录房间对话
//			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
//			// 允许注册的昵称登陆
//			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
//			// 允许使用者修改昵称
//			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
//			// 允许用户注册房间
//			submitForm.setAnswer("x-muc#roomconfig_registration", true);
//			// 发送已完表单（有默认值到服务器来配置聊天室
//			muc.sendConfigurationForm(submitForm);
//
//		} catch (XMPPException e) {
//			// 创建聊天室失败可能是因为聊天室已经存在
//			logger.error("{login:" + this.username + "}创建群聊房间失败。房间名："
//					+ roomName);
//			logger.error(trace(e));
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 删除指定群聊房间。必须在用户登录后执行。
//	 * 
//	 * @param roomName
//	 *            房间名称（群ID）
//	 * 
//	 * @return boolean
//	 */
//	public boolean destroyRoom(String roomName) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		roomName = ZOfUtils.getFullRoomID(roomName, this.connection);
//
//		// 创建MultiUserChat
//		MultiUserChat muc = new MultiUserChat(getConnection(), roomName);
//		try {
//			// JID需要附带资源名，否则报例外
//			muc.destroy("",
//					ZOfUtils.getFullJIDWithResource(roomName, connection));
//		} catch (XMPPException e) {
//			logger.error("{login:" + this.username + "}删除群聊房间失败。房间名："
//					+ roomName);
//			logger.error(trace(e));
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 把指定用户踢出指定群。必须在用户登录后执行。
//	 * 
//	 * @param users
//	 *            剔除人员一览
//	 * @param roomName
//	 *            聊天室名
//	 * 
//	 * @return boolean
//	 */
//	public boolean kickParticipant(List<String> users, String roomName) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		roomName = ZOfUtils.getFullRoomID(roomName, this.connection);
//		MultiUserChat muc = new MultiUserChat(this.getConnection(), roomName);
//
//		// 先自己进入聊天室
//		this.joinRoom(muc);
//
//		Iterator<String> iter = users.iterator();
//		while (iter.hasNext()) {
//			String userName = iter.next();
//			try {
//				// 根据昵称来踢人，所以不用完全JID
//				muc.kickParticipant(userName, "你已经被管理员踢出了本聊天室。");
//			} catch (XMPPException e) {
//				logger.error("{login:" + this.username + "}无法剔出聊天室。聊天室名："
//						+ roomName + ", 用户昵称:" + userName);
//				logger.error(trace(e));
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 加入指定群聊房间。必须在用户登录后执行。
//	 * 
//	 * @param roomName
//	 * 
//	 * @return boolean
//	 */
//	private boolean joinRoom(MultiUserChat muc) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		if (muc.isJoined()) {
//			// 已经加入了
//			return true;
//		}
//
//		try {
//			// 聊天室服务指定要接受的聊天记录数
//			DiscussionHistory history = new DiscussionHistory();
//			history.setMaxStanzas(0);
//			// 进入聊天室
//			muc.join(this.username, null, history, 10000);
//		} catch (XMPPException e) {
//			logger.error("{login:" + this.username + "}加入聊天室失败。聊天室名："
//					+ muc.getRoom());
//			logger.error(trace(e));
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 把指定用户加进指定群（发邀请）。必须在用户登录后执行。
//	 * 
//	 * @param users
//	 *            邀请人员一览
//	 * @param roomName
//	 *            聊天室名
//	 * 
//	 * @return boolean
//	 */
//	public boolean inviteParticipant(List<String> users, String roomName) {
//		if (getConnection() == null || this.connection.getUser() == null) {
//			return false;
//		}
//
//		roomName = ZOfUtils.getFullRoomID(roomName, this.connection);
//		MultiUserChat muc = new MultiUserChat(this.getConnection(), roomName);
//
//		// 先自己进入聊天室
//		this.joinRoom(muc);
//
//		Iterator<String> iter = users.iterator();
//		while (iter.hasNext()) {
//			String userName = iter.next();
//			// 邀请的时候要指定完整JID
//			userName = ZOfUtils.getFullJID(userName, connection);
//			muc.invite(userName, "");
//		}
//		return true;
//	}
//
//	private String getFullJID(String username, XMPPConnection connection) {
//		int atIndex = username.lastIndexOf("@");
//		if (atIndex >= 0) {
//			username = username.substring(0, atIndex);
//		}
//		return username + "@" + connection.getServiceName();
//	}
//}
