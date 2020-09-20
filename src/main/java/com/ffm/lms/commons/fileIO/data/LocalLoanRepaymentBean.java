package com.ffm.lms.commons.fileIO.data;

import java.math.BigDecimal;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocalLoanRepaymentBean {

	@Parsed(index = 0)
	private Long sn;
	@Parsed(index = 1)
	private String loanNo;
	@Parsed(index = 2)
	private String name;
	@Trim
	@Parsed(index = 3)
	private String computerNumber;
	@Parsed(index = 4)
	private String location;
	@Parsed(index = 5)
	private BigDecimal amount;
}
