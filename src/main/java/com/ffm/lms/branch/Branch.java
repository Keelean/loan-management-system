package com.ffm.lms.branch;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.customer.commons.domain.BaseEntity;

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
public class Branch extends BaseEntity {

	@NotNull
	private String name;
	@NotNull
	@Column(unique = true)
	private String code;
	private String oldCode;
}
