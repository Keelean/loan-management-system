package com.ffm.lms.loan.domain.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.ffm.lms.customer.domain.dto.CustomerUpdateDTO;
import com.ffm.lms.customer.domain.dto.CustomerUpdateDTO.CustomerUpdateDTOBuilder;
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
public class LoanUpdateDTO implements Serializable{
	
	@NotNull
	@NotEmpty
	private Long id;

	@NotNull
	@NotEmpty
	private LoanType loanType;
}
