package com.ffm.lms.customer.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaritalStatus {

	SINGLE("Single", "Single"), MARRIED("Married", "Married Customer"), DIVORCED("Divorce", "Divorced");

	private String code;
	private String name;
}
