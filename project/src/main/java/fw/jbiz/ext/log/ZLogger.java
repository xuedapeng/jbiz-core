package fw.jbiz.ext.log;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

/*
 * @Deprecated from 201804
 */
@Deprecated
public class ZLogger  {

	Logger mLogger = Logger.getLogger("[jbiz log v1506291855]");
	
	@Deprecated
	public ZLogger() {
		
	}

	@Deprecated
	public ZLogger(Logger logger) {
		mLogger = logger;
	}
	
	public void error(Throwable e) {
		String msg = getTrace(e);
		mLogger.error(msg);
	}
	
    public static String getTrace(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();	
    }

	public void info(String msg) {
		mLogger.info(msg);
	}
	public void info(String msg, Object me) {
		msg = msg + " # " + getClassInfo(me);
		mLogger.info(msg);
	}

	public void error(String msg) {
		mLogger.error(msg);		
	}
	public void error(String msg, Object me) {
		msg = msg + " # " + getClassInfo(me);
		mLogger.error(msg);		
	}

	public void debug(Object obj) {
		mLogger.debug(obj);		
	}
	public void info(Object obj) {
		mLogger.info(obj);		
	}
	public void error(Object obj) {
		mLogger.error(obj);		
	}
	
	private String getClassInfo(Object obj) {
		String info = obj.getClass().getName();
		return info;
	}
}