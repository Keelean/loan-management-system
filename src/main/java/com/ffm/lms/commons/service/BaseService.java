package com.ffm.lms.commons.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BaseService {

	public Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	protected ModelMapper mapper;

	protected static Reader getReader(String relativePath) throws FileNotFoundException {
		try {
			return new InputStreamReader(new FileInputStream(relativePath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to read input", e);
		}
	}

	private static InputStream readFile(String filePath) throws IOException {
		Resource resource = new FileSystemResource(filePath);
		InputStream in = resource.getInputStream();
		return in;
	}

	protected static void createDirectory(String filePath) {
		File targetDir = new File(filePath);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
	}

	protected static PrintWriter getWriter(String path, String fileName) throws FileNotFoundException {
		createDirectory(path);
		String filePath = path + File.separator + fileName;
		log.info("*** FILE PATH: " + filePath);
		PrintWriter writer = new PrintWriter(new File(filePath));
		return writer;
	}

	protected User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		if (authentication == null)
			throw new ApplicationException("User is not logged in");

		User user = userRepository
				.findByUsernameOrEmailOrMobileNo(authentication.getName(), authentication.getName(),
						authentication.getName())
				.orElseThrow(() -> new ApplicationException("Please login to access this resource"));

		return user;
	}

	protected String generateLoanNumber(Loan loan) {
		String month = loan.getCreated().getMonth().toString().substring(0, 3);
		/*
		 * if(loan.getLoanType().equals(LoanType.PERSONAL)) {
		 * 
		 * } else if(loan.getLoanType().equals(LoanType.LOCAL)) {
		 * 
		 * } else {
		 * 
		 * }
		 */
		String stringedId = "";
		switch (loan.getId().toString().length()) {
		case 1:
			stringedId = "00" + loan.getId();
			break;
		case 2:
			stringedId = "0" + loan.getId();
		default:
			stringedId = loan.getId().toString();
		}

		return loan.getBranchCode() + "-" + stringedId + "-" + month;
	}

	protected String generateDefaultPassword() {
		String resetPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9);
		return resetPassword;
	}

	protected String formatNumbers(Double figure) {
		String pattern = "#,###.##";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		decimalFormat.setGroupingSize(3);
		String format = decimalFormat.format(figure);
		return format;
	}

	protected BigDecimal getLiquidatedBalance(Loan loan) {
		if (Objects.isNull(loan)) {
			throw new ApplicationException("Invalid loan!");
		}

		BigDecimal liquidatedBalance = BigDecimal.ZERO;

		if (Objects.nonNull(loan.getRemainingTenure()) && loan.getRemainingTenure() > 0) {
			BigDecimal balance = loan.getMonthlyRepaymentAmount()
					.multiply(BigDecimal.valueOf(loan.getRemainingTenure()));

			double interest = loan.getInterestPaid().doubleValue() / loan.getDuration().doubleValue();
			double calculatedIn = interest * loan.getRemainingTenure();
			liquidatedBalance = balance.subtract(BigDecimal.valueOf(calculatedIn)).setScale(2, RoundingMode.HALF_UP);
			loan.setLiquidatedBalance(liquidatedBalance);
		}

		return liquidatedBalance;
	}

	protected String formatDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
		return formatter.format(date);
	}

}
