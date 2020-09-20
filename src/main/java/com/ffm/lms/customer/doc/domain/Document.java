package com.ffm.lms.customer.doc.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.ffm.lms.customer.commons.data.SearchableEntity;
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
public class Document extends BaseEntity implements SearchableEntity {

	@NotNull
	private Long customerId;
	//@NotNull
	//@Column(unique = true)
	private String type;
	private String location;
	@Transient
	private byte[] file;
	@NotNull
	private String fileName;
	private String url;

	@Override
	public List<String> getDefaultSearchFields() {
		return Arrays.asList("");
	}

}
