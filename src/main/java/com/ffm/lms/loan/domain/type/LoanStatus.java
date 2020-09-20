package com.ffm.lms.loan.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoanStatus {
	ACTIVE("Active", "Active Loan"), LIQUIDATED("Liquidated", "Liquidated Loan Account"),
	TOPUP("TopUp", "Top up account"), SETTLED("Settled", "Fully settled loan"),
	DEFAULTED("Defaulted", "Customer defaulted on loan"), INACTIVE("Inactive","Inactive Loan");

	private String code;
	private String name;
}
