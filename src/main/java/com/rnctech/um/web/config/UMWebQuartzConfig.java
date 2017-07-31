package com.rnctech.um.web.config;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.joda.time.LocalDateTime;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @author Zilin Chen
 *
 */

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class UMWebQuartzConfig {

    @Autowired
    DataSource dataSource;

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,
			JobFactory jobFactory) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setAutoStartup(true);
		factory.setDataSource(dataSource);
		factory.setJobFactory(jobFactory);
		factory.setQuartzProperties(quartzProperties());
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean
				.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}


	public static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail,
			long pollFrequencyMs) {
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
		factoryBean.setName("simple-trigger"+System.currentTimeMillis());
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setStartDelay(0L);
		factoryBean.setRepeatInterval(pollFrequencyMs);
		factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		// in case of misfire, ignore all missed triggers and continue :
		factoryBean.setMisfireInstruction(
				SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        try {
            factoryBean.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return factoryBean;
	}

	// Use this method for creating cron triggers instead of simple triggers:
	public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail,
			String cronExpression) {
		AdapterCronTriggerFactoryBean factoryBean = new AdapterCronTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);
		factoryBean.setCronExpression(cronExpression);		
        factoryBean.setName("cron-trigger"+System.currentTimeMillis());
        factoryBean.setStartTime(LocalDateTime.now().toDate());
        factoryBean.setEndTime(LocalDateTime.now().plusMinutes(30).toDate());
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return factoryBean;
	}

	public static JobDetailFactoryBean createJobDetail(Class jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		// job has to be durable to be stored in DB:
		factoryBean.setDurability(true);
        factoryBean.setName("adapter-job"+System.currentTimeMillis());
		factoryBean.afterPropertiesSet();
		return factoryBean;
	}
	
/*	public static class AdapterSimpleTriggerFactoryBean extends SimpleTriggerFactoryBean {

		@Override
		public void afterPropertiesSet() {
			super.afterPropertiesSet();
			// Remove the JobDetail element
			getJobDataMap().remove(JobDetailAwareTrigger.JOB_DETAIL_KEY);
		}
	}*/

	public static class AdapterCronTriggerFactoryBean extends CronTriggerFactoryBean {
	    private Date endTime;
	    public void setEndTime(Date endTime) {
	        this.endTime = endTime;
	    }

	    @Override
	    public void afterPropertiesSet() throws ParseException {
	        super.afterPropertiesSet();


	        if (super.getObject() != null) {
	            CronTriggerImpl object = (CronTriggerImpl) super.getObject();
	            object.setEndTime(endTime);
	        }
	    }
	}
	
	public final class AutowiringSpringBeanJobFactory
			extends SpringBeanJobFactory implements ApplicationContextAware {

		private transient AutowireCapableBeanFactory beanFactory;

		@Override
		public void setApplicationContext(final ApplicationContext context) {
			beanFactory = context.getAutowireCapableBeanFactory();
		}

		@Override
		protected Object createJobInstance(final TriggerFiredBundle bundle)
				throws Exception {
			final Object job = super.createJobInstance(bundle);
			beanFactory.autowireBean(job);
			return job;
		}
	}
}
