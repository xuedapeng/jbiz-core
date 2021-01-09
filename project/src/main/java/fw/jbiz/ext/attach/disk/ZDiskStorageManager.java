package fw.jbiz.ext.attach.disk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZCustomConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.common.helper.FileHelper;
import fw.jbiz.common.util.EncryptTool;
import fw.jbiz.ext.attach.disk.bean.ZDiskReadBean;
import fw.jbiz.ext.attach.disk.bean.ZDiskWriteBean;
import fw.jbiz.ext.attach.disk.interfaces.IDiskStorageProvider;

public class ZDiskStorageManager extends ZObject {

	static Logger logger = Logger.getLogger(ZDiskStorageManager.class);
	
	public static final Integer RESULT_OK = 1;
	public static final Integer RESULT_NO_PERMISSION = -101;
	public static final Integer RESULT_FAIL_WRITE_FILE = -102;
	public static final Integer RESULT_FAIL_READ_FILE = -103;
	
	
	private static final String CONFIG_FILE_NAME = "jbiz_config_disk_storage.properties";
	private static final String PROP_BASE_FILE_PATH = "disk_storage_base_file_path";
	private static final String PROP_PROVIDER_CLASS = "disk_storage_provider_class";
	
	private static IDiskStorageProvider mProvider = null;
	private static IConfig mConfig = new ZCustomConfig(Paths.get(ZSystemConfig.getSystemConfigPath(), CONFIG_FILE_NAME).toString());
	
	public static void setConfig(IConfig config) {
		mConfig = config;
	}
	
	// 设置提供商的实现类
	public static void setProvider(IDiskStorageProvider provider) {
		mProvider = provider;
	}

	private static IDiskStorageProvider getProvider()  {
		if (mProvider == null) {
			String className = mConfig.getProp(PROP_PROVIDER_CLASS);
			try {
				mProvider = (IDiskStorageProvider)Class.forName(className).newInstance();
				mProvider.setConfig(mConfig);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				
				logger.error(trace(e));
			}
		}
		
		return mProvider;
	}
	
	public static String getBaseFilePath() {
		return mConfig.getProp(PROP_BASE_FILE_PATH);
	}
	
	// 保存
	public static Integer write(ZDiskWriteBean diskBean, EntityManager em) {
		
		// 权限确认
		if (!getProvider().authWrite(diskBean, em)) {
			// 没有权限
			
			return RESULT_NO_PERMISSION;
		}

		// 年／月／日递进目录结构
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH)+1);
		String date = String.valueOf(cal.get(Calendar.DATE));
		
		// 写文件
		String fileName = diskBean.getClientFileName();
		String middlePath = getProvider().getMiddlePathOfCategory(diskBean.getClientFileCategory(), em);
		String absolutePath = Paths.get(getBaseFilePath(), middlePath, year, month, date).toString();
		String newFileName = EncryptTool.genUUID() + "." + FileHelper.getFileExtName(fileName);
		InputStream fileObject = diskBean.getClientFileObject();
		
		try {
			FileHelper.write(absolutePath, newFileName, fileObject);
			File file = FileHelper.read(Paths.get(absolutePath, newFileName).toString());
			diskBean.setClientFileSize(file.length());
			
		} catch (IOException e) {
			logger.error(trace(e));
			
			return RESULT_FAIL_WRITE_FILE;
		}
		
		diskBean.setRealShortPath(Paths.get(middlePath, year, month, date, newFileName).toString());
		
		
		// 保存文件信息到db, 设置 fileObjectId
		getProvider().saveFileInfo(diskBean, em);
		
		
		return RESULT_OK;
	}
	
	// 读取
	public static Integer read(ZDiskReadBean diskBean, EntityManager em) {
		
		// 权限确认
		if (!getProvider().authRead(diskBean, em)) {
			// 没有权限
			logger.error("RESULT_NO_PERMISSION");
			return RESULT_NO_PERMISSION;
		}
		
		getProvider().setFileInfoByFileObjectId(diskBean.getClientFileObjectId(), diskBean, em);
		
		String shortPath = diskBean.getReadShortPath();
		String absoluteFullPath = Paths.get(getBaseFilePath(), shortPath).toString();
		
		logger.debug("shortPath="+shortPath);
		logger.debug("absoluteFullPath="+absoluteFullPath);
		
		File file = FileHelper.read(absoluteFullPath);
		
		if (file == null) {
			logger.info("file == null");
			return RESULT_FAIL_READ_FILE;
		}
		
		String fileType = new MimetypesFileTypeMap().getContentType(file);
		Long fileLengh = file.length();
		
		diskBean.setReadFileObject(file);
		diskBean.setReadFileType(fileType);
		diskBean.setReadFileLength(fileLengh);
		
		return RESULT_OK;
	}
}
