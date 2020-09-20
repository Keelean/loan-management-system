package com.ffm.lms.scheduler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ffm.lms.commons.fileIO.domain.FileUploadService;
import com.ffm.lms.commons.service.BaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomerDocumentProcessor extends BaseService {

	private final FileUploadService uploadService;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss");

	/*
	@Scheduled(fixedRate = 60000)
	public void processCustomerVerification() {
		logger.info("Customer Verification Job Started: " + formatter.format(LocalDateTime.now()));
		try {
			uploadService.verifyCustomerRecords();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Customer Verification Job Ended: " + formatter.format(LocalDateTime.now()));
	}*/

	@Scheduled(fixedRate = 120000)
	public void processCustomerLoanRepayment() {
		logger.info("Customer Loan Repayment Job Started: " + formatter.format(LocalDateTime.now()));
		uploadService.doLoanRepaymentBatch();
		logger.info("Customer Loan Repayment Job Ended: " + formatter.format(LocalDateTime.now()));
	}
}
