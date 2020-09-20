package com.ffm.lms;


import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.LoanService;
import com.ffm.lms.loan.domain.LoanValidator;
import com.ffm.lms.loan.domain.dto.LoanDTO;

//@RunWith(MockitoJUnitRunner.class)
class FfmLoanAppApplicationTests {

	@Mock
	private LoanRepository repository;
	@Mock
	private LoanValidator validator;
	@Mock
	private CustomerService customerService;
	@Mock
	private ParameterService parameterService;
	
	@Mock
	public ModelMapper mapper;

	@InjectMocks
	private LoanService loanService;

	@Test
	void createLoanTest() {
		
		LoanDTO loan = new LoanDTO();
		//loan.setActualAmount(new BigDecimal(2000));
		loan.setDuration(7);
		//loan.setLoanNo("000000");
		loan.setCustomerId(23L);
		
		//given(validator.isAmountPositive(loan));
	}

}
