package com.ffm.lms.report.template.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateName {

	ACCOUNT_STATEMENT("account_statement", "Customer Account Statement"), 
	ACCOUNT_STATUS("account_status", "Customer Account Status"),
	OVER_PAYMENT_FOR_PERIOD("over_payment_for_period", "Customer Account Status"),
	PAID_UP_CUSTOMER("paid_up_customer", "Customer Account Status"),
	SHORT_PAID_FOR_PERIOD("short_payment_for_period", "Customer Account Status"),
	LEDGER("ledger", "Ledger"),
	DEFAULTERS_FOR_PERIOD("defaulters_for_period", "Defaulters");

	private String name;
	private String description;
}
