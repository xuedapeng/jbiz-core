package fw.jbiz.common.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.ZException;
import fw.jbiz.common.util.Blowfish;

public class ZSystemConfig extends ZObject implements IConfig{

	static Logger logger = Logger.getLogger(ZSystemConfig.class);
	
	private static final String SYSTEM_CONFIG_FILE = "jbiz_config.properties";
	
	private static ZSystemConfig singleInst = null;
	
	private Properties prop = new Properties();
	private boolean validProp = false;
	
	private Blowfish _bf = null;
	private List<String> _encryptItems = new ArrayList<String>();
	
	private ZSystemConfig() {

        String configFile = this.getDefaultSystemConfigFile();
        File file =new File(configFile);
        if (!file.exists()) {
        	return;
        }
        
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
            prop.load(in); 
            validProp = true;

        } catch (IOException e) {
        	logger.error(trace(e));
        	validProp = false;
        }
        
        // 解密处理
        logger.info("encrypt_key="+prop.getProperty("encrypt_key"));
        
        if (prop.containsKey("encrypt_key")) {
        	_bf = new Blowfish(getEndecKey(prop.getProperty("encrypt_key")));
        	
        	String items = prop.getProperty("encrypt_items");
        	if(StringUtils.isEmpty(items)) {
        		throw new ZException("no encrypt_items in jbiz_config");
        	}
        	

            logger.info("items="+items);
        	_encryptItems = Arrays.asList(items.replaceAll(" ", "").split(","));
            logger.info("_encryptItems="+_encryptItems.size());
        	
        }
	}	
	
	public static ZSystemConfig getInstance() {

    	if (singleInst == null) {    		
    		singleInst = new ZSystemConfig();
    	}
    	
    	return singleInst;
	}
    
	// 读取配置项
    public static String getProperty(String name) {
    	
    	String v =  getInstance().prop.getProperty(name);
    	if (getInstance()._bf!=null && getInstance()._encryptItems.contains(name)) {
    		v = getInstance()._bf.decryptString(v);
    	}
    	return v;
    }    
    
    // 设置并覆盖文件配置项
    public static void setProperty(String name, String value) {
    	
    	getInstance().prop.setProperty(name, value);
    }
    
    private String getDefaultSystemConfigFile() {

		
        String configFile = Paths.get(this.getDefaultSystemConfigPath(), SYSTEM_CONFIG_FILE).toString();
    	
        return configFile;
    }
    
    private String getDefaultSystemConfigPath() {

		String webRoot = this.getClass().getResource("/").toString();
		webRoot = webRoot.replace("file:/", "").replaceAll("%20", " ").replaceAll("\\\\", "/");  
		String webinf = "/WEB-INF";
		webRoot = webRoot.substring(0,webRoot.indexOf(webinf)+webinf.length());  
		
		// Mac OS X 处理
		String os = System.getProperty("os.name");
		if (os.startsWith("Mac") || os.startsWith("Linux")) {
			webRoot = "/" + webRoot;
		}
		
		logger.info("webRoot="+webRoot);
		logger.info("os.name="+os);
		
        String configPath = webRoot;
        
        return configPath;
    }

	@Override
	public String getProp(String name) {
		return getProperty(name);
	}

	@Override
	public void setProp(String name, String value) {
		setProperty(name, value);
	}

	@Override
	public boolean isValid() {
		return validProp;
	}
    

    public static String getSystemConfigPath() {
    	return getInstance().getDefaultSystemConfigPath();
    	
    }
    
    public static String encrypt(String plain, String key) {

    	return new Blowfish(key).encryptString(plain);
    }
    
    public static String decrypt(String encrypted, String key) {

    	return new Blowfish(key).decryptString(encrypted);
    }
    
    public static String getEndecKey(String fullpath) {
    	 try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(fullpath), "utf-8");
			BufferedReader br = new BufferedReader(in);
			String s = br.readLine();
			return s;
			
		} catch (IOException e) {
			logger.error(trace(e));
			return null;
		}
    }
    
}
