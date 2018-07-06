## 并发1k的tomcat＋mysql配置实践
### 目标
* 1000用户并发访问api；（数量）
* api响应时间小于5秒；（效率）
* 超出1000时，可拒绝连接，但必须确保tomcat、mysql服务本身不宕机；（质量）

### 思路
1.  为了满足1k并发访问，tomcat允许接受（含排队等候）的线程数必须不小于1k，即 acceptCount >= 1000; 同时，为了及时关闭闲置连接，可以connectionTimeout设置小一点。

2.  在系统资源允许的情况下，tomcat处理线程尽可能大一点，以减少排队等候时间，但不必超过1k。所以可以先把maxThreads 设置为1000，观察cpu和内存消耗情况，如果耗尽，可酌情减少。

3. 为tomcat分配充足的系统资源,以避免满载时出现OutOfMemory。服务器用途应以tomcat服务为主，考虑将操作系统以及周边程序运行必要的内存之外的全部内存分配给tomcat。

4. 为tomcat提供足够的数据库连接。在满载极端情况下，每个线程都需要一个db连接，也就是需要至少1k的连接数。极端情况下，线程如果分片使用连接池中的连接，新的连接请求频繁失败时，将会导致c3p0的死锁异常，从而致使tomcat服务宕机。
 
5. mysql服务器应提供至少大于1k的连接，考虑其它应用的连接，可以将max_connection设置为2k， 以避免超出限制的连接请求而导致的“too many connections“错误。

6. 以上可基本保证的并发访问的数量和质量，如果响应时间打不到小于5秒的目标，则需要配置2台tomcat服务器分担负载。对于1k的并发，不需要考虑数据库集群。如果出现db连接的瓶颈，则需要检查db访问相关的代码是否低效。


### 实践

1. 环境

	2台独立阿里云服务器。
	* web server：ubuntu14/tomcat7/cpu2.6G 2core/mem8G
	* db server: ubuntu14/mysql5.6/cpu2.6G 2core/mem8G
	
1. tomcat配置
	* server.xml
	
			<Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
               connectionTimeout="2000"
               URIEncoding="UTF-8"
               redirectPort="8443" 
               maxThreads="1000" acceptCount="1200"
               />

	* tomcat 启动选项
	
			export JAVA_OPTS="$JAVA_OPTS -Xms2048m -Xmx6144m -XX:PermSize=32m -XX:MaxPermSize=1024m -Xss2m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Djava.awt.headless=true"
			

1. 连接池配置 

	* persistence.xml
	
			<!-- c3p0 连接池配置 -->
			<property name="hibernate.connection.provider_class"
				value="org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider" />
			<property name="hibernate.c3p0.min_size" value="5" />
			<property name="hibernate.c3p0.max_size" value="1500" />
			<property name="hibernate.c3p0.maxIdleTime" value="60" />
			<property name="hibernate.c3p0.max_statements" value="1500" />
			<property name="hibernate.c3p0.timeout" value="120" />
			<property name="hibernate.c3p0.idle_test_period" value="120" />
			<property name="hibernate.c3p0.acquire_increment" value="1" />
			<property name="hibernate.c3p0.validate" value="true" />


1. mysql配置

	* 最大连接数与线程缓存
	
			set global max_connections=2000;
			set global thread_cache_size=500;

### 压力测试与调整

* 针对一个常用api，如电商系统的购物车查看接口，实施压力测试
	
		ab -k -n 10000 -c 500 -s 60 -p 'post.txt' -T 'application/x-www-form-urlencoded' 'http://xxx.yyy:8080/zzz/api/shoppingCar/getMyShoppingCarList.do'
		
* 对于明显的、制约性能的代码和配置进行修正。 

	比如与业务无关的访问日志功能，可分离出来采用mongoDB等nosql数据库。
	
### 结果


1. 方式 
	
	2台（macbook＋aliyunEcs），各开500线程：
		
		ab -k -n 1000 -c 500 -s 60  
		

2. 结果：

	* mackbook
	        
	        Concurrency Level:      500
            Time taken for tests:   115.784 seconds
            Complete requests:      1000
            Failed requests:        1
               (Connect: 0, Receive: 0, Length: 1, Exceptions: 0)
            Keep-Alive requests:    0
            Total transferred:      2307690 bytes
            Total body sent:        304000
            HTML transferred:       2101896 bytes
            Requests per second:    8.64 [#/sec] (mean)
            Time per request:       57892.244 [ms] (mean)
            Time per request:       115.784 [ms] (mean, across all concurrent requests)
            Transfer rate:          19.46 [Kbytes/sec] received
                                    2.56 kb/s sent
                                    22.03 kb/s total

            Connection Times (ms)
                          min  mean[+/-sd] median   max
            Connect:        0  552 715.9    536    4331
            Processing:   173 50240 43685.5  32538  114689
            Waiting:      173 50142 43725.0  32548  114461
            Total:        208 50792 43757.4  32586  115750

            Percentage of the requests served within a certain time (ms)
              50%  32586
              66%  88705
              75%  97941
              80%  102609
              90%  109264
              95%  112487
              98%  114717
              99%  114729
             100%  115750 (longest request)


		
	* aliyunEcs
	
		    Concurrency Level:      500
            Time taken for tests:   117.498 seconds
            Complete requests:      1000
            Failed requests:        2
               (Connect: 0, Receive: 0, Length: 2, Exceptions: 0)
            Keep-Alive requests:    0
            Total transferred:      2305380 bytes
            Total body sent:        304000
            HTML transferred:       2099792 bytes
            Requests per second:    8.51 [#/sec] (mean)
            Time per request:       58749.172 [ms] (mean)
            Time per request:       117.498 [ms] (mean, across all concurrent requests)
            Transfer rate:          19.16 [Kbytes/sec] received
                                    2.53 kb/s sent
                                    21.69 kb/s total

            Connection Times (ms)
                          min  mean[+/-sd] median   max
            Connect:        0   44  45.0     41    1045
            Processing:   154 36864 41416.5  14502  112533
            Waiting:      146 36637 41527.4  14437  112533
            Total:        201 36907 41417.3  14540  112570

            Percentage of the requests served within a certain time (ms)
              50%  14540
              66%  30336
              75%  79495
              80%  101463
              90%  106604
              95%  108138
              98%  110040
              99%  111609
             100%  112570 (longest request)
             
             
* 结论
	
	响应时间超出5秒内的目标值，如果单台tomcat承受500线程，实测结果如下：
	
			Percentage of the requests served within a certain time (ms)
  			50%   4041
  			66%   7968
  			75%  10112
  			80%  14993
  			90%  16513
  			95%  16884
  			98%  17196
  			99%  17412
  			100%  17915 (longest request)
  			
  	超过一半达到效率目标。
  	
  	因此，可考虑假设2～3台tomcat服务器分担负载，以达成效率目标。