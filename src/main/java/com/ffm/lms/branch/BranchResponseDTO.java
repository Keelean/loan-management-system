package com.ffm.lms.branch;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO.LoanDTOBuilder;
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
public class BranchResponseDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String code;
	private String oldCode;
}
