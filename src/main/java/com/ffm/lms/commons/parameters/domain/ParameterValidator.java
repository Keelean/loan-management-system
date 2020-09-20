package com.ffm.lms.commons.parameters.domain;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.util.List;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ParameterValidator {
	
	private final Validator validator;
	
	public void validate(ParameterDTO parameter) {
		validateFields(parameter);
	}

	private void validateFields(ParameterDTO parameter) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(parameter, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}
}
