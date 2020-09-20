package com.ffm.lms.loan.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoanType {

	STATE("State", "State Govt Loan"), LOCAL("Local", "Local Govt Loan"), PERSONAL("Personal", "Personal Loan");

	private String code;
	private String name;
}
