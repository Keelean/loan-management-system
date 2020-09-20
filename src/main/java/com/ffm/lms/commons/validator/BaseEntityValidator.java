package com.ffm.lms.commons.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public class BaseEntityValidator<T> implements EntityValidator<T> {
	
	protected final Validator validator;

	public BaseEntityValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	public Set<ConstraintViolation<T>> validate(T t, Validator validator) {
		return validator.validate(t);
	}

}
