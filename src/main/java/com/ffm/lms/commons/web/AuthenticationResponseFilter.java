package com.ffm.lms.commons.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
//@Order(1)
public class AuthenticationResponseFilter implements Filter {
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		chain.doFilter(request, response);
		if (res.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
			
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			Map<String, Object> data = new HashMap<>();
			data.put("message", "Authentication error");
			data.put("status", "failure");
			data.put("response", "Please login with username and password to acces this resource");
			data.put("isSuccess", "false");
			data.put("errors", Arrays.asList("Authentication error!"));

			res.getOutputStream().println(objectMapper.writeValueAsString(data));
		}
		System.out.println("Response Status Code is: " + res.getStatus());

	}

	@Bean
	public FilterRegistrationBean<AuthenticationResponseFilter> filter() {
		FilterRegistrationBean<AuthenticationResponseFilter> bean = new FilterRegistrationBean<>();

		bean.setFilter(new AuthenticationResponseFilter());
		bean.addUrlPatterns("/lms/api/*"); // or use setUrlPatterns()

		return bean;
	}
}
