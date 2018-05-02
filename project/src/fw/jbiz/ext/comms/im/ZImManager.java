//package fw.jbiz.ext.comms.im;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import fw.jbiz.ZObject;
//import fw.jbiz.common.conf.IConfig;
//import fw.jbiz.common.conf.ZSystemConfig;
//import fw.jbiz.ext.comms.im.bean.ZImAccount;
//import fw.jbiz.ext.comms.im.bean.ZImMsgBean;
//import fw.jbiz.ext.comms.im.bean.ZImResult;
//import fw.jbiz.ext.comms.im.interfaces.IImProvider;
//
//public class ZImManager extends ZObject {
//
//	static Logger logger = Logger.getLogger(ZImManager.class);
//	
//	private static final String PROP_PROVIDER_CLASS = "im_provider_class";
//	private static IImProvider mProvider = null;
//	private static IConfig mConfig = ZSystemConfig.getInstance();
//	
//	public static void setConfig(IConfig config) {
//		mConfig = config;
//	}
//	
//	// 设置提供商的实现类
//	public static void setProvider(IImProvider provider) {
//		mProvider = provider;
//	}
//	
//	private static IImProvider getProvider()  {
//		if (mProvider == null) {
//			String className = mConfig.getProp(PROP_PROVIDER_CLASS);
//			try {
//				mProvider = (IImProvider)Class.forName(className).newInstance();
//				mProvider.setConfig(mConfig);
//			} catch (InstantiationException | IllegalAccessException
//					| ClassNotFoundException e) {
//				
//				logger.error(trace(e));
//			}
//		}
//		
//		return mProvider;
//	}
//	
//	// 发送消息, 单个，同步
//	public static ZImResult sendChatMsg(ZImMsgBean imMsgBean, ZImAccount account) {
//		
//		return getProvider().sendChatMsg(imMsgBean, account);
//	}
//	
//
//	// 发送消息, 多个，同步
//	public static List<ZImResult> sendChatMsg(List<ZImMsgBean> imMsgBeanList, ZImAccount account) {
//		
//		List<ZImResult> resultList = new ArrayList<ZImResult>();
//		
//		for (ZImMsgBean imMsgBean: imMsgBeanList) {
//			ZImResult result = getProvider().sendChatMsg(imMsgBean, account);
//			resultList.add(result);
//		}
//		
//		return resultList;
//	}
//
//	// 发送消息, 单个，异步
//	public static ZImResult asyncSendChatMsg(final ZImMsgBean imMsgBean, final ZImAccount account) {
//
//		final IImProvider thisProvider = getProvider();
//		
//		new Thread(new Runnable() {
//			public void run() {
//				thisProvider.sendChatMsg(imMsgBean, account);
//			}
//		}).start();
//		
//		return null;
//	}
//	
//	// 发送消息, 多个，异步
//	public static List<ZImResult> asyncSendChatMsg(final List<ZImMsgBean> imMsgBeanList, final ZImAccount account) {
//
//		final IImProvider thisProvider = getProvider();
//		
//		new Thread(new Runnable() {
//			public void run() {
//				
//				List<ZImResult> resultList = new ArrayList<ZImResult>();
//				
//				for (ZImMsgBean imMsgBean: imMsgBeanList) {
//					ZImResult result = thisProvider.sendChatMsg(imMsgBean, account);
//					resultList.add(result);
//				}
//			}
//		}).start();
//		
//		return null;
//	}
//
//	// 注册账号
//	public static ZImResult registerAccount(ZImAccount account) {
//		
//		return getProvider().registerAccount(account);
//		
//	}
//	
//	// 注销账号(当前登录账号)
//	public static ZImResult removeAccount(ZImAccount account) {
//		
//		return getProvider().removeAccount(account);
//		
//	}
//	
//	// 创建聊天室
//	public static ZImResult createRoom(String roomName, ZImAccount account) {
//		
//		return getProvider().createRoom(roomName, account);
//		
//	}
//	
//	// 删除聊天室
//	public static ZImResult destroyRoom(String roomName, ZImAccount account) {
//		
//		return getProvider().destroyRoom(roomName, account);
//		
//	}
//
//	// 邀请加入聊天室
//	public static ZImResult inviteParticipant(
//			List<String> users, String roomName, ZImAccount account){
//		
//		return getProvider().inviteParticipant(users, roomName, account);
//		
//	}
//	
//	// 踢出聊天室
//	public static ZImResult kickParticipant(
//			List<String> users, String roomName, ZImAccount account) {
//
//		return getProvider().kickParticipant(users, roomName, account);
//	}
//	
//}
