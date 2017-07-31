package com.rnctech.umweb.job;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import com.rnctech.umweb.config.UMWebQuartzConfig;

/**
 * @author Zilin Chen
 */

@DisallowConcurrentExecution
public class UMCronJob extends RNCTechJob {
	
	public UMCronJob(){}
	
	public UMCronJob(String jobexpr) {
		super();
		this.jobexpr = jobexpr;
	}
	
    private String jobexpr;
	


	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		logger.info("Running Job With Trigger " + jobexpr);
		String[] args = buildArgs(jobContext.getMergedJobDataMap());
		RunJobMain(args);
		logger.info("Running "+this.getClass().getName());
	}

	public CronTriggerFactoryBean umJobTrigger(JobDetail jobDetail) {
		CronTriggerFactoryBean ctfb = UMWebQuartzConfig.createCronTrigger(jobDetail,jobexpr);
		ctfb.setBeanName(getTriggerKeyName(UMSimpleJob.class.getClass()));
		try {
			ctfb.afterPropertiesSet();
		} catch (ParseException e) {
		}
		return ctfb;
    }

	public JobDetailFactoryBean getUMJob(UMJobData config) {
		JobDetailFactoryBean jBean = UMWebQuartzConfig.createJobDetail(this.getClass());
		jBean.setBeanName(config.getJobkey());
		JobDataMap jobMap = buildJobMap(config);
		jBean.setJobDataMap(jobMap);
		jBean.afterPropertiesSet();
        return jBean;
    }


}
