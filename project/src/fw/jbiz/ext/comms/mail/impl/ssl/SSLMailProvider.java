package fw.jbiz.ext.comms.mail.impl.ssl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Base64.Encoder;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import fw.jbiz.ZObject;
import fw.jbiz.common.conf.IConfig;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.comms.mail.bean.ZMailBean;
import fw.jbiz.ext.comms.mail.bean.ZMailResult;
import fw.jbiz.ext.comms.mail.interfaces.IMailProvider;
import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public class SSLMailProvider extends ZObject implements IMailProvider {

	static Logger logger = Logger.getLogger(SSLMailProvider.class);
	
	private static String smtpServer;
	private static String smtpPort;
	private static String mailAccount;
	private static String mailPassword;
	private static Session mailSession = null;
	
	private static IConfig mConfig = ZSystemConfig.getInstance();
	
	public SSLMailProvider() {
		logger.info(getProviderInfo());
		initSetting();
	}
	
	public void setConfig(IConfig config) {
		mConfig = config;
		initSetting();
	}
	
	private static void initSetting() {
		if (!mConfig.isValid()) {
			return;
		}
		
		smtpServer = mConfig.getProp("mail_smtp_server");
		smtpPort = mConfig.getProp("mail_smtp_port");
		mailAccount = mConfig.getProp("mail_send_account");
		mailPassword = mConfig.getProp("mail_send_password");
		

	}
	
	private Session getMailSession() {
		
		if (mailSession == null) {
			// Get a Properties object
			Properties props = System.getProperties();
			props.put("mail.smtp.host", smtpServer);
			props.put("mail.smtp.port", smtpPort);
			props.put("mail.smtp.auth", "true");
			
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
	        props.setProperty("mail.smtp.socketFactory.fallback", "false");  
	        props.setProperty("mail.smtp.socketFactory.port", smtpPort);  

			final String username = mailAccount;
			final String password = mailPassword;

			mailSession = Session.getDefaultInstance(props,
					new Authenticator() {

						protected PasswordAuthentication getPasswordAuthentication() {

							return new PasswordAuthentication(username,
									password);

						}
					});
			
			mailSession.setDebug(true);// debug模式
		}
		
		return mailSession;
	}

	@Override
	public List<ZMailResult> send(List<ZMailBean> mailBeanList) {
		List<ZMailResult> mailResultList = new ArrayList<ZMailResult>();
		for (ZMailBean bean: mailBeanList) {
			ZMailResult result = mailRun(bean);
			mailResultList.add(result);
		}
		
		return mailResultList;
	}

	@Override
	public ZMailResult send(ZMailBean mailBean)  {
		ZMailResult result = mailRun(mailBean);
		return result;
	}
	
	private ZMailResult mailRun(ZMailBean mailBean)  {

		ZMailResult result = new ZMailResult();
		result.setSuccess(true);
		
		String fromNickname = mailBean.getFromNickname();
		String mailDispAddr = mailBean.getFromAddressDisp();
		String email = mailBean.getAddress();
		String mailSubject = mailBean.getSubject();
		String content = mailBean.getContent();
		
		try {

//			sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			Encoder enc = Base64.getEncoder();
			//mailDispAddr = enc.encode(mailDispAddr.getBytes("UTF-8"));
			
			// -- Create a new message --
			Message msg = new MimeMessage(getMailSession());

			// -- Set the FROM and TO fields --
			fromNickname = MimeUtility.encodeText(fromNickname);
			msg.setFrom(new InternetAddress(String.format("%s<%s>", fromNickname, mailDispAddr)));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email, false));
			// 设置邮件标题
			String subject = "=?UTF-8?B?"
					+ enc.encode(mailSubject.getBytes("UTF-8")) + "?=";
			msg.setSubject(subject);
			// 设置邮件正文
			//String ts = new Date().toString();
			//content = content + "\n " + ts;
			msg.setContent(content, "text/html;charset=UTF-8");
			msg.setSentDate(new Date());

			Transport.send(msg);

		} catch (UnsupportedEncodingException | MessagingException e) {
			result.setSuccess(false);
			logger.error(e);

		} finally {
			logger.info(
					String.format("mailRun end:[email:%s, content:%s]", email,
							content));
		}
		
		return result;
		
	}

	@Override
	public String getProviderInfo() {

		ZSimpleJsonObject res = new ZGsonObject();
		res.add("provider_class", this.getClass().getName());
		res.add("version", "v1.0.20150928");
		res.add("author", "JbizFw");
		res.add("website", "http://jbiz.net.cn/");
		return res.toString();
	}
}
