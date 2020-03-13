# 编写json-rpc风格的restful api

## 什么是json-rpc风格
*  json-rpc是基于json的跨语言远程调用协议。这里主要讲基于http协议的json-rpc实现。
* 传输参数json格式如下：


        { 
        "method": "sayHello", 
        "params": ["Hello JSON-RPC"], 
        "id": 1
        }
         
    其中参数由三部分构成：

        method： 调用的方法名
        params： 方法传入的參数。若无參数则传入 []
        id ： 调用标识符。用于标示一次远程调用过程
        
* 返回值json格式如下：

         {   
        "result":"Hello JSON-RPC",         
        "error":null,       
        "id":1
         } 
         
    其中返回值由三部分构成：
    
        result: 方法返回值。若无返回值。则返回null。若调用错误，返回null。
        error ：调用时错误，无错误返回null。
        id : 调用标识符，与调用方传入的标识符一致。   
 
## 与restful api的区别

* restful api通常使用url来定位需要调用的方法，而json-rpc则在使用method参数来定位方法，json-rpc的url是一个固定不变的。
   
## jbiz的实现思路

*  使用单一action来接收所有请求(DispatchAction)；
*  根据请求参数中的method参数值确定需要调用的logic
   (增加标注例：@Action(method="user.add"))；
*  将params参数影射到logicParam。

可以看出，只需要对aciton层进行适当的改造即可，而对logic层没有影响（少许改动，增加标注，见下文） 。

## 实现

* 标注类: Action.java

        @Retention(RetentionPolicy.RUNTIME)
        @Target(value={ElementType.TYPE})
        public @interface Action {
	
	        public String method();

        }
* 在logic类上添加Action标注，指明该logic处理哪个method请求。

        @Action(method="user.add")
        public class UserAddLogic extends BaseZLogic {
           ...
        }
        
* 单一action类: DispatchAction.java
    
    1. 扫描logic上的Action标注，并将结果保存在map中；
    2. 在dispatch方法解析参数、生成logicParam、确定logic并调用。

        DispatchAction.java
    
		    @Consumes("application/json;charset=UTF-8")
		    @Produces("application/json;charset=UTF-8")
		    @Path("/")
		    public class DispatchAction extends BaseZAction {
		
			static Logger logger = Logger.getLogger(DispatchAction.class);
		
			static final String LOGIC_PKG = "cloud.jbus.logic";
		
			static {
				
				scanLogicClass(LOGIC_PKG);
			}
			
			@POST
			@Path("/")
			public String dispatch(
					String params,
					@Context HttpServletRequest request) {
				
				// params schema: {"method":"login", "auth":[secretId, secretKey],"data":{}}，
				// 这里的schema可自己定义，并 checkSchema方法中实施check
				
				IResponseObject res = new  ZGsonObject();
				
				// 参数有效性
				Map<String, Object> mapParams = this.checkSchema(params, res);
				
				if (mapParams == null) {
					return res.toString();
				}
				
				// 设置参数
				ZLogicParam logicParam = this.makeParam(mapParams, res, request);
				if (logicParam == null) {
					return res.toString();
				}
				
				// 调用logic
				ZLogic logic = this.makeLogic(mapParams, res);
				if (logic == null) {
					return res.toString();
				}
				
				return logic.process(logicParam);
			}
		
		    }
		    
	3. BaseZAction中实现注解扫描、logic定位、schema校验等方法。
	    
	    BaseZAction.java
	    
	        参考附件。


## 调用

* url: 

        http://localhost:8080/jbuscloud/api/
        
* jsondata:
        
        {"method":"user.add", 
        "data":{"nickName":"tom","age":"18",...},
        "auth":["1234","5678"]
        }
        
 如果要严格遵循json-rpc规范，应该讲jsondata定义成method／params／id的模式。
 
        
    
## 附件


