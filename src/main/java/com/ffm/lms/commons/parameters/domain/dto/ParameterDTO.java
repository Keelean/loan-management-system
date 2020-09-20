package com.ffm.lms.commons.parameters.domain.dto;

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
public class ParameterDTO implements Serializable {

	private Long id;

	@NotNull
	private String name;
	@NotNull
	private String value;

}
