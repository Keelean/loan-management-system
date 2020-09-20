package com.ffm.lms.user.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.customer.domain.type.ApprovedStatus;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.user.domain.types.DefaultCode;
import com.ffm.lms.user.domain.types.Role;

import lombok.AllArgsConstructor;
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
@Access(AccessType.FIELD)
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class User extends BaseEntity {

	@NotNull
	private String firstname;
	@NotNull
	private String lastname;
	@NotNull
	@Column(unique = true)
	private String username;
	private String middleName;
	private String name;

	@NotNull
	@Column(unique = true)
	private String email;
	
	@JsonIgnore
	private String password;
	@Column(unique = true)
	@NotNull
	private String mobileNo;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private Role role;
	@Enumerated(EnumType.STRING)
	private DefaultCode defaultCode;
	

	public String getName() {
		if (firstname != null && lastname != null)
			name = this.firstname + " " + this.lastname;
		return name;
	}
}
