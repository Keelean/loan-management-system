package com.ffm.lms.commons.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	//private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		/*Map<String, Object> data = new HashMap<>();
		data.put("message", "Authentication error");
		data.put("status", "failure");
		data.put("response", "Please login with username and password to acces this resource");
		data.put("isSuccess", "false");
		data.put("errors", Arrays.asList("Authentication error!"));
		response.getOutputStream().println(objectMapper.writeValueAsString(data));*/
	}

}
