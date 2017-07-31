package com.rnctech.um.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * @contributor zilin
 * 
 */

@Configuration
@PropertySource({ "classpath:/umweb.properties" })
@EnableAspectJAutoProxy
public class UMWebConfig {

	@Value("${com.ldb.adapter.exec.type}")
	private String execType;

	@Autowired
	Environment environment;

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public UMWebLoggingAspect UMLogger(){
		return new UMWebLoggingAspect();
	}

}
