package com.ffm.lms.customer.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.commons.data.DeleteFlag;
import com.ffm.lms.customer.domain.dto.CustomerDTO;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.customer.domain.dto.CustomerUpdateDTO;
import com.ffm.lms.customer.domain.type.CustomerStatus;
import com.ffm.lms.customer.domain.type.Gender;
import com.ffm.lms.customer.domain.type.MaritalStatus;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.LoanService;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;
import com.ffm.lms.wallet.domain.Wallet;
import com.ffm.lms.wallet.domain.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl extends BaseService implements CustomerService {

	private final CustomerRepository repository;
	private final CustomerValidator validator;
	private final LoanRepository loanRepository;
	private final WalletService walletService;

	@Override
	public CustomerResponseDTO create(CustomerDTO customer) throws ApplicationException {

		validator.validate(customer);
		LocalDate dob = customer.getDob();
		LocalDate doe = customer.getDateOfEmployment();
		if(Objects.nonNull(dob) && Objects.nonNull(customer.getDateOfEmployment())) {
			int retirementDateDob = dob.getYear() + 60;
			int retirementDateDoe = doe.getYear() + 35;
			if(retirementDateDob > retirementDateDoe) {
				customer.setExpectedYearOfRetirement(retirementDateDoe);
			}
			else {
				customer.setExpectedYearOfRetirement(retirementDateDob);
			}
			
		}

		Customer c = mapper.map(customer, Customer.class);
		CustomerResponseDTO dto = mapper.map(repository.save(c), CustomerResponseDTO.class);
		Wallet wallet = walletService.initializeCustomerWallet(dto.getId());
		dto.setWalletBalance(wallet.getAmount());
		return dto;
	}

	@Override
	public boolean delete(Long id) {
		Customer customer = repository.findById(id)
				.orElseThrow(() -> new ApplicationException("Customer does not exist"));
		customer.setDelFlag(DeleteFlag.DELETED);
		return repository.softDelete(customer);
	}

	@Override
	public Page<CustomerResponseDTO> list(Pageable pageable) {

		Page<Customer> customers = repository.findAll(pageable);
		if (customers == null || customers.isEmpty())
			throw new ApplicationException("Empty Record!");

		return customers.map(ops -> {
			CustomerResponseDTO dto = mapper.map(ops, CustomerResponseDTO.class);
			Long customerId = dto.getId();
			dto.setWalletBalance(walletService.getBalance(customerId));
			boolean hasLoan = hasTakenLoan(customerId);
			dto.setHasLoan(hasLoan);
			List<String> loans = getLoanTypes(customerId);
			if (loans.size() > 0) {
				if (loans.contains(Constants.REGULAR)) {
					dto.setRegular(true);
				} else {
					dto.setRegular(false);
				}
				if (loans.contains(Constants.SPECIAL)) {
					dto.setSpecial(true);
				} else {
					dto.setSpecial(false);
				}
			}
			return dto;
		});
	}

	@Override
	public CustomerResponseDTO findById(Long id) {

		Customer customer = repository.findById(id)
				.orElseThrow(() -> new ApplicationException("Customer does not exist"));
		
		CustomerResponseDTO dto = mapper.map(customer, CustomerResponseDTO.class);
		dto.setWalletBalance(walletService.getBalance(id));
		
		return dto;

	}

	@Override
	public CustomerResponseDTO modify(CustomerUpdateDTO customerDTO) throws ApplicationException {
		Customer customer = repository.findById(customerDTO.getId())
				.orElseThrow(() -> new ApplicationException("Customer does not exist"));

		mapper.map(customerDTO, customer);

		if (Objects.nonNull(customerDTO.getGender())) {
			customer.setGender(Enum.valueOf(Gender.class, customerDTO.getGender()));
		}
		if (Objects.nonNull(customerDTO.getMaritalStatus())) {
			customer.setMaritalStatus(Enum.valueOf(MaritalStatus.class, customerDTO.getMaritalStatus()));
		}
		LocalDate dob = customer.getDob();
		LocalDate doe = customerDTO.getDateOfEmployment();
		if(Objects.nonNull(dob) && Objects.nonNull(customer.getDateOfEmployment())) {
			int retirementDateDob = dob.getYear() + 60;
			int retirementDateDoe = doe.getYear() + 35;
			if(retirementDateDob > retirementDateDoe) {
				customer.setExpectedYearOfRetirement(retirementDateDoe);
			}
			else {
				customer.setExpectedYearOfRetirement(retirementDateDob);
			}
			
		}
		
		customer = repository.save(customer);
		CustomerResponseDTO dto = mapper.map(customer, CustomerResponseDTO.class);
		dto.setWalletBalance(walletService.getBalance(customer.getId()));
		return dto;
	}

	@Override
	public boolean exist(Long customerId) {
		return repository.findById(customerId).isPresent();

	}

	@Override
	public Page<CustomerResponseDTO> filter(String email, String firstName, String lastName, String computerNumber,
			Pageable pageable) {
		Page<Customer> customers = repository.findByEmailOrFirstNameOrLastNameOrComputerNumber(email, firstName,
				lastName, computerNumber, pageable);

		if (Objects.isNull(customers) || customers.isEmpty())
			throw new ApplicationException("No records!");

		return customers.map(customer -> {
			CustomerResponseDTO customerDTO = mapper.map(customer, CustomerResponseDTO.class);
			customerDTO.setWalletBalance(walletService.getBalance(customerDTO.getId()));
			return customerDTO;
		});
	}

	private List<String> getLoanTypes(Long customerId) {
		List<Loan> loans = loanRepository.findByCustomerId(customerId);
		List<String> loanTypes = Arrays.asList("");
		if (Objects.isNull(loans) || loans.isEmpty())
			return loanTypes;

		loanTypes = loans.stream()
				.filter(loan -> loan.getStatus().equals(LoanStatus.ACTIVE) || loan.getStatus().equals(LoanStatus.TOPUP) || loan.getStatus().equals(LoanStatus.INACTIVE))
				.map(l -> getLoanType(l.getLoanType())).distinct().collect(Collectors.toList());
		return loanTypes;
	}

	private boolean hasTakenLoan(Long customerId) {
		List<Loan> loans = loanRepository.findByCustomerId(customerId);
		if (loans == null || loans.size() < 1)
			return false;
		return true;
	}

	private String getLoanType(LoanType loanType) {
		if (loanType.equals(LoanType.LOCAL) || loanType.equals(LoanType.STATE))
			return Constants.REGULAR;
		else
			return Constants.SPECIAL;
	}

}
