package com.ffm.lms.branch;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BranchValidator {
	
	private final Validator validator;
	private final BranchRepository repository;
	
	public void validate(BranchCreateDTO branch) {
		validateFields(branch);
		doesCodeExist(branch.getCode());
	}
	
	private void validateFields(BranchCreateDTO branch) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(branch, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}
	
	private void doesCodeExist(String code){
		Optional<Branch> branch = repository.findByCode(code);
		if(branch.isPresent())
			throw new ApplicationException(String.format("Branch with code(%s) already exist", code));
	}
}
