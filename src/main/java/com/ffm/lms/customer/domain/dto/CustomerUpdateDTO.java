package com.ffm.lms.customer.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateDTO implements Serializable{
	
	private static final long serialVersionUID = 1734555835698322743L;
	
	private Long id;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate dob;
	private String stateOfOrigin;
	private String lgaOfOrigin;
	private String gender;
	private String computerNumber;
	private String nin;
	private String residentialAddress;
	private String homeTownAddress;
	private String maritalStatus;
	private String nextOfKin;
	private String nextOfKinPhoneNumber;
	private String employer;
	private String employerAddress;
	private LocalDate dateOfEmployment;
	private String position;
	private String gradeLevel;
	private String department;
	private BigDecimal netMonthlySalary;
	private Integer expectedYearOfRetirement;
	private String state;
	@Lob
	private byte[] passport;
}
