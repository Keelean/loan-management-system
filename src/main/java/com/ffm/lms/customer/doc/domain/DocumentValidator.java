package com.ffm.lms.customer.doc.domain;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.util.List;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.doc.domain.dto.DocumentDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DocumentValidator {

	public final Validator validator;

	public void validate(DocumentDTO document) {
		validateFields(document);
	}

	private void validateFields(DocumentDTO document) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(document, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}
}
