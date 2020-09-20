package com.ffm.lms.commons.fileIO.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileUploadType {

	LOAN_REPAYMENT("Repayment", "For Processing Loan Repayent"), CUSTOMER_VERIFICATION("Customer","Verify Customer");

	private String code;
	private String name;
}
