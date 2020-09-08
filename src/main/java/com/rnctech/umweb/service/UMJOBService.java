package com.rnctech.umweb.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Service;

import com.rnctech.umweb.job.RNCTechJob;
import com.rnctech.umweb.job.UMCronJob;
import com.rnctech.umweb.job.UMJobData;
import com.rnctech.umweb.job.UMSimpleJob;
import com.rnctech.umweb.utils.UMProcessExitDetector;
import com.rnctech.umweb.utils.UMShellScriptUtils;

@Service
public class UMJOBService {

	public static Logger logger = Logger.getLogger(UMJOBService.class);
	public Map<String, UMJobData> tmap = new ConcurrentHashMap<>();
	public Map<String, Process> tp = new ConcurrentHashMap<>();
	
	@Value("${quartz.enabled}")
	boolean isSchedule;
	
	@Value("${umweb.schedule.type}")
	String scheduleType;
	
	@Value("${umweb.exec.type}")
	String execType;
	
	@Value("${umweb.cron.jobtrigger}")
	private long frequency;
	
	@Value("${umweb.cron.jobexpression}")
    private String jobexpr;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ApplicationContext appContext;
	
/*	@Autowired
	private SchedulerFactoryBean schedFactory;*/
	
	
	public String scheduleUMJob(UMJobData jobdata){
		String scheduled = "Job is Scheduled!!";
		try {
			tmap.put(jobdata.getCommand().getName(), jobdata);
			if(execType.equals("standalone")){
				logger.info("run as "+ execType +" start @ "+new Date());
				boolean waitProcessDone = true;
				Object ret = UMShellScriptUtils.executeJava(jobdata, null, jobdata.getCommand().getName(), waitProcessDone, logger);
				if(waitProcessDone && ret instanceof String){
					tmap.remove(jobdata.getCommand().getName());
					scheduled = (String)ret;
				}else{
					Process p = (Process)ret;					
					tp.put(jobdata.getCommand().getName(), p);
					scheduled = "Job "+jobdata.getJobkey()+" kicked off.";
					try {
						UMProcessExitDetector processExitDetector = new UMProcessExitDetector(p);
						processExitDetector.addProcessListener(new UMProcessExitDetector.ProcessListener() {
						    public void processFinished(Process process) {
						    	logger.info("The subprocess"+p+" has finished.");
						        tmap.remove(jobdata.getCommand().getName());
						    }
						});
						processExitDetector.start();
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}else{  //in container kickoff
				RNCTechJob ajob = null;
				if(scheduleType.equalsIgnoreCase("cron")){
					ajob = new UMCronJob(jobexpr);
				}else{
					ajob = new UMSimpleJob(frequency);
				}
				if(isSchedule){
					JobDetail jd = ajob.getUMJob(jobdata).getObject();
					logger.info("ready for job "+jobdata.getJobkey()+" as detail "+jd+" for type "+scheduleType);
					Trigger t = ajob.umJobTrigger(jd).getObject();
					logger.info("Trigger as "+t+" start @ "+t.getStartTime());
					SchedulerFactoryBean schedFactory = appContext.getBean(SchedulerFactoryBean.class);
					schedFactory.getScheduler().scheduleJob(jd, t);
				}else{
					logger.info("job run start @ "+new Date());
					scheduled = ajob.executeNow(jobdata);
					tmap.remove(jobdata.getCommand().getName());
				}
			}			
		} catch (Exception e) {
			scheduled = "Could not schedule a job. " + e.getMessage();
		}
		return scheduled;
	}
	
	public String getExecutingJobs(){
		StringBuilder rj = new StringBuilder();
		try {	
			if(execType.equals("standalone") || !isSchedule){
				tmap.forEach((t, ajd) -> {
					rj.append(t).append(" : ").append(ajd.getJobkey());
				});
			}else{
				SchedulerFactoryBean schedFactory = appContext.getBean(SchedulerFactoryBean.class);
				List<JobExecutionContext> jobctxs = schedFactory.getScheduler().getCurrentlyExecutingJobs();
				jobctxs.forEach(jec -> rj.append(jec.getJobDetail().getKey()).append(","));
			}
		} catch (Exception e) {
			rj.append("Exception: " + e.getMessage());
		}
		return rj.toString();
	}
	
	public String getJobHistory(String tenant) {
		if(!execType.equals("standalone") && isSchedule){
			UMJobData jobdata = tmap.get(tenant);
			if(null != jobdata){
				TriggerKey tkey = new TriggerKey(RNCTechJob.getTriggerKeyName(UMSimpleJob.class));
				if(scheduleType.equalsIgnoreCase("cron")){
					tkey = new TriggerKey(RNCTechJob.getTriggerKeyName(UMCronJob.class));
				}	
				//@Todo retrieve job history 
			}
		}
		return "No history available.";
	}

    //need Ingestion API to cancel job elengently instead of kill the process
	public String cancelumJob(String tenant) {
		StringBuilder ret = new StringBuilder("Ready to cancel job ");
		UMJobData jobdata = tmap.get(tenant);
		if(null != jobdata){
			if(execType.equals("standalone")){
				Process p = tp.get(tenant);
				if(null != p)
					try {
						p.destroyForcibly();
						ret.append("... successful canceled.");
					} catch (Exception e) {
						ret.append("\nError when try to cancel job "+e.getMessage());
						logger.error(ret);
					}
			}else{
				if(isSchedule){
					TriggerKey tkey = new TriggerKey(RNCTechJob.getTriggerKeyName(UMSimpleJob.class));
					if(scheduleType.equalsIgnoreCase("cron")){
						tkey = new TriggerKey(RNCTechJob.getTriggerKeyName(UMCronJob.class));
					}		
					JobKey jkey = new JobKey(jobdata.getJobkey());
					ret.append(jkey.getName()+" trigger "+tkey.getName());
					logger.info(ret);
					try {
						SchedulerFactoryBean schedFactory = appContext.getBean(SchedulerFactoryBean.class);
						schedFactory.getScheduler().unscheduleJob(tkey);
						schedFactory.getScheduler().deleteJob(jkey);
						ret.append("... successful unscheduled.");
					} catch (SchedulerException e) {
						ret.append("\nError while unscheduling " + e.getMessage());
						logger.error(ret);
					}
				}else{
					//@Todo cancel Ingestion.main
				}
			}
		}
		return ret.toString();
	}	
}
