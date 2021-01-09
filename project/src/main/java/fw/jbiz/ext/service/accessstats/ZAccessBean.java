package fw.jbiz.ext.service.accessstats;

import java.sql.Timestamp;

import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

public class ZAccessBean {

	private String reqClientIP;
	private String reqUserAgent;
	private String reqServerIP;
	private String reqApiPath;
	
	private ZLogicParam logicParam;
	private ZSimpleJsonObject resultResource;
	private Timestamp starttime;
	private Timestamp endtime;
	
	public String getReqClientIP() {
		return reqClientIP;
	}
	public void setReqClientIP(String reqClientIP) {
		this.reqClientIP = reqClientIP;
	}
	public String getReqUserAgent() {
		return reqUserAgent;
	}
	public void setReqUserAgent(String reqUserAgent) {
		this.reqUserAgent = reqUserAgent;
	}
	public String getReqServerIP() {
		return reqServerIP;
	}
	public void setReqServerIP(String reqServerIP) {
		this.reqServerIP = reqServerIP;
	}
	public String getReqApiPath() {
		return reqApiPath;
	}
	public void setReqApiPath(String reqApiPath) {
		this.reqApiPath = reqApiPath;
	}
	public ZLogicParam getLogicParam() {
		return logicParam;
	}
	public void setLogicParam(ZLogicParam logicParam) {
		this.logicParam = logicParam;
	}
	public ZSimpleJsonObject getResultResource() {
		return resultResource;
	}
	public void setResultResource(ZSimpleJsonObject resultResource) {
		this.resultResource = resultResource;
	}
	public Timestamp getStarttime() {
		return starttime;
	}
	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}
	public Timestamp getEndtime() {
		return endtime;
	}
	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}
}
