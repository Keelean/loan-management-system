package com.ffm.lms.user.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffm.lms.customer.domain.type.ApprovedStatus;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO.LoanDTOBuilder;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.user.domain.types.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
public class UserDTO implements Serializable{
	
	private Long id;
	
	private String firstname;
	private String lastname;

	private String username;
	private String middleName;
	private String name;

	private String email;
	
	@JsonIgnore
	private String password;
	private String mobileNo;
	
	private String role;
    private LocalDateTime created;
    
    private LocalDateTime updated;

}
