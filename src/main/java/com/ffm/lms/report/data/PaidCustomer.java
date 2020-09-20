package com.ffm.lms.report.data;

import java.math.BigDecimal;

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
public class PaidCustomer {

	private String loanNo;
	private String customerName;
	private BigDecimal endBalance;
}
