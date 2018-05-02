package fw.jbiz.common.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;

public class ZCustomConfig extends ZObject implements IConfig{
	
	static Logger logger = Logger.getLogger(ZCustomConfig.class);
	
	private Properties prop = new Properties();
	private boolean validProp = false;
	
	public ZCustomConfig(String configFile) {

        try {        	
            InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
            prop.load(in);           
            validProp = true; 

        } catch (IOException e) {
        	logger.error(trace(e));
        	validProp = false;
        }
	}
	

	@Override
    public String getProp(String name) { 	
    	return prop.getProperty(name);
    }
    
    // 设置并覆盖文件配置项
	@Override
    public void setProp(String name, String value) {
    	prop.setProperty(name, value);
    }
    
	@Override
	public boolean isValid() {
		return validProp;
	}

}
