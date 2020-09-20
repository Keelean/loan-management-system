package com.ffm.lms.customer.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

	MALE("Male", "Male customer"), FEMALE("Female", "Female Customer");

	private String code;
	private String name;
}
