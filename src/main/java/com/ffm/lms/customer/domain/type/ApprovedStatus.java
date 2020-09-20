package com.ffm.lms.customer.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovedStatus {
	
	YES("Yes", "Approved"), NO("No", "Not Approved");

	private String code;
	private String name;

}
