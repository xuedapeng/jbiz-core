package fw.jbiz.ext.attach.disk.bean;

import java.io.InputStream;

import fw.jbiz.ZObject;

public class ZDiskWriteBean extends ZObject {

	private String clientTicket;
	private String clientFileCategory;
	private String clientFileAliasName;
	private String clientFileName;
	private long clientFileSize;
	private InputStream clientFileObject;
	
	private String realShortPath;
	
	private String logicFileObjectId;
	

	public String getClientTicket() {
		return clientTicket;
	}

	public void setClientTicket(String clientTicket) {
		this.clientTicket = clientTicket;
	}

	public String getClientFileCategory() {
		return clientFileCategory;
	}

	public void setClientFileCategory(String clientFileCategory) {
		this.clientFileCategory = clientFileCategory;
	}

	public String getClientFileAliasName() {
		return clientFileAliasName;
	}

	public void setClientFileAliasName(String clientFileAliasName) {
		this.clientFileAliasName = clientFileAliasName;
	}

	public String getClientFileName() {
		return clientFileName;
	}

	public void setClientFileName(String clientFileName) {
		this.clientFileName = clientFileName;
	}

	public InputStream getClientFileObject() {
		return clientFileObject;
	}

	public void setClientFileObject(InputStream clientFileObject) {
		this.clientFileObject = clientFileObject;
	}


	public String getRealShortPath() {
		return realShortPath;
	}

	public void setRealShortPath(String realShortPath) {
		this.realShortPath = realShortPath;
	}

	public String getLogicFileObjectId() {
		return logicFileObjectId;
	}

	public void setLogicFileObjectId(String logicFileObjectId) {
		this.logicFileObjectId = logicFileObjectId;
	}

	public long getClientFileSize() {
		return clientFileSize;
	}

	public void setClientFileSize(long clientFileSize) {
		this.clientFileSize = clientFileSize;
	}

	
	
}
