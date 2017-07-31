package com.rnctech.umweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;

/**
 * @contributor zilin
 * 
 */

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class TestApplication {

	     public static void main(String[] args) throws Exception {
	    	 System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "test");
	    	 SpringApplication.run(TestApplication.class, args);
	     }
	 }
