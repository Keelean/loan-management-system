package com.ffm.lms.customer.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface CustomerRepository extends CommonRepository<Customer> {

	Page<Customer> findByEmailOrFirstNameOrLastNameOrComputerNumber(String email, String firstName, String lastName,
			String computerNumber, Pageable pageable);
	
	Optional<Customer> findByComputerNumber(String computerNumber);

}
