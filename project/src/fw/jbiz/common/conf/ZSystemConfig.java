package fw.jbiz.common.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;

public class ZSystemConfig extends ZObject implements IConfig{

	static Logger logger = Logger.getLogger(ZSystemConfig.class);
	
	private static final String SYSTEM_CONFIG_FILE = "jbiz_config.properties";
	
	private static ZSystemConfig singleInst = null;
	
	private Properties prop = new Properties();
	private boolean validProp = false;
	
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
	}	
	
	public static ZSystemConfig getInstance() {

    	if (singleInst == null) {    		
    		singleInst = new ZSystemConfig();
    	}
    	
    	return singleInst;
	}
    
	// 读取配置项
    public static String getProperty(String name) {
    	
    	return getInstance().prop.getProperty(name);
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
    
}
