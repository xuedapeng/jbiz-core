1. 问题

    在action中获取到post参数后，需要逐个set到logicParam中。
    如果参数很多，action中方法的参数就要写很多，而且逐个set到logicParam的代码也会冗长。

2. 办法

    在action的方法中，把post参数当成json字符串一次获取，然后将json的key／value通过反射机制自动设定到logicParam中。

3. 步骤

 
	1. action方法增加注解，Consumes("application/json")
		
			@POST
			@Consumes("application/json")
			@Path("performanceJson.do")
			public String performanceJson(
				String json,
				@Context HttpServletRequest request) {

				PerformanceLogicParam myParam = new PerformanceLogicParam(null, null, request);
				setParamValues(myParam, json);	
				return new PerformanceT3Logic().process(myParam);
			}
	
   
   
	1. 将json设置到LogicParam中，包括父类属性。
	
			
			public static void setParamValues(ZLogicParam logicParam, String jsonStr) {

				// 获取 参数名－方法 map
				Map<String, Method> methodMap = new HashMap<String, Method>();
				Method[] methods = logicParam.getClass().getMethods();
		 		for (Method method : methods) {  
			 
	        	    String methodName = method.getName();  
	
		            if (methodName.startsWith("set")) {

		           	 String paramName = methodName.substring(3); // 去掉set
		           	 paramName = paramName.substring(0, 1).toLowerCase() + paramName.substring(1); 
		      	      methodMap.put(paramName, method);
	          	  }
				 }

				// 遍历参数
				Map<String, Object> jsonMap = JsonHelper.jsonStr2Map(jsonStr);
 				for(String key: jsonMap.keySet()) {
 					String paramName = key;
 					Object paramValue = jsonMap.get(key);
 					Method method = methodMap.get(paramName);
 					if (method == null) {
 						// 没找到，忽略
 					} else {

 						try {
							method.invoke(logicParam, paramValue);
					
						} catch (Exception e) {
							logger.error(trace(e));
						}
 					}
 				}
			}
	
	

	1. 客户端设置
		
		ajax调用时，需要明确设置contentType＝"application/json; charset=utf-8"
		
				 $.ajax({
		  		  
		  		    contentType: "application/json; charset=utf-8",
		  	        type    : "POST",
		  	        url     : url,
		  	        data    : data4log,
		  	        dataType: "json",
		  	        
		  	        （略）
		  	        

1. 注意事项

	自动设置属性可以减少冗余代码，提高开发效率，避免手误产生的bug。
	
	但是，
	1. 反射的开销较大，每次请求都使用反射设置值，对效率不利。对于不频繁的请求，可以考虑使用。
	1. 自动设定时，setParamValues方法会把json的key映射成param的属性，如果param没有该属性，则json的该key和value被忽略。所以，必须在logic的validate方法中实施校验。
	1. 另外，setParamValues在对param属性进行强制设置时，只有param的方法名以set开头且余下部分与key匹配，就会调用改方法并设置value，请确保param及其父类的所有方法，不会被非法的json参数破坏。
				