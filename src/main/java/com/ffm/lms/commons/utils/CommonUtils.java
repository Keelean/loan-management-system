package com.ffm.lms.commons.utils;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validator;

public class CommonUtils {

	private CommonUtils() {
	}

	public static <T> List<String> getStaticFieldValidationErrors(T entity, Validator validator) {
		return validator.validate(entity).stream().map(violation -> violation.getMessage()).collect(Collectors.toList());

	}
}
