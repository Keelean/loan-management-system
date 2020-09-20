package com.ffm.lms.customer.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ffm.lms.customer.domain.dto.CustomerDTO;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.customer.domain.dto.CustomerUpdateDTO;

public interface CustomerService {

	CustomerResponseDTO create(CustomerDTO customer);

	boolean delete(Long id);
	
	Page<CustomerResponseDTO> list(Pageable pageable);
	
	CustomerResponseDTO findById(Long id);
	
	CustomerResponseDTO modify(CustomerUpdateDTO customerDTO);
	
	boolean exist(Long customerId);
	
	Page<CustomerResponseDTO> filter(String email, String firstName, String lastName,
			String computerNumber, Pageable pageable);

}
