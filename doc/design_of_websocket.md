## design of websocket

* 消息协议
	
	1. websocket连接地址：webappRoot/ws
	2. client向server发送第一条报名（signup）消息：
		
			{
			"msgType":"sign_up", 
			"auth":{"userId":"xxx", "apiKey":"yyy"}, 
			"path":"addLocation"
			}
		
	3. 后续消息，采用json格式，内容由path对应的 ZWsHandler 的子类定义并校验。
	
	


* 接口(服务端)

		ZWsEndPoint extends EndPoint	
		ZWsSession extends xxx??	
		
		ZWsHandler
			abstract public boolean auth(ZWsHandlerParam); // 报名消息用
			abstract public boolean validate(ZWsHandlerParam)); // 以后每条消息用
			abstract public void subscribe(ZWsHandlerParam, String sessionId); // onSignUp前被调用			
			abstract public void onSignUp(ZWsHandlerParam));
			abstract public void onMessage(ZWsHandlerParam));
			abstract public void onSignOut(ZWsHandlerParam));			
			protect String getSessionId();	
			protect void respond(ZWsHandlerParam);
				
		ZWsHandlerParam
			msgType: sigin_in, custom, sign_out
			userId:
			apiKey:
			path:;
			abstract public static ZWsHandlerParam json2Param(String jsonMsg);
			abstract public String toJson();
			
		ZWsHandlerManager
			public static void broadcast(IResponseObject);
			public static void send(IResponseObject, sessionId);
			public static void send(IResponseObject, sessionIdList);			
			public static ZWsSession getSession(sessionId);
			public static void closeSession(sessionId);
			public static List<ZWsSession> getSessionList(sessionIdList);
			
			public static void initialize();
		
		ZWsEventChannel
			public static void subscribe(String channelId, String sessionId);
			public static void unSubscribe(String channelId, String sessionId);
			public static List<channelId> getSubscribedList(String sessionId);
			
			public static void publish(String channelId, IResponseObject);			
				
		XxxWsHandlerParam extends ZWsHandlerParam

		XxxWsHandler extends ZWsHandler	
		
* 接口(客户端js)


		// 原型模式
		function ZWsClient() {
		
		};
		
		ZWsClient.prototype = {
			constructor: ZWsClient,
			autoReconnect: true,
			checkRate: 5, // 秒
			connectUrl: "",
			onOpen: "",
			onClose: "",
			onMessage: "",
			onError:"",
			initialize: function(_connectUrl, _onOpen, _onClose, _onMessage, _onError) {
				connectUrl = _connectUrl;
				onOpen = _onOpen;
				onClose = _onClose;
				onMessage = _onMessage;

			}
					
			sendMessage = function(jsonMsg) {
				// todo
			}
			
			connect = function() {
				// todo
			}
			disconnect = function() {
				// todo
			}
		};
					
			
		
					
	