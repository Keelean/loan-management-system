package com.ffm.lms.commons.data.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.exceptions.handler.LoanAppAccessDeniedException;
import com.ffm.lms.commons.exceptions.handler.LoanAppAuthenticationException;
import com.ffm.lms.commons.exceptions.handler.LoanAppDataAccessException;


@ControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ApiResponseBase<String>> genericException(Exception e) {
		return error(e, HttpStatus.NOT_FOUND, e.getMessage().toString());
	}

	@ExceptionHandler({ LoanAppAuthenticationException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ApiResponseBase<String>> authentiction(final LoanAppAuthenticationException e) {
		return error(e, HttpStatus.UNAUTHORIZED, e.getMessage());
	}
	
	@ExceptionHandler({ LoanAppAccessDeniedException.class })
	public ResponseEntity<ApiResponseBase<String>> accessDeniedException(final AccessDeniedException e) {
		return error(e, HttpStatus.NOT_FOUND, e.getMessage());
	}
	
	@ExceptionHandler({ LoanAppDataAccessException.class })
	public ResponseEntity<ApiResponseBase<String>> dataAccesException(final DataAccessException e) {
		return error(e, HttpStatus.NOT_FOUND, e.getMessage());
	}
	
	@ExceptionHandler({ ApplicationException.class })
	public ResponseEntity<ApiResponseBase<String>> runtimeException(final ApplicationException e) {
		return error(e, HttpStatus.NOT_FOUND, e.getError().toString());
	}

	
	public ResponseEntity<ApiResponseBase<String>> notFoundException(final RuntimeException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getMessage().toString());
    }
	
    private ResponseEntity<ApiResponseBase<String>> error(final Exception exception, final HttpStatus httpStatus, final String logRef) {
        final String message = Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        List<String> errors = new ArrayList<>();
        String authCode = null;
        String msg = "Something went wrong!";
    	if(exception instanceof ApplicationException) {
    		ApplicationException ex = (ApplicationException)exception;
    		errors.addAll(ex.getError());
    		authCode = ex.getErrorCode();
    		msg = ex.getMessage();
    	}
    	
        ApiResponseBase<String> errorMessage = new ApiResponseBase<String>();
        errorMessage.setErrors(errors);
        errorMessage.setStatus("failed");
        errorMessage.setResponse("Error");
        errorMessage.setMessage(msg);
        errorMessage.setSuccess(false);
        errorMessage.setAuthCode(authCode);
        return new ResponseEntity<>(errorMessage, httpStatus);
    }
    
    @ExceptionHandler(IllegalArgumentException.class) 
    public ResponseEntity<ApiResponseBase<String>> assertionException(final IllegalArgumentException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage());
    }
}
