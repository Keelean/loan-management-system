package com.ffm.lms.loan.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.ffm.lms.customer.domain.dto.CustomerDTO;
import com.ffm.lms.customer.domain.dto.CustomerDTO.CustomerDTOBuilder;
import com.ffm.lms.customer.domain.type.ApprovedStatus;
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
public class LoanDTO implements Serializable {

	@NotNull
	private Long customerId;
	@NotNull
	private BigDecimal amount;

	private LoanType loanType;

	@NotNull
	private Integer duration;
	@NotNull
	private BigDecimal interestRate;


}
