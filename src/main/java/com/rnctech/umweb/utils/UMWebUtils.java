package com.rnctech.umweb.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.slf4j.MDC;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.google.gson.Gson;
import com.rnctech.umweb.UMWebConsts;
import com.rnctech.umweb.exception.UMWebException;

/**
 * @contributor zilin
 * 
 */

public class UMWebUtils implements UMWebConsts {
	
	private static final String RUNID_PREFIX = "RunIdentifier - ";
	private static final String UM_SESSION_ID = "UM_SESSION_ID";
	
	// DataIngester-6.0.jar:pipelineutils-6.0-jar-with-dependencies.jar:PlatformUtil-6.0.jar:SourceHandler-6.0.jar:httpcore-4.4.4.jar:httpclient-4.4.1.jar:
	//guava-20.0.jar:log4j-1.2.17.jar:org.apache.commons.codec-1.3.0.jar:cxf-bundle-2.7.5.jar:javax.ws.rs-api-2.0.1.jar:javax.ws.rs-api-2.0-m10.jar:neethi-3.0.2.jar:httpcore-nio-4.2.2.jar:httpasyncclient-4.0-beta3.jar:wsdl4j-1.6.3.jar:commons-io-2.4.jar;
	public static void loadClass() throws Exception {
			ClassLoader cl = new URLClassLoader(
					new URL[] { new File("commons-codec-1.6.jar").toURL(), new File("httpcore-4.4.4.jar").toURL(), new File("httpclient-4.4.1.jar").toURL() },
					Thread.currentThread().getContextClassLoader());
			cl.loadClass("org.apache.commons.codec.binary.Base64");
			cl.loadClass("org.apache.http.impl.auth.BasicScheme");
			cl.loadClass("org.apache.http.impl.auth.BasicSchemeFactory");
	}
		
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String aTos(String[] args){
		StringBuilder argsb = new StringBuilder();
		for(String s : args){
			argsb.append(s).append(" ");
		}
		return argsb.toString();
	}
	
	public static String getRunID(String ssi) {
		return ssi+ RUNID_PREFIX + getUUID();
	}
	
	public static void putTransactionId() {
		MDC.put(TXN_ID, UMWebUtils.getUUID());
	}
	
	public static String getTransactionId() {
		if(null == MDC.get(TXN_ID)) {
			putTransactionId();
		}
		return MDC.get(TXN_ID);
	}
	
	public static void clearTransactionId() {
		MDC.remove(TXN_ID);
	}
	
	private static boolean isCollectionEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static boolean isObjectEmpty(Object object) {
		if(object == null) return true;
		else if(object instanceof String) {
			if (((String)object).trim().length() == 0) {
				return true;
			}
		} else if(object instanceof Collection) {
			return isCollectionEmpty((Collection<?>)object);
		}
		return false;
	}	

	public static String getBeanToJsonString(Object beanClass) {
		return new Gson().toJson(beanClass);
	}
	
	public static String getBeanToJsonString(Object... beanClasses) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Object beanClass : beanClasses) {
			stringBuilder.append(getBeanToJsonString(beanClass));
			stringBuilder.append(", ");
		}
		return stringBuilder.toString();
	}
	
	public static String concatenate(List<String> listOfItems, String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> stit = listOfItems.iterator();
		
		while (stit.hasNext()) {
			sb.append(stit.next());
			if(stit.hasNext()) {
				sb.append(separator);
			}
		}
		
		return sb.toString();		
	}
	
	public static String getConnectedTenant(HttpServletRequest request) throws UMWebException {
		String tenant = (String)RequestContextHolder.getRequestAttributes().getAttribute("tenant", RequestAttributes.SCOPE_REQUEST);		
		if(null == tenant && null != request.getSession(false)){
			tenant = (String)request.getSession(false).getAttribute(UM_SESSION_ID);
		}
		if(null == getAuthentication())
			throw new UMWebException("No authentication");
		String loginUserTenant = getAuthentication().getName();
		if(tenant.equals(loginUserTenant))
			return tenant;
		else
			throw new UMWebException("Authenticate failed "+tenant+" vs authenticated name "+loginUserTenant);
	}
	
	
	public static String getTenant(HttpServletRequest request) {		
		if(null != request.getSession(false)){
			return (String)request.getSession(false).getAttribute(UM_SESSION_ID);
		}
		return null;
	}
	
	public static String getTenant() {	
		String tenant = (String)RequestContextHolder.getRequestAttributes().getAttribute(UM_SESSION_ID, RequestAttributes.SCOPE_SESSION);
		if(null == tenant)
			tenant = (String)RequestContextHolder.getRequestAttributes().getAttribute("tenant", RequestAttributes.SCOPE_REQUEST);
		return tenant;
	}
	
	public static Authentication getAuthentication() {
		Authentication authentication = null;

		// The context in here never null
		SecurityContext context = SecurityContextHolder.getContext();
		authentication = context.getAuthentication();

		return authentication;
	}
	
	public static List<String> getRolesOfUser() {
		List<String> callerAuthList = new ArrayList<String>();
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			for (GrantedAuthority grantedAuthority : authorities) {
				callerAuthList.add(grantedAuthority.getAuthority());
			}
			return callerAuthList;
		}
		return null;
	}

	public static boolean hasAdminRole() {
		List<String> callerAuthList = getRolesOfUser();
		if (callerAuthList.size() > 0) {
			return true;
		}
		return false;
	}
}
