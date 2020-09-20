package com.ffm.lms.commons.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public interface EntityValidator<T> {
	Set<ConstraintViolation<T>> validate(T t, Validator validator);
}
