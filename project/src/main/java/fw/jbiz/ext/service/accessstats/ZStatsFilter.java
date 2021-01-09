package fw.jbiz.ext.service.accessstats;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.ZSystemFilter;

public  class ZStatsFilter extends ZSystemFilter {

	private ZAccessBean mAccessBean = null;

	@Override
	public void doFilterBefore(ZLogicParam logicParam,
			ZSimpleJsonObject res) {

		ZLogicParam myParam = (ZLogicParam) logicParam;
		HttpServletRequest request = myParam.getRequest();
		mAccessBean = createAccessBean(logicParam);

		// 真实client ip
		String realIP = request.getHeader("X-Real-IP");
		if (StringUtils.isEmpty(realIP)) {
			mAccessBean.setReqClientIP(request.getRemoteAddr());
		} else {
			mAccessBean.setReqClientIP(realIP);
		}
		mAccessBean.setReqUserAgent(request.getHeader("User-Agent"));
		mAccessBean.setReqServerIP(request.getLocalAddr());
		mAccessBean.setReqApiPath(request.getRequestURI());
		
		mAccessBean.setLogicParam(myParam);
		mAccessBean.setStarttime(new Timestamp(new Date().getTime()));

	}

	@Override
	public void doFilterAfter(ZLogicParam logicParam, ZSimpleJsonObject res) {

		if (mAccessBean != null) {
			mAccessBean.setEndtime(new Timestamp(new Date().getTime()));
			mAccessBean.setResultResource(res);
			
			adjustAccessBean(mAccessBean);
			ZAccessStatsService.addItemToQueue(mAccessBean);
		}
	}
	
	// 创建，并设置扩展值
	public ZAccessBean createAccessBean(ZLogicParam logicParam) {
		
		return new ZAccessBean();
	}
	
	// 允许扩展方法调整记录值
	public void adjustAccessBean(ZAccessBean mAccessBean) {

		// 代理服务器的情况，设置clientIP
		HttpServletRequest request = mAccessBean.getLogicParam().getRequest();
		String ip = request.getHeader("x-forwarded-for");
		
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || ip.equals("127.0.0.1"))
			ip = request.getHeader("Proxy-Client-IP");
		
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || ip.equals("127.0.0.1"))   
			ip = request.getHeader("WL-Proxy-Client-IP");
		
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || ip.equals("127.0.0.1"))   
			ip = request.getRemoteAddr();
		
		mAccessBean.setReqClientIP(ip);
	}
}
