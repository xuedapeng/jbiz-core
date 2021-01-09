package fw.jbiz.common.util;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import fw.jbiz.common.ZException;

public class EncryptTool {

	protected static final Blowfish _blow = new Blowfish("4f^7H3Geqe9f$");
	
	/*
	 * BCrypt 加密
	 */
	public static String bcryptEncryt(String plain){
		
		throw new ZException("not implement");
	}

	/*
	 * BCrypt 校验
	 */
	public static boolean bcryptCheck(String plain, String encrypted){
		
		throw new ZException("not implement");
	}
	
	/*
	 * todo:md5可以使用彩虹表破解，建议使用 BCrypt
	 */
	 public static String md5(String string) throws Exception {
		    byte[] hash;
		    try {
		        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		    } catch (NoSuchAlgorithmException e) {
		        throw new Exception("Huh, MD5 should be supported?", e);
		    } catch (UnsupportedEncodingException e) {
		        throw new Exception("Huh, UTF-8 should be supported?", e);
		    }

		    StringBuilder hex = new StringBuilder(hash.length * 2);
		    for (byte b : hash) {
		        if ((b & 0xFF) < 0x10) hex.append("0");
		        hex.append(Integer.toHexString(b & 0xFF));
		    }
		    return hex.toString();
	 }	
	 
	 public static String bfEn(String string) {
		 return _blow.encryptString(string);
	 }
	 

	/**
	 * 生成主键ID
	 */
	public final static String genUUID() {

		UUID uuid = UUID.randomUUID();

		return uuid.toString();
	}

}
