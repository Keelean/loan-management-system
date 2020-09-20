package com.ffm.lms.customer.domain.bean;

import com.univocity.parsers.annotations.Parsed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Headers(sequence = {"id", "status","email","phoneNumber"}, extract = true, write = true)
public class CustomerBean {

	@Parsed(field = "id")
	private Long id;
	@Parsed(field = "status")
	private String status;
	@Parsed(field = "email")
	private String email;
	@Parsed(field = "phoneNumber")
	private String phoneNumber;
}
