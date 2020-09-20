package com.ffm.lms.loan.transaction.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO.LoanDTOBuilder;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.loan.transaction.domain.type.TransactionSource;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;

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
public class TransactionDTO implements Serializable {

	private Long id;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Long loanId;
	private TransactionStatus status;
	@NotNull
	private LocalDate paidDate;
	private TransactionSource source;
	private BigDecimal carryOverAmount;
    private LocalDateTime created;
    private LocalDateTime updated;
    private BigDecimal walletBalance;
    private BigDecimal outstanding;
}
