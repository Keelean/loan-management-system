package com.ffm.lms.branch;

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
public class BranchCreateDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String name;
	@NotNull
	private String code;
	private String oldCode;
}
