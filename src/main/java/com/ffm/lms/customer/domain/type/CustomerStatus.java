package com.ffm.lms.customer.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerStatus {
	VERIFIED("VER", "Verified Customer"), UNVERIFIED("UNV", "Unverified Customer");

	private String code;
	private String name;

}
