package com.ffm.lms.loan.transaction.domain;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.util.List;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.transaction.domain.dto.TransactionDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TransactionValidator {

	private final Validator validator;

	public void validate(TransactionDTO transaction) {
		validateFields(transaction);
	}

	private void validateFields(TransactionDTO transaction) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(transaction, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}
	
	public void isAmountPositive(TransactionDTO txDTO) {
		
		if(txDTO.getAmount().doubleValue() < 0) {
			throw new ApplicationException("Loan amount is invalid!");
		}
	}
}
