//package fw.jbiz.ext.comms.im.impl.openfire;
//
//import org.apache.log4j.Logger;
//import org.jivesoftware.smack.Connection;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.provider.PrivacyProvider;
//import org.jivesoftware.smack.provider.ProviderManager;
//import org.jivesoftware.smackx.GroupChatInvitation;
//import org.jivesoftware.smackx.PrivateDataManager;
//import org.jivesoftware.smackx.ServiceDiscoveryManager;
//import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
//import org.jivesoftware.smackx.packet.ChatStateExtension;
//import org.jivesoftware.smackx.packet.LastActivity;
//import org.jivesoftware.smackx.packet.OfflineMessageInfo;
//import org.jivesoftware.smackx.packet.OfflineMessageRequest;
//import org.jivesoftware.smackx.packet.SharedGroupsInfo;
//import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
//import org.jivesoftware.smackx.provider.DataFormProvider;
//import org.jivesoftware.smackx.provider.DelayInformationProvider;
//import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
//import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
//import org.jivesoftware.smackx.provider.MUCAdminProvider;
//import org.jivesoftware.smackx.provider.MUCOwnerProvider;
//import org.jivesoftware.smackx.provider.MUCUserProvider;
//import org.jivesoftware.smackx.provider.MessageEventProvider;
//import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
//import org.jivesoftware.smackx.provider.RosterExchangeProvider;
//import org.jivesoftware.smackx.provider.StreamInitiationProvider;
//import org.jivesoftware.smackx.provider.VCardProvider;
//import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
//import org.jivesoftware.smackx.search.UserSearch;
//
//import fw.jbiz.ZObject;
//import fw.jbiz.common.conf.IConfig;
//import fw.jbiz.common.conf.ZSystemConfig;
//
///**
// * 
// * XMPP服务器连接工具类.
// * 
// */
//public class ZOfConnectionManager extends ZObject {
//
//	static Logger logger = Logger.getLogger(ZOfConnectionManager.class);
//	
//	private static final String PROP_KEY_OPENFIRE_SERVER = "openfire_server";
//	private static final String PROP_KEY_OPENFIRE_PORT = "openfire_port";
//	
//	private static String xmppHost = null;
//	private static Integer xmppPort = null;
//	
//	private static IConfig mConfig = ZSystemConfig.getInstance();
//
//	private XMPPConnection connection;
//	private ConnectionConfiguration connectionConfig;
//
//	private static String getHost() {
//		if (xmppHost == null) {
//			xmppHost = mConfig.getProp(PROP_KEY_OPENFIRE_SERVER);
//		}
//		
//		return xmppHost;
//	}
//	
//	private static int getPort() {
//		if (xmppPort == null) {
//			xmppPort = Integer.valueOf(mConfig.getProp(PROP_KEY_OPENFIRE_PORT));
//		}
//		
//		return xmppPort;
//		
//	}
//
//	// 可以设置配置，如果没有设置，则采用系统配置。
//	public static void setConfig(IConfig config) {
//		mConfig = config;
//	}
//	
//	public ZOfConnectionManager() {
//		init();
//	}
//
//	private boolean init() {
//		Connection.DEBUG_ENABLED = false;
//		// 配置Provider。如果不配置会无法解析数据
//		ProviderManager pm = ProviderManager.getInstance();
//		configure(pm);
//
//		connectionConfig = new ConnectionConfiguration(getHost(), getPort());
//		connectionConfig.setSASLAuthenticationEnabled(false);
//		connectionConfig
//				.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//		connectionConfig.setReconnectionAllowed(true);
//		connectionConfig.setSendPresence(false);
//		connection = new XMPPConnection(connectionConfig);
//		try {
//			connection.connect();
//			initFeatures(connection);
//			return true;
//		} catch (XMPPException e) {
//			logger.error(trace(e));
//		}
//		return false;
//	}
//
//	/**
//	 * 
//	 * 返回一个有效的xmpp连接,如果无效则返回空.
//	 * 
//	 */
//	public XMPPConnection getConnection() {
//		if (connection == null || !connection.isConnected()) {
//			init();
//		}
//		return connection;
//	}
//
//	/**
//	 * 
//	 * 销毁xmpp连接.
//	 * 
//	 */
//	public void disconnect() {
//		if (this.connection != null) {
//			if (this.connection.isConnected())
//				this.connection.disconnect();
//			this.connection = null;
//		}
//	}
//
//	private void configure(ProviderManager pm) {
//
//		 // Private Data Storage
//        pm.addIQProvider("query", "jabber:iq:private",
//                new PrivateDataManager.PrivateDataIQProvider());
//
//        // Time
//        try {
//            pm.addIQProvider("query", "jabber:iq:time",
//                    Class.forName("org.jivesoftware.smackx.packet.Time"));
//        } catch (ClassNotFoundException e) {
//        }
//
//        // Roster Exchange
//        pm.addExtensionProvider("x", "jabber:x:roster",
//                new RosterExchangeProvider());
//
//        // Message Events
//        pm.addExtensionProvider("x", "jabber:x:event",
//                new MessageEventProvider());
//
//        // Chat State
//        pm.addExtensionProvider("active",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        pm.addExtensionProvider("composing",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        pm.addExtensionProvider("paused",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        pm.addExtensionProvider("inactive",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        pm.addExtensionProvider("gone",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        // XHTML
//        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
//                new XHTMLExtensionProvider());
//
//        // Group Chat Invitations
//        pm.addExtensionProvider("x", "jabber:x:conference",
//                new GroupChatInvitation.Provider());
//
//        // Service Discovery # Items
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
//                new DiscoverItemsProvider());
//
//        // Service Discovery # Info
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
//                new DiscoverInfoProvider());
//
//        // Data Forms
//        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
//
//        // MUC User
//        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
//                new MUCUserProvider());
//
//        // MUC Admin
//        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
//                new MUCAdminProvider());
//
//        // MUC Owner
//        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
//                new MUCOwnerProvider());
//
//        // Delayed Delivery
//        pm.addExtensionProvider("x", "jabber:x:delay",
//                new DelayInformationProvider());
//
//        // Version
//        try {
//            pm.addIQProvider("query", "jabber:iq:version",
//                    Class.forName("org.jivesoftware.smackx.packet.Version"));
//        } catch (ClassNotFoundException e) {
//            // Not sure what's happening here.
//        }
//        // VCard
//        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
//
//        // Offline Message Requests
//        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
//                new OfflineMessageRequest.Provider());
//
//        // Offline Message Indicator
//        pm.addExtensionProvider("offline",
//                "http://jabber.org/protocol/offline",
//                new OfflineMessageInfo.Provider());
//
//        // Last Activity
//        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
//
//        // User Search
//        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
//
//        // SharedGroupsInfo
//        pm.addIQProvider("sharedgroup",
//                "http://www.jivesoftware.org/protocol/sharedgroup",
//                new SharedGroupsInfo.Provider());
//
//        // JEP-33: Extended Stanza Addressing
//        pm.addExtensionProvider("addresses",
//                "http://jabber.org/protocol/address",
//                new MultipleAddressesProvider());
//
//        // FileTransfer
//        pm.addIQProvider("si", "http://jabber.org/protocol/si",
//                new StreamInitiationProvider());
//
//        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
//                new BytestreamsProvider());
//
//        // Privacy
//        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
//
//        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider());
//        pm.addExtensionProvider("malformed-action",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.MalformedActionError());
//        pm.addExtensionProvider("bad-locale",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadLocaleError());
//        pm.addExtensionProvider("bad-payload",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadPayloadError());
//        pm.addExtensionProvider("bad-sessionid",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadSessionIDError());
//        pm.addExtensionProvider("session-expired",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.SessionExpiredError());
//	}
//
//
//    private static void initFeatures(XMPPConnection xmppConnection) {
//            ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppConnection);
//            if (sdm == null) {
//                    sdm = new ServiceDiscoveryManager(xmppConnection);
//            }
//            sdm.setIdentityName("Jibu_IM");
//            sdm.setIdentityType("phone");
//            sdm.addFeature("http://jabber.org/protocol/disco#info");
//            sdm.addFeature("http://jabber.org/protocol/caps");
//            sdm.addFeature("urn:xmpp:avatar:metadata");
//            sdm.addFeature("urn:xmpp:avatar:metadata+notify");
//            sdm.addFeature("urn:xmpp:avatar:data");
//            sdm.addFeature("http://jabber.org/protocol/nick");
//            sdm.addFeature("http://jabber.org/protocol/nick+notify");
//            sdm.addFeature("http://jabber.org/protocol/xhtml-im");
//            sdm.addFeature("http://jabber.org/protocol/muc");
//            sdm.addFeature("http://jabber.org/protocol/commands");
//            sdm.addFeature("http://jabber.org/protocol/si/profile/file-transfer");
//            sdm.addFeature("http://jabber.org/protocol/si");
//            sdm.addFeature("http://jabber.org/protocol/bytestreams");
//            sdm.addFeature("http://jabber.org/protocol/ibb");
//            sdm.addFeature("http://jabber.org/protocol/feature-neg");
//            sdm.addFeature("jabber:iq:privacy");
//    }
//}
