package com.ffm.lms.customer.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.customer.commons.data.SearchableEntity;
import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.customer.domain.type.CustomerStatus;
import com.ffm.lms.customer.domain.type.Gender;
import com.ffm.lms.customer.domain.type.MaritalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Where(clause = "del_flag='N'")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table
public class Customer extends BaseEntity {

	@Column(name = "firstname")
	private String firstName;
	@Column(name = "lastname")
	private String lastName;
	@Column(name = "phone_number")
	private String phoneNumber;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private CustomerStatus status = CustomerStatus.UNVERIFIED;
	private String email;
	@Past
	private LocalDate dob;
	private String stateOfOrigin;
	private String lgaOfOrigin;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@NotNull
	@Column(name = "computer_number")
	private String computerNumber;
	private String nin;
	private String residentialAddress;
	private String homeTownAddress;
	@Enumerated(EnumType.STRING)
	private MaritalStatus maritalStatus;
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
	@NotNull
	private String branchCode;

}