BaseZAction.java


     
	public class BaseZAction extends ZAction {
	
		static Logger logger = Logger.getLogger(BaseZAction.class);
	
		// method, ZLogic
		static Map<String, Class<? extends ZLogic>> _logicClassMap = new HashMap();
		// method, BaseZLogicParam
		static Map<String, Class<? extends BaseZLogicParam>> _logicParamClassMap = new HashMap();
		
		// make logic
		protected ZLogic makeLogic(Map<String, Object> mapParams, final IResponseObject res) {
			
			try {
				ZLogic logic = _logicClassMap.get(mapParams.get("method")).newInstance();
				
				return logic;
				
				
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("", e);
				
				res.add("status", -1)
				.add("msg", "unkown error!");
				
				return null;
			}
			
			
			
		}
		// make param
		protected ZLogicParam makeParam(Map<String, Object> mapParams, final IResponseObject res, final HttpServletRequest request) {
	
			try {
				BaseZLogicParam logicParam = _logicParamClassMap.get(mapParams.get("method")).newInstance();
			
				// 设置 auth
				if (mapParams.containsKey("auth")) {
		
					List<String> auth = (List<String>)mapParams.get("auth");
					if (auth.size() == 2) {
						String secretId = auth.get(0);
						String secretKey = auth.get(1);
						logicParam.setSecretId(secretId);
						logicParam.setSecretKey(secretKey);
					}
				}
				
				// 设置request
				logicParam.setRequest(request);
				
				// 设置属性
				if (mapParams.containsKey("data")) {
					Map data = (Map) mapParams.get("data");
					BeanHelper.setValuesFromJson(logicParam, JsonHelper.map2json(data));
				}
	
				return logicParam;
				
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("", e);
				
				res.add("status", -1)
				.add("msg", "unkown error!");
				
				return null;
			}
	
		}
		
		// check schema
		protected Map<String, Object> checkSchema(String params, final IResponseObject res) {
	
			Map<String, Object> mapParams;
			
			try {
				mapParams = JsonHelper.json2map(params);
				if (!mapParams.containsKey("method")) {
					res.add("status", -11)
						.add("msg", "need method!");
					return null;
				}
				
				String method = (String)mapParams.get("method");
				
				if (!_logicClassMap.containsKey(method)) {
					res.add("status", -11)
						.add("msg", "invalid method!");
					return null;
				}
				
				if (mapParams.containsKey("auth")) {
	
					List<String> auth = (List<String>)mapParams.get("auth");
					if (auth.size() != 2) {
						res.add("status", -11)
							.add("msg", "invalid auth!");
						return null;
					}
				}
				
			
			} catch(Exception e) {
				
				logger.error("", e);
				
				res.add("status", -11)
					.add("msg", "参数格式错误");
				
				return null;
			}
			
			return mapParams;
		}
		
		// gether logic annotation info
		protected static void scanLogicClass(String packageName) {
	
			Map<String, Annotation> annMap = AnnotationHelper.getAnnotationOnClass(packageName, Action.class);
			
			for(String clsName: annMap.keySet()) {
				Action annotation = (Action)annMap.get(clsName);
				String method = annotation.method();
				
				// path 不可重复
				if (_logicClassMap.containsKey(method)) {
					String message = String.format("duplicate method for Logic Class: class=%s, method=%s", 
							clsName, method);
							
					logger.error(message);
					throw new ZException("jbus-cloud", message);
				}
				
				try {
	
					Class<? extends ZLogic> logicClass = (Class<? extends ZLogic>) Class.forName(clsName);
					
					_logicClassMap.put(method, logicClass);
					
					String pkg = logicClass.getPackage().getName();
					String clzName = logicClass.getSimpleName();
					
					// logic.param.XxxLogicParam;
					String paramClsName = pkg + ".param." + clzName + "Param";
	
					Class<? extends BaseZLogicParam> paramClass = (Class<? extends BaseZLogicParam>) Class.forName(paramClsName);
	
					_logicParamClassMap.put(method, paramClass);
	
					logger.info(String.format("logic class found:\n method=%s, class=%s", method, clsName));
					logger.info(String.format("logicParam class found:\n method=%s, class=%s", method, paramClsName));
				} catch (ClassNotFoundException e) {
							
					logger.error(trace(e));
					throw new ZException("jbus-cloud", e);
				}
			}
			
			if (_logicClassMap.isEmpty()) {
				
				String msg = String.format("not logic class found in %s", packageName);
				logger.error(msg);
				throw new ZException("jbus-cloud", msg);
			}
		}
	}
  
     
   