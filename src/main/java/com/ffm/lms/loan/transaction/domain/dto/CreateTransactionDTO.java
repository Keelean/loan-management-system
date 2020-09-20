package com.ffm.lms.loan.transaction.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.ffm.lms.loan.transaction.domain.dto.TransactionDTO.TransactionDTOBuilder;
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
public class CreateTransactionDTO implements Serializable {
	
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Long loanId;
	private TransactionStatus status;
	@NotNull
	private LocalDate paidDate;
	private TransactionSource source;
	private BigDecimal carryOverAmount;
}
