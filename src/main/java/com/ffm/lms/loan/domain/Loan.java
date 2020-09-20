package com.ffm.lms.loan.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Where(clause = "del_flag='N'")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table
@Access(AccessType.FIELD)
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Loan extends BaseEntity {
	
	@NotNull
	private Long customerId;
	@NotNull
	private BigDecimal amount;
	//@NotNull
	private BigDecimal outstanding;
	private String loanNo;
	@NotNull
	@Enumerated(EnumType.STRING)
	private LoanType loanType;
	@NotNull
	private Integer remainingTenure;
	//@NotNull
	@Enumerated(EnumType.STRING)
	private LoanStatus status;
	
	private String reference;
	@NotNull
	private Integer duration;
	private BigDecimal monthlyRepaymentAmount;
	@NotNull
	private BigDecimal interestRate;
	private BigDecimal processingFee;
	private boolean approved;
	private BigDecimal interestPaid;
	private BigDecimal totalRepaymentAmount;
	private LocalDate paymentStartDate;
	private BigDecimal liquidatedBalance;
	private BigDecimal chequeAmount;
	@NotNull
	private String branchCode;

}
