package com.rnctech.um.web.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.rnctech.um.web.utils.UMWebUtils;

/**
 * @contributor zilin
 * 
 */
@Controller
public class UMWebInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = LoggerFactory
			.getLogger(UMWebInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {			
		if(request.getRequestURI().endsWith("/error") || request.getRequestURI().endsWith("/ping") || request.getRequestURI().endsWith("/role"))
			return true;
		
		if(request.getRequestURI().endsWith("/logout")) {
			request.getSession(false).invalidate();
			return true;
		}
		
		String p = request.getContextPath()+"/";
		if(request.getRequestURI().equals(p) || request.getRequestURI().equals(p+"api/")){	
			if(null == request.getSession(false)){ // direct access
				
			}else if(null != UMWebUtils.getTenant(request)){
				String tname = UMWebUtils.getTenant(request);
			}else{
				response.setStatus(400);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "please using main page to access this application!");
				logger.error("please using main page to access this application!");
				return false;
			}

		} else {
			return false;			
		}
		return true;		
	}
	
	@Override
	public void postHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		
	}

}
