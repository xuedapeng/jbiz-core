package fw.jbiz.ext.attach.disk.bean;


import java.io.File;

import fw.jbiz.ZObject;

public class ZDiskReadBean extends ZObject {

	private String clientTicket;
	
	private String clientFileObjectId;
	
	
	private String readShortPath;
	private File readFileObject;
	private String readFileName;
	private String readFileAliasName;
	private Long readFileLength;
	private String readFileType;

	public String getClientTicket() {
		return clientTicket;
	}

	public void setClientTicket(String clientTicket) {
		this.clientTicket = clientTicket;
	}

	public String getClientFileObjectId() {
		return clientFileObjectId;
	}

	public void setClientFileObjectId(String clientFileObjectId) {
		this.clientFileObjectId = clientFileObjectId;
	}

	public File getReadFileObject() {
		return readFileObject;
	}

	public void setReadFileObject(File readFileObject) {
		this.readFileObject = readFileObject;
	}

	public String getReadFileName() {
		return readFileName;
	}

	public void setReadFileName(String readFileName) {
		this.readFileName = readFileName;
	}

	public Long getReadFileLength() {
		return readFileLength;
	}

	public void setReadFileLength(Long readFileLength) {
		this.readFileLength = readFileLength;
	}

	public String getReadFileType() {
		return readFileType;
	}

	public void setReadFileType(String readFileType) {
		this.readFileType = readFileType;
	}

	public String getReadShortPath() {
		return readShortPath;
	}

	public void setReadShortPath(String readShortPath) {
		this.readShortPath = readShortPath;
	}

	public String getReadFileAliasName() {
		return readFileAliasName;
	}

	public void setReadFileAliasName(String readFileAliasName) {
		this.readFileAliasName = readFileAliasName;
	}
	
	
}
