package com.ffm.lms.customer.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffm.lms.customer.domain.type.CustomerStatus;

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
public class CustomerDTO implements Serializable {
	
	@NotNull(message = "first name cannot be empty")
	@NotEmpty(message = "first name cannot be empty")
	private String firstName;
	@NotNull(message = "last name cannot be empty")
	@NotEmpty(message = "last name cannot be empty")
	private String stateOfOrigin;
	private String lgaOfOrigin;
	private String gender;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String computerNumber;
	private LocalDate dob;
	private String state;
	
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
	@JsonIgnore
	private CustomerStatus status;
	private byte[] passport;
    private boolean regular;
    private boolean special;
    private boolean hasLoan;
    private String branchCode;
}
