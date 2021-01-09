package fw.jbiz.ext.service.accessstats.impl;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import fw.jbiz.db.ZEntity;

@Entity
@Table(name="t_access_stats")  // 根据config设置可以动态修改
public class SimpleAccessStatsEntity extends ZEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String client_ip;
	private String user_agent;
	private String server_ip;
	private String api_path;
	private String userId;
	private String account;
	private String params;
	private String result_status;
	private Timestamp starttime;
	private Timestamp endtime;
	private Long elapsedTime;
	private String domainId;
	
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public String getUser_agent() {
		return user_agent;
	}
	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}
	public String getServer_ip() {
		return server_ip;
	}
	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}
	public String getApi_path() {
		return api_path;
	}
	public void setApi_path(String api_path) {
		this.api_path = api_path;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getResult_status() {
		return result_status;
	}
	public void setResult_status(String result_status) {
		this.result_status = result_status;
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
	
	public Long getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
