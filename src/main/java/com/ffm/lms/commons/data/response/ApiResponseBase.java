package com.ffm.lms.commons.data.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiResponseBase<T> {
	
	private String message;
	private String status;
	private T response;
	private List<String> errors;
	private boolean isSuccess;
	private String authCode;
}
