package com.rnctech.umweb;
/**
 * Copyright Numerify 2016. All rights reserved.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * @contributor zilin
 * 
 */

@Configuration
@PropertySource({ "classpath:/umweb.test.properties"})
@ComponentScan({ "com.rnctech.um.web"})
@Profile("test")
public class TestAppConfig {
	
	@Value("${persistence.type}")
	private String persistenceType;
	
	@Autowired
	Environment environment;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	
}
