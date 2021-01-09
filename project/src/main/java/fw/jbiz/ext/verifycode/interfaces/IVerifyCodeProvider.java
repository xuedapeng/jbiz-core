package fw.jbiz.ext.verifycode.interfaces;

import java.util.List;

import javax.persistence.EntityManager;

import fw.jbiz.ext.verifycode.bean.ZVerifyCodeBean;

public interface IVerifyCodeProvider {
	
	// 账号有效性
	public boolean isValidAccount(String destAddress, EntityManager em);
	
	// 当天发送次数
	public int getSendTimes(String destAddress, EntityManager em);
	// 保存code到db
	void saveCode(ZVerifyCodeBean verifyCodeBean, EntityManager em);
	// 获取所有有效的code
	List<ZVerifyCodeBean> getAllCodes(String sessionKey, EntityManager em);
	// 无效化sessionKey
	void invalidSessionKey(String sessionKey, EntityManager em);
	
	// 发送sms
	boolean sendCodeBySms(String mobileNumber, String code, int validMinutes, String signature, String smsTemplateId);
	// 发送email
	boolean sendCodeByEmail(String emailAddress, String code, int validMinutes, String signature);
}
