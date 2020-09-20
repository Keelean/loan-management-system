package com.ffm.lms.commons.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.exceptions.handler.LoanAppAuthenticationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	 private ObjectMapper objectMapper = new ObjectMapper();
	 

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

			log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			Map<String, Object> data = new HashMap<>();
			data.put("message", "Authentication error");
			data.put("status", "401");
			data.put("response", "Please login with username and password to acces this resource");
			data.put("isSuccess", Boolean.FALSE);
			data.put("errors", Arrays.asList("Authentication error!","The resource you are trying to access does not exist","You may not have the permission to access this resouce"));
			response.addHeader("WWW-Authenticate", "Basic realm=");
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getOutputStream().println(objectMapper.writeValueAsString(data));
			//throw new ApplicationException("Authentication Error", "Authentication Error!", Arrays.asList("Authentication error!","The resource you are trying to access does not exist",
					//"You may not have the permission to access this resouce"));
			//ApiResponseBase rr = objectMapper.convertValue(objectMapper.writeValueAsString(data),ApiResponseBase.class);
	}

}
