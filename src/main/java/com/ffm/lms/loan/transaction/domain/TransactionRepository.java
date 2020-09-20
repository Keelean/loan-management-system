package com.ffm.lms.loan.transaction.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;

@Repository
public interface TransactionRepository extends CommonRepository<Transaction>{
	
	Page<Transaction> findByLoanId(Long loanId, Pageable pageable);
	List<Transaction> findByLoanIdOrderByPaidDateAsc(Long loanId);
	List<Transaction> findByLoanId(Long loanId);
	List<Transaction> findByPaidDateAndLoanIdOrderByPaidDateAsc(LocalDate paidDate, Long loanId);
	@Query(value = "select t from Transaction t where t.loanId = :loanId and t.paidDate between :startDate and :endDate")
	List<Transaction> findByStartAndEndPaidDateAndLoanId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("loanId") Long loanId);
	
	List<Transaction> findByPaidDateBetweenAndLoanId(LocalDate paidDateStartDate, LocalDate paidDateEndDate, Long loanId);
	List<Transaction> findByPaidDateBetweenAndStatus(LocalDate paidDateStartDate, LocalDate paidDateEndDate, TransactionStatus status);
	List<Transaction> findByPaidDateAndStatus(LocalDate paidDateStartDate, TransactionStatus status);
	List<Transaction> findByPaidDateBetween(LocalDate startDate, LocalDate endDate);
	
	@Query(value = "select SUM(t.amount) from Transaction t where t.created between :startDate and :endDate")
	BigDecimal getSumOfMonthyInflows(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	
	@Query(value = "select count(t) from Transaction t where t.created between :startDate and :endDate and t.status = :status")
	Integer getTransactionCountByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("status") TransactionStatus status);

}
