package com.ffm.lms.loan.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.ffm.lms.customer.domain.type.ApprovedStatus;
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
public class LoanResponseDTO implements Serializable {

	private Long id;

	@NotNull
	private Long customerId;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private BigDecimal outstanding;
	private String loanNo;
	@NotNull
	private LoanType loanType;
	@NotNull
	private Integer remainingTenure;
	// @NotNull
	private LoanStatus status;
	@NotNull
	private Integer duration;
	@NotNull
	private BigDecimal interestRate;
	private BigDecimal monthlyRepaymentAmount;
	private BigDecimal processingFee;
	private BigDecimal totalRepaymentAmount;
	private boolean approved;
	private BigDecimal interestPaid;
	private BigDecimal liquidatedBalance;
	private BigDecimal chequeAmount;
	private String branchCode;

	/*
	 * public BigDecimal getTotalRepaymentAmount() { if (duration > 0 &&
	 * processingFee != null && monthlyRepaymentAmount != null) {
	 * totalRepaymentAmount =
	 * monthlyRepaymentAmount.multiply(BigDecimal.valueOf(duration)).add(
	 * processingFee); return totalRepaymentAmount; } return BigDecimal.ZERO; }
	 */

	// repayment.multiply(BigDecimal.valueOf(duration)).add(loanDTO.getProcessingFee())
	/*
	 * public BigDecimal getInterestPaid() { if (processingFee != null &&
	 * totalRepaymentAmount != null) { interestPaid =
	 * totalRepaymentAmount.subtract(amount); return interestPaid; } return
	 * BigDecimal.ZERO; }
	 */
}
