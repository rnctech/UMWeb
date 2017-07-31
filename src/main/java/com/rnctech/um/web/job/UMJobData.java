package com.rnctech.um.web.job;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"UMSession","description","jobkey"})
public class UMJobData {

	String mrurl = null;
	String user = null;
	String password = null;
	JobData command = null;
	Long jobid = -1l;
	Boolean allowssl = false;
	String kafkabrokers = null;
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
	public Boolean getAllowssl() {
		return allowssl;
	}
	public void setAllowssl(Boolean allowssl) {
		this.allowssl = allowssl;
	}
	public String getKafkabrokers() {
		return kafkabrokers;
	}
	public void setKafkabrokers(String kafkabrokers) {
		this.kafkabrokers = kafkabrokers;
	}
	public String getMrurl() {
		return mrurl;
	}
	public void setMrurl(String mrurl) {
		this.mrurl = mrurl;
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
		String tenantName = null;
		String sourceSystemName = null;
		String sourceSystemInstanceName = null;
		String entityCategory = null;
		String jobName = null;
		String loadType = "INCREMENTAL";
		String jobType = "ETL";
		Long parentJobId = null;
		Long externalParentJobId = null;
		Map<String, String> jobProperties = null;
		public String getTenantName() {
			return tenantName;
		}
		public void setTenantName(String tenantName) {
			this.tenantName = tenantName;
		}
		public String getSourceSystemName() {
			return sourceSystemName;
		}
		public void setSourceSystemName(String sourceSystemName) {
			this.sourceSystemName = sourceSystemName;
		}
		public String getSourceSystemInstanceName() {
			return sourceSystemInstanceName;
		}
		public void setSourceSystemInstanceName(String sourceSystemInstanceName) {
			this.sourceSystemInstanceName = sourceSystemInstanceName;
		}
		public String getEntityCategory() {
			return entityCategory;
		}
		public void setEntityCategory(String entityCategory) {
			this.entityCategory = entityCategory;
		}
		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getLoadType() {
			return loadType;
		}
		public void setLoadType(String loadType) {
			this.loadType = loadType;
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
		public Long getExternalParentJobId() {
			return externalParentJobId;
		}
		public void setExternalParentJobId(Long externalParentJobId) {
			this.externalParentJobId = externalParentJobId;
		}
		public Map<String, String> getJobProperties() {
			return jobProperties;
		}
		public void setJobProperties(Map<String, String> jobProperties) {
			this.jobProperties = jobProperties;
		}
	}

}
