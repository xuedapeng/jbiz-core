package fw.jbiz.common.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.ZObject;

public class FileHelper extends ZObject {

	static Logger logger = Logger.getLogger(FileHelper.class);
	
	public static void write(String absolutePath, String fullFileName, InputStream fileObject) throws IOException {
		
		
			File file = new File(absolutePath, fullFileName);
			
			if(!file.getParentFile().exists()) {  
	            //如果目标文件所在的目录不存在，则创建父目录  
				logger.info("目标文件所在目录不存在，准备创建它！");  
	            if(!file.getParentFile().mkdirs()) {  
	            	logger.error("创建目标文件所在目录失败！");  
	                throw new IOException();
	            }  
	        }  
		
            OutputStream outputStream = new FileOutputStream(file);
            
            int length = 0;
 
            byte[] buff = new byte[256];
 
            while (-1 != (length = fileObject.read(buff))) {
                outputStream.write(buff, 0, length);
            }
            fileObject.close();
            outputStream.close();
		
	}
	
	

	public static File read(String absoluteFullPath) {
		
		 File file = new File(absoluteFullPath);
        if (!file.exists()) {
            return null;
        }
        
        return file;
	}
	
	public static String getFileExtName(String fileName) {
		
		if (StringUtils.isEmpty(fileName)) {
			return "";
		}
		
		String suffix= "";
		if (fileName.indexOf(".") > 0) {
			suffix = fileName.substring(fileName.lastIndexOf(".")+1);
		}
		
		return suffix;
		
	}
}
