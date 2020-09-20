package com.ffm.lms.loan.transaction.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.loan.transaction.domain.type.TransactionSource;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Transaction extends BaseEntity {

	@NotNull
	private BigDecimal amount;
	@NotNull
	private Long loanId;
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;
	@NotNull
	private LocalDate paidDate;
	@Enumerated(EnumType.STRING)
	private TransactionSource source;

}
