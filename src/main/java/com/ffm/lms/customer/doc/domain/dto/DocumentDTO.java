package com.ffm.lms.customer.doc.domain.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

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
public class DocumentDTO implements Serializable {

	private Long id;

	@NotNull
	private Long customerId;
	//@NotNull
	private String type;
	private String location;
	@NotNull
	private byte[] file;
	@NotNull
	private String fileName;
	private String url;
}
