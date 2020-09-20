package com.ffm.lms.customer.domain;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
//@RunWith(SpringRunner.class)
public class CustomerRepositoryTest {

	@MockBean
	private CustomerRepository repository;
	
	//@Test
	public void findCustomerById() {
		//Mockito.verify(this.repository.findById(Mockito.anyLong())).Re
		//Mockito.g
	}
}
