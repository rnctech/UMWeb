package com.rnctech.um.web.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rnctech.um.web.utils.UMWebUtils;

/**
 * @contributor zilin
 * 
 */

@Component
@Aspect
public class UMWebLoggingAspect {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution(* com.rnctech.um.web.api..*.*(..)) && @target(org.springframework.stereotype.Controller)")
	public void controller() {
	}

	@Before("controller()")
	public void logBeforeController(JoinPoint joinPoint) {
		logger.debug("UM api: Begin call to " + joinPoint.getThis().getClass()+"."+joinPoint.getSignature().getName() + " by "
				+ getAuthenticatedUserName() + " with tenant " + getTenantName());
	}

	@After("controller()")
	public void logAfterController(JoinPoint joinPoint) {
		logger.debug("UM api: End call to " + joinPoint.getThis().getClass()+"."+joinPoint.getSignature().getName() + " by "
				+ getAuthenticatedUserName() + " with tenant " + getTenantName());
	}

	private String getAuthenticatedUserName() {		
		return  "anonymous";
	}

	public String getTenantName() {
		String tenant = UMWebUtils.getTenant();
		return (null==tenant)?"":tenant;
	}
}