package com.ffm.lms.commons.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationResponseInterceptor implements HandlerInterceptor{

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
			String newContent = "{ \"name\" : \"************r\" }";
			//response.setContentLength(newContent .length());
			//response.getWriter().write(newContent);
		}
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
