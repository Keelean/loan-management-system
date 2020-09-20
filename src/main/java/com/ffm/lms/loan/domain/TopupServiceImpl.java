package com.ffm.lms.loan.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanResponseDTO;
import com.ffm.lms.loan.domain.dto.TopUpDTO;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.transaction.domain.Transaction;
import com.ffm.lms.loan.transaction.domain.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopupServiceImpl extends BaseService implements TopupService {

	private final LoanService loanService;
	private final LoanRepository repository;
	private final TransactionRepository transactionRepository;


	@Override
	public LoanResponseDTO topUp(TopUpDTO loanDTO) {

		if(loanDTO.getAmount() == null || loanDTO.getAmount().doubleValue() < 0)
			throw new ApplicationException("Invalid top up value.");
		
		Loan loan = repository.findById(loanDTO.getId()).orElseThrow(() -> new ApplicationException("Inavlid Loan"));
		BigDecimal amount = BigDecimal.ZERO;

		if (loan == null)
			throw new ApplicationException("Invalid Loan");

		if (!(loan.getStatus().equals(LoanStatus.ACTIVE) || loan.getStatus().equals(LoanStatus.TOPUP)))
			throw new ApplicationException("You can only top up an active account not a " + loan.getStatus().getCode()+ " account");

		/*
		List<Transaction> transactionListOfExistingLoan = transactionRepository.findByLoanId(loan.getId());
		if(transactionListOfExistingLoan != null && !transactionListOfExistingLoan.isEmpty()) {
			BigDecimal sumofExistingLoansTransactions = transactionListOfExistingLoan.stream().collect(Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount,BigDecimal::add));
			double balanceOfExistingLoan = loan.getTotalRepaymentAmount().doubleValue() - sumofExistingLoansTransactions.doubleValue();
			amount = BigDecimal.valueOf(balanceOfExistingLoan);
		}*/
		
		amount = getLiquidatedBalance(loan);
	
		amount = amount.add(loanDTO.getAmount());

		loan.setStatus(LoanStatus.LIQUIDATED);
		repository.save(loan);
		String oldReference = loan.getLoanNo();
		//loanDTO.setId(null);

		LoanDTO ldto = new LoanDTO();
		ldto.setAmount(amount);
		ldto.setDuration(loanDTO.getDuration());
		ldto.setInterestRate(loanDTO.getInterestRate());
		LoanResponseDTO lrdto =  loanService.calculateLoan(ldto);
		Loan topUp = new Loan();
		topUp.setReference(oldReference);
		topUp.setId(0L);
		topUp.setAmount(amount);
		topUp.setProcessingFee(lrdto.getProcessingFee());
		topUp.setChequeAmount(loanDTO.getAmount().subtract(lrdto.getProcessingFee()));
		topUp.setStatus(LoanStatus.TOPUP);
		topUp.setApproved(false);
		topUp.setLoanType(loan.getLoanType());
		topUp.setCustomerId(loan.getCustomerId());
		topUp.setInterestRate(loanDTO.getInterestRate());
		topUp.setDuration(loanDTO.getDuration());
		topUp.setRemainingTenure(loanDTO.getDuration());
		topUp.setMonthlyRepaymentAmount(lrdto.getMonthlyRepaymentAmount());
		topUp.setTotalRepaymentAmount(lrdto.getTotalRepaymentAmount());
		topUp.setInterestPaid(topUp.getTotalRepaymentAmount().subtract(amount));
		topUp.setOutstanding(topUp.getTotalRepaymentAmount());
		topUp = repository.save(topUp);
		topUp.setLoanNo(generateLoanNumber(topUp));
		topUp = repository.save(topUp);
		return mapper.map(topUp, LoanResponseDTO.class);
	}

}
