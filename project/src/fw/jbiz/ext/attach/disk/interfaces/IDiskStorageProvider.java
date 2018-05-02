package fw.jbiz.ext.attach.disk.interfaces;

import javax.persistence.EntityManager;

import fw.jbiz.ext.attach.IAttachProvider;
import fw.jbiz.ext.attach.disk.bean.ZDiskReadBean;
import fw.jbiz.ext.attach.disk.bean.ZDiskWriteBean;

public interface IDiskStorageProvider extends IAttachProvider{

	public boolean authWrite(ZDiskWriteBean diskBean, EntityManager em);
	public boolean authRead(ZDiskReadBean diskBean, EntityManager em);

	public void saveFileInfo(ZDiskWriteBean diskBean, EntityManager em);
	
	public String getMiddlePathOfCategory(String clientFileCategory, EntityManager em);
	public void setFileInfoByFileObjectId(String clientFileObjectId, ZDiskReadBean diskBean, EntityManager em);
}
