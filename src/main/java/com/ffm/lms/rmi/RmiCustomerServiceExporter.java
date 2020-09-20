package com.ffm.lms.rmi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.ffm.lms.customer.domain.CustomerService;

@Configuration
public class RmiCustomerServiceExporter {

	@Bean
	public RmiServiceExporter rmiExporter(CustomerService customerService) {
		RmiServiceExporter rmiExporter = new RmiServiceExporter();
		rmiExporter.setService(customerService);
		rmiExporter.setServiceName("CustomerService");
		rmiExporter.setServiceInterface(CustomerService.class);
		return rmiExporter;
	}

}
