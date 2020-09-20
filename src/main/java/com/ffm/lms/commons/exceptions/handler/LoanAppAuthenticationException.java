package com.ffm.lms.commons.exceptions.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanAppAuthenticationException extends AuthenticationException {

	private String errorCode;
	private List<String> error;
	
	
	public LoanAppAuthenticationException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
		setError(Arrays.asList(this.getMessage()));
	}
	
	public LoanAppAuthenticationException(List<String> errors, String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
		this.error = errors;
	}

	public LoanAppAuthenticationException(String message) {
		super(message);
		setError(Arrays.asList(this.getMessage()));
	}
	
	public LoanAppAuthenticationException(String message, String errorCode, List<String> errors) {
		super(message);
		this.errorCode = errorCode;
		this.error = errors;
	}

	public LoanAppAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LoanAppAuthenticationException(String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

}
