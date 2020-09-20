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
public class StateLoanRepaymentBean {

	@Parsed(index = 0)
	private Long sn;
	@Parsed(index = 1)
	private String fileNumber;
	@Parsed(index = 2)
	private String biometricNumber;
	@Trim
	@Parsed(index = 3)
	private String computerNumber;
	@Parsed(index = 4)
	private String surname;
	@Parsed(index = 5)
	private String firstName;
	@Parsed(index = 6)
	private String otherName;
	@Parsed(index = 7)
	private String ministry;
	@Parsed(index = 8)
	private String grade;
	@Parsed(index = 9)
	private String step;
	@Parsed(index = 10)
	private String deduction;
	@Parsed(index = 11, defaultNullRead = "0")
	private BigDecimal amount;
}
