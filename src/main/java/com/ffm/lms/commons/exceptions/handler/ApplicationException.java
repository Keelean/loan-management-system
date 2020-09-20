package com.ffm.lms.commons.exceptions.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class ApplicationException extends RuntimeException {
	
	private String errorCode;
	private List<String> error;
	
	public ApplicationException() {
		super();
	}
	
	public ApplicationException(String message, String errorCode) {
		super();
		this.errorCode = errorCode;
		setError(Arrays.asList(this.getMessage()));
	}
	
	public ApplicationException(List<String> errors, String errorCode) {
		super();
		this.errorCode = errorCode;
		this.error = errors;
	}

	public ApplicationException(String message) {
		super(message);
		setError(Arrays.asList(this.getMessage()));
	}
	
	public ApplicationException(String message, String errorCode, List<String> errors) {
		super(message);
		this.errorCode = errorCode;
		this.error = errors;
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ApplicationException(String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}
}
