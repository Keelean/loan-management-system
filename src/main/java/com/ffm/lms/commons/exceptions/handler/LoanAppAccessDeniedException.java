package com.ffm.lms.commons.exceptions.handler;

import org.springframework.security.access.AccessDeniedException;

public class LoanAppAccessDeniedException extends AccessDeniedException {

	public LoanAppAccessDeniedException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
}
