package fw.jbiz.ext.verifycode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import fw.jbiz.ZObject;
import fw.jbiz.db.ZDao;
import fw.jbiz.ext.verifycode.bean.ZVerifyCodeBean;
import fw.jbiz.ext.verifycode.interfaces.IVerifyCodeProvider;

public class ZVerifyCodeManager extends ZObject {

	public static final int CONFIRM_OK = 1;
	public static final int CONFIRM_ERR_CD = -11; // 错误的验证码
	public static final int CONFIRM_SESSION_NOT_EXIST = -12; // 验证码会话不存在
	public static final int CONFIRM_EXPIRED = -13; // 验证码已过期
	public static final int CONFIRM_ADDRESS_NOT_FIT = -14; // 手机号码／邮箱与验证码会话不匹配
	public static final int CONFIRM_NG_UNKNOWN = -19; // 未知错误
	public static final int MAKE_OK = 1;
	public static final int MAKE_TOO_FREQ = -11; // 间隔时间太短
	public static final int MAKE_TOO_MANY = -12; // 次数过多
	public static final int MAKE_ERR_ADDRESS = -13; // 无效的手机号码和邮箱
	public static final int MAKE_SESSION_NOT_EXIST = -14; // 验证码会话不存在
	public static final int MAKE_ADDRESS_NOT_FIT = -15; // 手机号码／邮箱与上次提交的不匹配
	public static final int MAKE_ADDRESS_NOT_VALID = -16; // 手机号码／邮箱不是有效的用户账号
	public static final int MAKE_SEND_FAIL = -17; // 短信／邮件发送失败
	
	public static final String DEST_TYPE_MOBILE = "m";
	public static final String DEST_TYPE_EMAIL = "e";
	
	private static final String PTN_EMAIL = "^.+@.+$"; 
	private static final String PTN_MOBILE = "^[0-9]{11,}$"; 
	
	public static int TIME_INTERVAL_MS = 60000; // 间隔毫秒数，1分钟
	public static int MAX_SEND_TIMES_MOBILE = 5; // 每天最大发送次数
	public static int MAX_SEND_TIMES_EMAIL = 20; // 每天最大发送次数
	public static int CODE_VALID_TIME_MOBILE = 5; // 验证码有效时间，分钟
	public static int CODE_VALID_TIME_EMAIL = 30; // 验证码有效时间，分钟
	
	private static IVerifyCodeProvider thisProvider;
	
	public static void setProvider(IVerifyCodeProvider provider) {
		thisProvider = provider;
	}
	
