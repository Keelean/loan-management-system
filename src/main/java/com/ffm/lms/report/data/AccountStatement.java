package com.ffm.lms.report.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountStatement {

	private LocalDate paidDate;
	private String documentNumber;
	private String narration;
	private String time;
	private BigDecimal debit = BigDecimal.ZERO;
	private BigDecimal credit = BigDecimal.ZERO;
	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal totalDebit = BigDecimal.ZERO;
	private BigDecimal totalCredit = BigDecimal.ZERO;
	private BigDecimal totalBalance = BigDecimal.ZERO;
}
