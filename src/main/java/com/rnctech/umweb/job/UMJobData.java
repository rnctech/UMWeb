package com.rnctech.umweb.job;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"UMSession","description","jobkey"})
public class UMJobData {

	String user = null;
	String password = null;
	JobData command = null;
	Long jobid = -1l;
	@JsonIgnore String description;
	@JsonIgnore String jobkey;
	@JsonIgnore long UMSession = 0l;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public JobData getCommand() {
		return command;
	}
	public void setCommand(JobData command) {
		this.command = command;
	}
	public Long getJobid() {
		return jobid;
	}
	public void setJobid(Long jobid) {
		this.jobid = jobid;
	}
	public long getUMSession() {
		return UMSession;
	}
	public void setUMSession(long uMSession) {
		UMSession = uMSession;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getJobkey() {
		return jobkey;
	}
	public void setJobkey(String jobkey) {
		this.jobkey = jobkey;
	}
	
	public class JobData {
		String name = null;
		String jobName = null;
		String jobType = "MAIN";
		Long parentJobId = null;
		Map<String, String> jobProperties = null;
		public String getName() {
			return name;
		}
		public void setName(String tenantName) {
			this.name = tenantName;
		}
		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getJobType() {
			return jobType;
		}
		public void setJobType(String jobType) {
			this.jobType = jobType;
		}
		public Long getParentJobId() {
			return parentJobId;
		}
		public void setParentJobId(Long parentJobId) {
			this.parentJobId = parentJobId;
		}
		public Map<String, String> getJobProperties() {
			return jobProperties;
		}
		public void setJobProperties(Map<String, String> jobProperties) {
			this.jobProperties = jobProperties;
		}
	}

}
