package fw.jbiz.ext.comms.sms.impl.yuntx;

import java.util.Arrays;

import fw.jbiz.ext.comms.sms.bean.ZSmsBean;

public class YtxSmsBean extends ZSmsBean {

	// 模版编号
	private String templateId;
	
	// 模版参数
	private String[] params;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String toString() {
		String format = "YtxSmsBean: [mobileNumber=%s, templateId=%s, params=%s]";
		return String.format(format, getMobileNumber(), templateId, Arrays.toString(params));
	}
	
	
	
}
