package com.ffm.lms.loan.domain;

import static com.ffm.lms.commons.constants.Constants.FORM_FEE;
import static com.ffm.lms.commons.constants.Constants.VAT;
import static com.ffm.lms.commons.constants.Constants.PROCESSING_RATE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.commons.data.DeleteFlag;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanApprovalDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanUpdateDTO;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.domain.type.LoanType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoanServiceImpl extends BaseService implements LoanService {

	private final LoanRepository repository;
	private final LoanValidator validator;
	private final CustomerService customerService;
	private final ParameterService parameterService;

	@Override
	public LoanResponseDTO create(LoanDTO loanDTO) {
		validator.isCustomerValid(loanDTO);
		validator.validate(loanDTO);
		validator.isAmountPositive(loanDTO);
		// Validate customer if he has existing Loan
		validator.isEligibleForLoan(loanDTO);
		// create customer Walletcu

		// Calculate Repayment Amount

		LoanResponseDTO loanResponseDTO = mapper.map(loanDTO, LoanResponseDTO.class);

		String branchCode = customerService.findById(loanDTO.getCustomerId()).getBranchCode();
		
		log.info(String.format("The loan branch code is: %s", branchCode));
		
		loanResponseDTO.setBranchCode(branchCode);
		log.info(String.format("The loan branch code22 is: %s", loanResponseDTO.getBranchCode()));
		loanResponseDTO = calculateLoan(loanDTO);
		loanResponseDTO.setStatus(LoanStatus.INACTIVE);
		loanResponseDTO.setApproved(false);
		loanResponseDTO.setTotalRepaymentAmount(loanResponseDTO.getTotalRepaymentAmount());
		loanResponseDTO.setOutstanding(loanResponseDTO.getTotalRepaymentAmount());
		loanResponseDTO.setInterestPaid(loanResponseDTO.getTotalRepaymentAmount().subtract(loanDTO.getAmount()));
		Loan loan = mapper.map(loanResponseDTO, Loan.class);
		loan.setBranchCode(branchCode);
		loan = repository.save(loan);
		loanResponseDTO = mapper.map(loan, LoanResponseDTO.class);
		loanResponseDTO.setTotalRepaymentAmount(loanResponseDTO.getMonthlyRepaymentAmount());
		return loanResponseDTO;
	}

	@Override
	public LoanResponseDTO calculateLoan(LoanDTO loan) {

		LoanResponseDTO loanDTO = mapper.map(loan, LoanResponseDTO.class);

		if (loanDTO.getInterestRate().equals(null) || loanDTO.getAmount().equals(null)
				|| loanDTO.getDuration().equals(null))
			throw new ApplicationException("Invalid Data!");

		BigDecimal processingFee = calculateProcessingFee(loanDTO.getAmount());

		loanDTO.setProcessingFee(processingFee);
		
		
		//loanDTO.setTotalRepaymentAmount(loanDTO.getAmount().add(loanDTO.getProcessingFee()));

		Double interestRate = loanDTO.getInterestRate().doubleValue() / 100;

		int duration = loanDTO.getDuration() == null || loanDTO.getDuration() == 0 ? 12 : loanDTO.getDuration();

		//Double Numerator = loanDTO.getTotalRepaymentAmount().doubleValue() * interestRate * Math.pow(interestRate + 1, duration);
		
		BigDecimal monthlyPayment = BigDecimal.valueOf(((((duration * interestRate) * loanDTO.getAmount().doubleValue())  + loanDTO.getAmount().doubleValue())/duration)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(duration)).setScale(2, RoundingMode.HALF_UP);

		//Double Denominator = Math.pow(interestRate + 1, duration) - 1;

		//BigDecimal repayment = new BigDecimal(Numerator / Denominator).setScale(2, RoundingMode.HALF_UP);

		// loanDTO.setMonthlyRepaymentAmount(repayment);
		loanDTO.setMonthlyRepaymentAmount(monthlyPayment);

		loanDTO.setAmount(loanDTO.getAmount());
		loanDTO.setRemainingTenure(duration);
		loanDTO.setTotalRepaymentAmount(totalPayment);
		loanDTO.setChequeAmount(loanDTO.getAmount().subtract(processingFee));

		/*loanDTO.setTotalRepaymentAmount(
				repayment.multiply(BigDecimal.valueOf(duration)).add(loanDTO.getProcessingFee()));*/
		return loanDTO;
	}

	@Override
	public Loan modify(LoanUpdateDTO loanDTO) {

		Loan loan = repository.findById(loanDTO.getId()).orElseThrow(() -> new ApplicationException("Inavlid Loan"));

		// validator.isCustomerValid(loan);
		// validator.validate(loan);

		loan.setLoanType(loanDTO.getLoanType());

		return repository.save(loan);

	}

	@Override
	public boolean delete(Long loanId) {
		// Check if account is active or is a top up

		Loan loan = repository.findById(loanId).orElseThrow(() -> new ApplicationException("Invalid Loan"));

		if (loan.getStatus().equals(LoanStatus.ACTIVE) || loan.getStatus().equals(LoanStatus.TOPUP) || loan.getStatus().equals(LoanStatus.SETTLED)
				|| loan.getStatus().equals(LoanStatus.LIQUIDATED)) {
			throw new ApplicationException(String.format("You cannot delete an %s loan", loan.getStatus().name()));
		}

		// delete loan and all corresponding transactions
		loan.setDelFlag(DeleteFlag.DELETED);
		return repository.softDelete(loan);

	}

	@Override
	public Page<LoanResponseDTO> page(Long customerId, Pageable pageable) {

		Page<Loan> loans = repository.findByCustomerPageable(customerId, pageable);

		if (loans == null || loans.isEmpty())
			throw new ApplicationException("Empty record");

		return loans.map(loan -> {
			LoanResponseDTO loanDTO = mapper.map(loan, LoanResponseDTO.class);
			return loanDTO;
		});

	}

	@Override
	public List<LoanResponseDTO> list(Long customerId) {

		System.out.println("CUSTOMER ID: " + customerId);

		List<Loan> loans = repository.findByCustomerId(customerId);

		if (loans == null || loans.isEmpty())
			throw new ApplicationException("Empty record");

		return loans.stream().map(loan -> mapper.map(loan, LoanResponseDTO.class)).collect(Collectors.toList());

	}

	@Override
	public LoanResponseDTO findById(Long id) {
		Loan loan = repository.findById(id).orElseThrow(() -> new ApplicationException("Loan does not exist"));
		return mapper.map(loan, LoanResponseDTO.class);
	}

	public LoanResponseDTO getLoanById(Long id) {
		Loan loan = repository.findById(id).orElseThrow(() -> new ApplicationException("Loan does not exist"));
		return mapper.map(loan, LoanResponseDTO.class);
	}

	@Override
	public LoanResponseDTO topUp(LoanDTO loanDTO) {
		return null;
	}

	@Override
	public LoanResponseDTO modify(Loan loan) {
		Loan existingLoan = repository.findById(loan.getId())
				.orElseThrow(() -> new ApplicationException("Loan does not exist"));

		if (existingLoan.getStatus().equals(LoanStatus.ACTIVE)) {
			throw new ApplicationException("You cannot modify an active loan");
		}

		existingLoan.setRemainingTenure(loan.getRemainingTenure());
		existingLoan.setOutstanding(loan.getOutstanding());
		return mapper.map(repository.save(loan), LoanResponseDTO.class);
	}

	@Override
	public List<LoanResponseDTO> loansByCustomer(Long customerId) {
		CustomerResponseDTO customer = customerService.findById(customerId);
		if (customer == null)
			throw new ApplicationException("Customer does not exist");

		List<Loan> loans = repository.findByCustomerId(customerId);

		return loans.stream().map(loan -> mapper.map(loan, LoanResponseDTO.class)).collect(Collectors.toList());
	}

	@Override
	public BigDecimal calculateProcessingFee(BigDecimal loanAmount) {
		if (loanAmount == null || loanAmount.equals(BigDecimal.ZERO))
			return BigDecimal.ZERO;

		// Double amount = loanAmount.doubleValue();

		// try {
		ParameterDTO paramProcessingRate = parameterService.getParameterByName(PROCESSING_RATE);
		Double processingRate = Double.parseDouble(paramProcessingRate.getValue()) / 100;

		ParameterDTO paramVatFee = parameterService.getParameterByName(VAT);
		Double vatFee = Double.parseDouble(paramVatFee.getValue()) / 100;

		ParameterDTO paramFormFee = parameterService.getParameterByName(FORM_FEE);

		Double formFeeValue = Double.parseDouble(paramFormFee.getValue());

		Double formFee = (formFeeValue * (vatFee)) + formFeeValue;

		BigDecimal proFee = loanAmount.multiply(BigDecimal.valueOf(processingRate));

		BigDecimal processingFee = proFee.multiply(BigDecimal.valueOf(vatFee));

		return processingFee.add(proFee).add(BigDecimal.valueOf(formFee)).setScale(2, RoundingMode.HALF_UP);
		// } catch (Exception eexception) {
		// throw new ApplicationException("Invalid Fee Value!");
		// }

	}

	@Override
	public LoanResponseDTO approve(Long loanId) {

		Loan loan = repository.findById(loanId).orElseThrow(() -> new ApplicationException("Loan does not exist"));
		
		LoanDTO loanDTO = mapper.map(loan, LoanDTO.class);

		validator.isCustomerValid(loanDTO);

		LocalDate paymentStartDate = loan.getCreated().plusMonths(1).toLocalDate();
		loan.setPaymentStartDate(paymentStartDate);
		loan.setApproved(true);
		loan.setStatus(loan.getStatus() == LoanStatus.TOPUP ? loan.getStatus(): LoanStatus.ACTIVE);
		loan.setLoanNo(generateLoanNumber(loan));
		return mapper.map(repository.save(loan), LoanResponseDTO.class);

	}

	public LoanResponseDTO approve(Long loanId, LoanApprovalDTO approvalDto) {
		Loan loan = repository.findById(loanId).orElseThrow(() -> new ApplicationException("Loan does not exist"));

		LoanDTO loanDTO = mapper.map(loan, LoanDTO.class);

		validator.isCustomerValid(loanDTO);

		validator.isLoanStartDateValid(approvalDto);

		loan.setPaymentStartDate(approvalDto.getLoanStartDate());
		loan.setApproved(true);
		loan.setStatus(loan.getStatus() == LoanStatus.TOPUP ? loan.getStatus(): LoanStatus.ACTIVE);
		loan.setLoanNo(generateLoanNumber(loan));
		return mapper.map(repository.save(loan), LoanResponseDTO.class);
	}

	public Page<LoanResponseDTO> list(Pageable pageable) {

		Page<Loan> loans = repository.findAll(pageable);

		if (loans == null || loans.isEmpty())
			throw new ApplicationException("Empty record");

		return loans.map(loan -> {
			LoanResponseDTO loanDTO = mapper.map(loan, LoanResponseDTO.class);
			return loanDTO;
		});

	}

	public LoanResponseDTO liquidate(Long loanId) {
		Loan loan = repository.findById(loanId).orElseThrow(() -> new ApplicationException("Loan does not exist"));
		if (loan.getStatus() == LoanStatus.INACTIVE || loan.getStatus() == LoanStatus.SETTLED
				|| loan.getStatus() == LoanStatus.LIQUIDATED)
			throw new ApplicationException("You cannot liquidate a " + loan.getStatus() + " loan");
		
		BigDecimal liquidatedbalance = getLiquidatedBalance(loan);

		loan.setLiquidatedBalance(liquidatedbalance);
		loan.setStatus(LoanStatus.LIQUIDATED);
		return mapper.map(repository.save(loan), LoanResponseDTO.class);

	}
	


}
