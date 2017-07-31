package com.rnctech.um.web.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.apache.log4j.Logger;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.rnctech.um.web.config.UMWebQuartzConfig;
import com.rnctech.um.web.utils.UMWebUtils;


/**
 * @author Zilin Chen
 *
 */

@DisallowConcurrentExecution
public class UMSimpleJob extends RNCTechJob {

	public UMSimpleJob(){}
	
	public UMSimpleJob(long frequency) {
		super();
		this.frequency = frequency;
	}

	private static final Logger logger = Logger.getLogger(UMSimpleJob.class);

	private long frequency;

	@Override
	public void execute(JobExecutionContext jobContext) {
		if(jobContext.getJobDetail().getJobDataMap().entrySet().isEmpty())
			return;
		String[] args = buildArgs(jobContext.getJobDetail().getJobDataMap());
		logger.info("Running Job With " + UMWebUtils.aTos(args));
		RunJobMain(args);		
	}

	public SimpleTriggerFactoryBean umJobTrigger(JobDetail jobDetail) {
		SimpleTriggerFactoryBean stfb = UMWebQuartzConfig.createTrigger(jobDetail, frequency);
		stfb.setBeanName(getTriggerKeyName(UMSimpleJob.class.getClass()));
		stfb.afterPropertiesSet();
		return stfb;
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