	public static ZVerifyCodeBean makeVerifyCode(String destAddress, String sessionKey, boolean checkAccount, String signature, 
			String smsTemplateId, EntityManager em) {
		ZVerifyCodeBean verifyCodeBean = new ZVerifyCodeBean();
		String bSessionKey;
		String bCode;
		String bDestType;
		String bDestAddress;
		Date bExpiredTime;
		Integer bResult;

		Date nowTime = new Date();
		
		// 账号有效性
		
		if (checkAccount && !thisProvider.isValidAccount(destAddress, em)) {
			 bResult = MAKE_ADDRESS_NOT_VALID;
			 verifyCodeBean.setResult(bResult);
			 return verifyCodeBean;
		}
		
		// 验证，判断 destAddress
		   
		 Pattern regex = Pattern.compile(PTN_EMAIL);    
		 Matcher matcher = regex.matcher(destAddress);  
		 if (matcher.matches()) {
			 bDestType = DEST_TYPE_EMAIL;
		 } else {
			 regex = Pattern.compile(PTN_MOBILE);
			 matcher = regex.matcher(destAddress);
			 if (matcher.matches()) {
				 bDestType = DEST_TYPE_MOBILE;
			 } else {
				 bResult = MAKE_ERR_ADDRESS;
				 verifyCodeBean.setResult(bResult);
				 return verifyCodeBean;
			 }
		 }
		 
		 bDestAddress = destAddress;
		 verifyCodeBean.setDestAddress(bDestAddress);
		 verifyCodeBean.setDestType(bDestType);
		
		// sessionKey 判断
		 if (StringUtils.isEmpty(sessionKey)) {
			 bSessionKey = ZDao.genPkId();
		 } else {
			 bSessionKey = sessionKey;
			 
			List<ZVerifyCodeBean> codeList = thisProvider.getAllCodes(bSessionKey, em);
			
			// 会话是否存在
			if (codeList.isEmpty()) {
				bResult = MAKE_SESSION_NOT_EXIST;
				verifyCodeBean.setResult(bResult);
				return verifyCodeBean;
			}
			
			// 判断 destAddress 是否一致
			boolean validAddress = true;

			// 间隔时间判断 >60s
			boolean hasOver = false;
			for (ZVerifyCodeBean bean: codeList) {
				
				if (!bean.getDestAddress().equals(bDestAddress)) {
					validAddress = false;
				}
				
				Date createTime = bean.getCreateTime();
				long diff = nowTime.getTime() - createTime.getTime();
				if (diff > TIME_INTERVAL_MS) {
					hasOver = true;
				}
			}

			if (!validAddress) {
				bResult = MAKE_ADDRESS_NOT_FIT;
				verifyCodeBean.setResult(bResult);
				return verifyCodeBean;
			}
			
			if (!hasOver) {
				bResult = MAKE_TOO_FREQ;
				verifyCodeBean.setResult(bResult);
				return verifyCodeBean;
			}
			
		 }

		verifyCodeBean.setSessionKey(bSessionKey);
		
		// 发送次数判断
		Integer times = thisProvider.getSendTimes(bDestAddress, em);
		int maxTimes = MAX_SEND_TIMES_EMAIL;
		if (bDestType.equals(DEST_TYPE_MOBILE)) {
			maxTimes = MAX_SEND_TIMES_MOBILE;
		}
		
		if (times >= maxTimes) {
			bResult = MAKE_TOO_MANY;
			verifyCodeBean.setResult(bResult);
			return verifyCodeBean;
		}
		
		// 发送code
		int randomInt = 0;
		while (randomInt < 100000) {
			randomInt = (int) (Math.random() * 1000000);
		}
		bCode = String.valueOf(randomInt);
		
		boolean sendResult;
		if (bDestType.equals(DEST_TYPE_MOBILE)) {
			sendResult = thisProvider.sendCodeBySms(bDestAddress, bCode, CODE_VALID_TIME_MOBILE, signature, smsTemplateId);
		} else {
			sendResult = thisProvider.sendCodeByEmail(bDestAddress, bCode, CODE_VALID_TIME_EMAIL, signature);
		}
		
		if (!sendResult) {
			bResult = MAKE_SEND_FAIL;
			verifyCodeBean.setResult(bResult);
			return verifyCodeBean;
		}
		
		verifyCodeBean.setCode(bCode);
		// 保存code
		if (bDestType.equals(DEST_TYPE_MOBILE)) {
			bExpiredTime = new Date(nowTime.getTime() + CODE_VALID_TIME_MOBILE*60*1000);
		} else {
			bExpiredTime = new Date(nowTime.getTime() + CODE_VALID_TIME_EMAIL*60*1000);
		}
		verifyCodeBean.setExpiredTime(bExpiredTime);
		thisProvider.saveCode(verifyCodeBean, em);
		// 返回
		bResult = MAKE_OK;
		verifyCodeBean.setResult(bResult);
		return verifyCodeBean;
	}
	
	public static Integer confirmVerifyCode(String sessionKey, String code, String destAddress, EntityManager em) {
		
		Integer result = CONFIRM_OK;
		
		// 获取sessionKey所有关联的有效code
		List<ZVerifyCodeBean> codeList = thisProvider.getAllCodes(sessionKey, em);
		// 去除已过期的code
		List<String> validCodeList = new ArrayList<String>();
		List<String> allCodeList = new ArrayList<String>();
		
		Date nowTime = new Date();
		for (ZVerifyCodeBean codeBean: codeList) {
			Date expiredTime = codeBean.getExpiredTime();
			long diff = nowTime.getTime() - expiredTime.getTime();
			if (diff <= 0) {
				validCodeList.add(codeBean.getCode());
			}
			allCodeList.add(codeBean.getCode());
			
			//  关联手机号码／邮箱是否一致
			if (code.equals(codeBean.getCode())) {
				if (!destAddress.equals(codeBean.getDestAddress())) {
					result = CONFIRM_ADDRESS_NOT_FIT; // 手机号码／邮箱与验证码会话不匹配
					return result;
				}
			}
		}
		
		// 判断是否存在验证码会话
		if (allCodeList.isEmpty()) {
			result = CONFIRM_SESSION_NOT_EXIST; // 会话不存在
		} else if (!allCodeList.contains(code)) {
			result = CONFIRM_ERR_CD;  // 验证码错误
		} else {
			// 判断参数code是否在内
			if (!validCodeList.contains(code)) {
				result = CONFIRM_EXPIRED; // 验证码过期
			}
		}
		
		// 一旦验证成功, sessionKey失效
		if (result == CONFIRM_OK) {
			thisProvider.invalidSessionKey(sessionKey, em);
		}
		
		// 返回
		return result;
		
	}
	
}
