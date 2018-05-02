package fw.jbiz.common.helper.httpclient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.olib.org.json.JSONException;
import fw.olib.org.json.JSONObject;


public class HttpHelper extends ZObject{

	static Logger logger = Logger.getLogger(HttpHelper.class);
	
	private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";
	private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
	

	  public static String doPost(String url, String jsonStr){
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost post = new HttpPost(url);
	    String result = null;
	    
	    try {
	      StringEntity s = new StringEntity(jsonStr, "UTF-8");
	      // s.setContentEncoding("UTF-8");
	      s.setContentType(CONTENT_TYPE_JSON);//发送json数据需要设置contentType
	      post.setEntity(s);
	      HttpResponse res = client.execute(post);
	      if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	        HttpEntity entity = res.getEntity();
	        result = EntityUtils.toString(entity);// 返回json格式：
	        
	      }
	    } catch (Exception e) {
	      logger.error(trace(e));
	    }
	    return result;
	  }

	  
	// post请求restful api
	public static String doPostByPair(String url, String jsonParams) {

		HttpClient client = createHttpClient(url);  
        PostMethod method = new PostMethod(url);  
        InputStream ins = null; 
        StringBuilder sb = new StringBuilder();  
        
        try {
	        method.setRequestHeader("Content-Type",  CONTENT_TYPE_FORM);  
	        NameValuePair[] param = json2Pairs(jsonParams);
	        		
	        method.setRequestBody(param);  
	        int statusCode = client.executeMethod(method);  
	        logger.info(statusCode);  
	        
	        
	        if (statusCode == HttpStatus.SC_OK) {  
	            ins = method.getResponseBodyAsStream();  
	            byte[] b = new byte[1024];  
	            int r_len = 0;  
	            while ((r_len = ins.read(b)) > 0) {  
	                sb.append(new String(b, 0, r_len, method  
	                        .getResponseCharSet()));  
	            }  
	        } else {  
	        	logger.info("Response Code: " + statusCode);  
	        }  
	        
	        logger.info(sb.toString()); 
        } catch (Exception e) {
        	logger.error(trace(e));
        } finally {
            method.releaseConnection();  
        }

        return sb.toString();
	}
	
	
	// get请求
	public static String doGet(String url) {

		HttpClient client = createHttpClient(url);  
		GetMethod method = new GetMethod(url);  
        InputStream ins = null; 
        StringBuilder sb = new StringBuilder();  
        
        try {
	        		
	        int statusCode = client.executeMethod(method);  
	        logger.info(statusCode);  

	        if (statusCode == HttpStatus.SC_OK) {  
	            ins = method.getResponseBodyAsStream();  
	            byte[] b = new byte[1024];  
	            int r_len = 0;  
	            while ((r_len = ins.read(b)) > 0) {  
	                sb.append(new String(b, 0, r_len, method  
	                        .getResponseCharSet()));  
	            }  
	        } else {  
	        	logger.info("Response Code: " + statusCode);  
	        }  
	        
	        System.out.println(sb.toString()); 
        } catch (Exception e) {
        	logger.error(trace(e));
        } finally {
            method.releaseConnection();  
        }

        return sb.toString();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static NameValuePair[] json2Pairs(String jsonString) throws JSONException {
		
		List<NameValuePair> resultPairs = new ArrayList<NameValuePair>();
		
		//JSONObject必须以"{"开头  
        JSONObject jsonObject = new JSONObject(jsonString);  
        Iterator<String> iter = jsonObject.keys();  
        String key=null;  
        Object value=null;  
        while (iter.hasNext()) {  
            key=iter.next();  
            value=jsonObject.get(key);  
            resultPairs.add(new NameValuePair(key, value.toString()));
        }  
        
        NameValuePair[] resultArr = new NameValuePair[resultPairs.size()];
        resultPairs.toArray(resultArr);  
        return resultArr;
	}
	
	
	private static HttpClient createHttpClient(String url) {
		
		if (url.startsWith("https://")) {
			registSelfSignatureSSL();
		}
		
		return new HttpClient();
	}
	
	private static void registSelfSignatureSSL() {
		EasySSLProtocolSocketFactory easySSL = null;  
		try {  
		    easySSL = new EasySSLProtocolSocketFactory();  
		} catch (Exception e) {  
			logger.error(trace(e));
		}  
		Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory)easySSL, 443);  
		Protocol.registerProtocol( "https", easyhttps );  
	}
}
