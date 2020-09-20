package com.ffm.lms.commons.exceptions.handler;

import org.springframework.dao.DataAccessException;

public class LoanAppDataAccessException extends DataAccessException{

	private static final long serialVersionUID = 1L;

	public LoanAppDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}
	
	public LoanAppDataAccessException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
