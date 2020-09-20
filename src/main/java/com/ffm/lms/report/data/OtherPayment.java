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
public class OtherPayment {

	private String loanNo;
	private String customerName;
	private BigDecimal expected;
	private BigDecimal paid;
	private BigDecimal difference;
}
