package com.ffm.lms.loan.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;
import com.ffm.lms.loan.domain.type.LoanStatus;

@Repository
public interface LoanRepository extends CommonRepository<Loan>{
	
	List<Loan> findByCustomerId(Long customerId);
	
	@Query(value = "select l from Loan l where l.status in ('ACTIVE','INACTIVE') and l.customerId = :customerId")
	List<Loan> findAllActiveLoans(@Param("customerId") Long customerId);
	
	@Query(value = "select l from Loan l where l.customerId = :customerId")
	Page<Loan> findByCustomerPageable(@Param("customerId") Long customerId, Pageable pageable);
	
	@Query(value = "select l from Loan l order by l.created desc")
	Page<Loan> findAllOrderByCreatedDate(Pageable pageable);
	
	@Query(value = "select count(l) from Loan l where l.updated between :startDate and :endDate and (l.status = :statusOne or l.status = :statusTwo)")
	Integer getLoanCountsByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("statusOne") LoanStatus statusOne, @Param("statusTwo") LoanStatus statusTwo);
	
}
