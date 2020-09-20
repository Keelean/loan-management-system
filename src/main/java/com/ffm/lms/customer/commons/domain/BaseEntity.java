package com.ffm.lms.customer.commons.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ffm.lms.customer.commons.data.DeleteFlag;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
/*
 * @JsonIgnoreProperties( value = {"created", "updated"}, allowGetters = true )
 */
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", initialValue = 1, allocationSize = 1)
	protected Long id;

	protected String delFlag = DeleteFlag.NOT_DELETED;

	@Version
	private Integer version;

	@Column(insertable = true, updatable = false)
	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime updated;

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String updatedBy;

	@Transient
	private String userAction;

}
