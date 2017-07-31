package com.rnctech.um.web;

import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.rnctech.um.web.config.UMWebWebConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestAppConfig.class, UMWebWebConfig.class})
@TestExecutionListeners(listeners = {
		DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		UMWebTest.class })
@WebAppConfiguration
@ActiveProfiles("test")
public class UMWebTest extends AbstractTestExecutionListener {


	
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {

		List<SimpleGrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority("role_ldbadmin"));

		Authentication testingAuthenticationToken = new UsernamePasswordAuthenticationToken(
				"admin", "Admin123", authorities);

		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(testingAuthenticationToken);
		SecurityContextHolder.setContext(securityContext);

	}
}
