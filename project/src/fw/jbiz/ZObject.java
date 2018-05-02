package fw.jbiz;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import fw.jbiz.ext.log.ZLogger;

import fw.jbiz.common.conf.ZSystemConfig;

@SuppressWarnings("deprecation")
public class ZObject {
	
	static ZLogger mylogger = new ZLogger();
	
	@Deprecated
	public static ZLogger log() {
		return mylogger;
	}

	@Deprecated
	public static ZLogger log(Logger logger) {
		return new ZLogger(logger);
	}

    public static boolean isDevMode() {    	
    	if ("true".equals(ZSystemConfig.getProperty("dev_mode"))) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public static String trace(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();	
    }
    
    public String getJbizVersion() {
    	String version = "v1.0.20160418";
    	return version;
    }
    
}
