package com.ffm.lms.commons.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoanAppMetricInterceptor implements HandlerInterceptor {

	private MeterRegistry registry;
	private String URI, pathKey, METHOD;

	public LoanAppMetricInterceptor(MeterRegistry registry) {
		super();
		this.registry = registry;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		URI = request.getRequestURI();
		METHOD = request.getMethod();
		if (!URI.contains("prometheus")) {
			log.info(" >> PATH: {}", URI);
			log.info(" >> METHOD: {}", METHOD);
			pathKey = "api_".concat(METHOD.toLowerCase()).concat(URI.replaceAll("/", "_").toLowerCase());
			this.registry.counter(pathKey).increment();
		}
	}

}
