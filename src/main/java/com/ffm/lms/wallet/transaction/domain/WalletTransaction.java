package com.ffm.lms.wallet.transaction.domain;

import java.io.Serializable;
import java.math.BigDecimal;

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
import com.ffm.lms.wallet.transaction.domain.types.WalletTransactionTypes;

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
public class WalletTransaction extends BaseEntity {

	@NotNull
	private BigDecimal amount;
	@NotNull
	@Enumerated(EnumType.STRING)
	private WalletTransactionTypes type;
	private String description;
	@NotNull
	private Long customerId;
}
