package com.ffm.lms.loan.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.loan.domain.dto.LoanApprovalDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanUpdateDTO;

public interface LoanService {

	LoanResponseDTO create(LoanDTO loanDTO);

	Loan modify(LoanUpdateDTO loan);

	boolean delete(Long loanId);

	Page<LoanResponseDTO> page(Long customerId, Pageable pageable);
	
	List<LoanResponseDTO> list(Long customerId);
	
	LoanResponseDTO findById(Long id);
	
	LoanResponseDTO topUp(LoanDTO loanDTO);
	
	LoanResponseDTO calculateLoan(LoanDTO loanDTO);
	
	LoanResponseDTO modify(Loan loan);
	
	LoanResponseDTO getLoanById(Long id);
	
	List<LoanResponseDTO> loansByCustomer(Long customerId);
	
	BigDecimal calculateProcessingFee(BigDecimal loanAmount);
	
	LoanResponseDTO approve(Long loanId);
	
	Page<LoanResponseDTO> list(Pageable pageable);
	
	LoanResponseDTO approve(Long loanId, LoanApprovalDTO approvalDto);
	
	LoanResponseDTO liquidate(Long loanId);
	
}
