package com.ffm.lms.loan.transaction.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.loan.transaction.domain.dto.CreateTransactionDTO;
import com.ffm.lms.loan.transaction.domain.dto.TransactionDTO;

public interface TransactionService {

	TransactionDTO repay(CreateTransactionDTO transaction);
	
	Page<TransactionDTO> findTransactionsById(Long loanId, Pageable pageable);
	
	TransactionDTO computeRepayment(Long loanId, String source);
	
	List<TransactionDTO> findTransactionsByLoanIdList(Long loanId, Pageable pageable);
}
