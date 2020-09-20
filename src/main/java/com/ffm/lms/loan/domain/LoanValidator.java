package com.ffm.lms.loan.domain;

import static com.ffm.lms.commons.utils.CommonUtils.getStaticFieldValidationErrors;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanApprovalDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoanValidator {

	private final Validator validator;
	private final LoanRepository repository;
	private final CustomerService customerService;

	public void validate(LoanDTO loan) {
		validateFields(loan);
	}

	public void isCustomerValid(LoanDTO loanDTO) {
		boolean exist = customerService.exist(loanDTO.getCustomerId());

		if (!exist)
			throw new ApplicationException("Customer does not exist");

		CustomerResponseDTO customer = customerService.findById(loanDTO.getCustomerId());

		/*
		if (customer.getStatus() == null || !(customer.getStatus().equals(CustomerStatus.VERIFIED)))
			throw new ApplicationException("Customer have not been verified");*/
	}

	private void validateFields(LoanDTO loan) {
		List<String> fieldValidationErrors = getStaticFieldValidationErrors(loan, validator);

		if (fieldValidationErrors.isEmpty()) {
			return;
		}
		throw new ApplicationException("Invalid Fields", "", fieldValidationErrors);
	}

	public void isEligibleForLoan(LoanDTO loan) {
		if (loan.getLoanType().equals(LoanType.PERSONAL))
			isEligibleForPersonalLoan(loan);
		else
			isEligibleForGovtLoan(loan);

	}

	private void isEligibleForPersonalLoan(LoanDTO loan) {
		List<Loan> loans = repository.findAllActiveLoans(loan.getCustomerId());

		if (loans == null || loans.isEmpty())
			return;

		for (Loan l : loans) {
			if ((l.getLoanType().equals(LoanType.PERSONAL) && l.getStatus().equals(LoanStatus.ACTIVE)) || 
					(l.getLoanType().equals(LoanType.PERSONAL) && l.getStatus().equals(LoanStatus.INACTIVE)))
				throw new ApplicationException(
						l.getLoanType().getCode() + " Loan for this customer already exist.Approve if inactive");
		}
	}

	private void isEligibleForGovtLoan(LoanDTO loan) {
		List<Loan> loans = repository.findAllActiveLoans(loan.getCustomerId());

		if (loans == null || loans.isEmpty())
			return;

		for (Loan l : loans) {
			if ((l.getLoanType().equals(LoanType.LOCAL) && l.getStatus().equals(LoanStatus.ACTIVE)) || 
					(l.getLoanType().equals(LoanType.LOCAL) && l.getStatus().equals(LoanStatus.INACTIVE))||
					(l.getLoanType().equals(LoanType.STATE) && l.getStatus().equals(LoanStatus.ACTIVE)) ||
					(l.getLoanType().equals(LoanType.STATE) && l.getStatus().equals(LoanStatus.INACTIVE)))
				throw new ApplicationException(
						 l.getLoanType().getCode() + " Loan for this customer already exist. Approve if inactive");
		}
	}

	public void isAmountPositive(LoanDTO txDTO) {

		if (txDTO.getAmount().doubleValue() <= 0) {
			throw new ApplicationException("Loan amount is invalid!");
		}
	}

	public void isLoanStartDateValid(LoanApprovalDTO approvalDto) {
		
		if(approvalDto.getLoanStartDate() == null) {
			throw new ApplicationException("Your Loan start date is not valid. Choose a date between now and a time in the future");
		}
		
		if(approvalDto.getLoanStartDate().isEqual(LocalDate.now()) || approvalDto.getLoanStartDate().isAfter(LocalDate.now())) {
			return;
		}
		throw new ApplicationException("Your Loan start date is not valid. Choose a date between now and a time in the future");
	}
}
