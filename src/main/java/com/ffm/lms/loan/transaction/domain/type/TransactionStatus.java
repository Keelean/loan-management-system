package com.ffm.lms.loan.transaction.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatus {

	PAID("Paid", "Fully paid"), DEFAULTED("Defaulted", "Defaulted repayment"),
	OVERPAID("Overpaid", "Repayment Overpaid"), UNDERPAID("Underpaid","Below monthly repayment");

	private String code;
	private String name;
}
