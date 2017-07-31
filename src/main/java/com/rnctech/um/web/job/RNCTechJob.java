package com.rnctech.um.web.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnctech.um.web.utils.UMWebUtils;
import com.rnctech.um.web.utils.UMShellScriptUtils;


/**
 * @author Zilin Chen
 */

public abstract class RNCTechJob implements InterruptableJob {


	public static Logger logger = Logger.getLogger(RNCTechJob.class);

	public abstract FactoryBean<? extends Trigger> umJobTrigger(
			JobDetail jobDetail);

	public abstract JobDetailFactoryBean getUMJob(UMJobData config);

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		logger.info("Running Job at " + new Date());
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		logger.info("Job interrupt at " + new Date());

	}

	public String getJobKeyName(UMJobData config) {
		return config.getCommand().getName() + "_UMJob";
	}

	public static String getTriggerKeyName(Class clz) {
		return clz.getName() + "_UMTrigger";
	}

	public JobDataMap buildJobMap(UMJobData config) {
		ObjectMapper oMapper = new ObjectMapper();
		JobDataMap jobMap = new JobDataMap();
		Map<String, Object> map = oMapper.convertValue(config, Map.class);
		jobMap.putAll(map);
		try {
			jobMap.remove(UMShellScriptUtils.UMWEB_JOB_CMD);
			String ajd = oMapper.writer()
					.writeValueAsString(config.getCommand());
			jobMap.put(UMShellScriptUtils.UMWEB_JOB_CMD, ajd);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}

		return jobMap;
	}

	public String[] buildArgs(JobDataMap dataMap) {
		int length = dataMap.size();
		List<String> arglist = new ArrayList<>();
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			Object o = entry.getValue();
			if (null != o) {
				arglist.add("--" + entry.getKey().trim().toLowerCase());
				if (entry.getKey().equals(UMShellScriptUtils.UMWEB_JOB_CMD)) {
					arglist.add(entry.getValue().toString().replaceAll("\"",
							"\\\""));
				} else {
					arglist.add(entry.getValue().toString());
				}
			}
		}
		String[] args = new String[arglist.size()];
		arglist.toArray(args);
		return args;

	}

	public String executeNow(UMJobData jobdata) {
		String[] args = UMShellScriptUtils.buildArgs(jobdata, logger);
		logger.info("Running Job With " + UMWebUtils.aTos(args));
		try {
			RunJobMain(args);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			t.printStackTrace();
			return "Job "+jobdata.getJobkey()+" successed with exception: \n"+UMWebUtils.aTos(args);
		}
		logger.info("Job "+jobdata.getJobkey()+" successed. "+UMWebUtils.aTos(args));
		return "Job "+jobdata.getJobkey()+" successed. "+UMWebUtils.aTos(args);
	}
	
	
	public static void RunJobMain(String[] args){
		System.out.println("Call main() successed! with args "+args);
	}
}
