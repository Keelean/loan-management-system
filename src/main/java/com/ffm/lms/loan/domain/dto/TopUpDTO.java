package com.ffm.lms.loan.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.ffm.lms.loan.domain.dto.LoanDTO.LoanDTOBuilder;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;

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
public class TopUpDTO implements Serializable {

	private Long id;

	@NotNull
	private Long customerId;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private LoanType loanType;
	@NotNull
	private Integer duration;
	
	private BigDecimal interestRate;
}
