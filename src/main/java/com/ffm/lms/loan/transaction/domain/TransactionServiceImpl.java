package com.ffm.lms.loan.transaction.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.LoanService;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.transaction.domain.dto.CreateTransactionDTO;
import com.ffm.lms.loan.transaction.domain.dto.TransactionDTO;
import com.ffm.lms.loan.transaction.domain.type.TransactionSource;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;
import com.ffm.lms.wallet.domain.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl extends BaseService implements TransactionService {

	private final LoanService loanService;
	private final TransactionRepository repository;
	private final TransactionValidator validator;
	private final LoanRepository loanRepository;
	private final WalletService walletService;

	@Override
	public TransactionDTO repay(CreateTransactionDTO transaction) {

		TransactionDTO dto = mapper.map(transaction, TransactionDTO.class);
		validator.validate(dto);
		validator.isAmountPositive(dto);

		Loan loan = loanRepository.findById(transaction.getLoanId())
				.orElseThrow(() -> new ApplicationException("Inavlid Loan"));

		if (loan.getRemainingTenure() <= 0 && loan.getOutstanding().compareTo(BigDecimal.ZERO) == 0)
			throw new ApplicationException("Tenure is complete and Loan is fully settled.");

		if (loan.getRemainingTenure() <= 0)
			throw new ApplicationException("Tenure is complete and customer loan is not fully settled");

		if (!loan.isApproved())
			throw new ApplicationException("Loan have not been approved.");

		if (loan.getStatus() == LoanStatus.LIQUIDATED)

			// also check if loan have been fully settled
			if (loan.getRemainingTenure() == 0) {
				if (loan.getStatus().equals(LoanStatus.SETTLED))
					throw new ApplicationException("Tenure is complete and Loan is fully settled.");
				if (loan.getStatus().equals(LoanStatus.DEFAULTED))
					throw new ApplicationException("Tenure is complete and Loan is defaulted.");
			}

		// logger.info("Status: ?" + (loan.getStatus().equals(LoanStatus.ACTIVE)));
		if (!(loan.getStatus().equals(LoanStatus.ACTIVE) || (loan.getStatus().equals(LoanStatus.TOPUP))))
			throw new ApplicationException(
					"Transactions cannot be created for " + loan.getStatus().getCode() + " Loan");

		List<Transaction> loanTransactionLists = repository.findByLoanId(loan.getId());

		LocalDate repaymentValidDate = loan.getPaymentStartDate().plusMonths(loan.getDuration());
		LocalDate paidDate = transaction.getPaidDate();

		log.info("**************repaymentValidDate**************" + repaymentValidDate);
		log.info("**************getPaymentStartDate**************" + loan.getPaymentStartDate());
		log.info("**************paidDate**************" + paidDate);

		if (paidDate.isBefore(loan.getPaymentStartDate()) || paidDate.isAfter(repaymentValidDate)) {
			throw new ApplicationException("Paid date is not within the valid range of loan repayment date ("
					+ formatDate(loan.getPaymentStartDate()) + " - " + formatDate(repaymentValidDate) + ")");
		}

		boolean isWallet = transaction.getSource() == TransactionSource.WALLET;

		if (isWallet) {
			if (transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) > 0) {
				throw new ApplicationException("Amount cannot be greater than monthly loan repayment");
			}
		}

		// Since we are going to be using the payment months as a way to validate
		// repayments
		// We'll need to get the list of previous transactions
		if (Objects.isNull(loanTransactionLists) || loanTransactionLists.isEmpty()) {
			// If this list is empty then this is the first ever transaction for the
			// customer
			// if(loan.getPaymentStartDate().getMonth() != paidDate.getMonth()) {
			// throw new ApplicationException("Invalid loan repayment month: "+
			// loan.getPaymentStartDate().getMonth().name()+",
			// "+loan.getPaymentStartDate().getYear());
			// }
			// else {
			// Check amount
			// If amount is equal to repayment amount. If it is reduce tenure by 1
			if (transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) == 0) {
				loan.setRemainingTenure(loan.getRemainingTenure() - 1);
				BigDecimal outstanding = loan.getOutstanding().subtract(transaction.getAmount());
				outstanding = outstanding.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : outstanding;
				loan.setOutstanding(
						loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0
								? BigDecimal.ZERO
								: loan.getOutstanding().subtract(transaction.getAmount()));
				transaction.setStatus(TransactionStatus.PAID);
				log.info("----------------NOV0000------------");
			}
			// If amount is greater than repayment, move excess to wallet
			else if (transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) > 0) {
				BigDecimal txnAmount = transaction.getAmount();
				transaction.setStatus(TransactionStatus.OVERPAID);
				loan.setRemainingTenure(loan.getRemainingTenure() - 1);
				transaction.setAmount(loan.getMonthlyRepaymentAmount());
				loan.setOutstanding(loan.getOutstanding().subtract(loan.getMonthlyRepaymentAmount()));
				// Move excess to customer Wallet
				Double overflow = txnAmount.doubleValue() - loan.getMonthlyRepaymentAmount().doubleValue();
				log.info("----------------OV0055------------" + overflow);
				BigDecimal outstanding = loan.getOutstanding().subtract(BigDecimal.valueOf(overflow))
						.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO
								: loan.getOutstanding().subtract(BigDecimal.valueOf(overflow));
				loan.setOutstanding(outstanding);

				walletService.credit(loan.getCustomerId(),
						BigDecimal.valueOf(overflow).setScale(2, RoundingMode.HALF_UP));
				transaction.setCarryOverAmount(BigDecimal.valueOf(overflow).setScale(2, RoundingMode.HALF_UP));
			}
			// If amount is less than monthly repayment, leave tenure as is and set status
			// as UNDERPAID
			else {
				log.info("----------------NOO200------------");
				if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0) {
					transaction.setStatus(TransactionStatus.DEFAULTED);
					loan.setRemainingTenure(loan.getRemainingTenure() - 1);
				}
				else {
					transaction.setStatus(TransactionStatus.UNDERPAID);
				}
				loan.setOutstanding(
						loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0
								? BigDecimal.ZERO
								: loan.getOutstanding().subtract(transaction.getAmount()));
			}
			// }
		} else {
			// Check for previous transaction the nextPaymentDate
			List<Transaction> transactionsForMonth = getTransactionsByLoanIdAndPaidDate(loan.getId(), paidDate);

			LocalDate nextPayment = loan.getPaymentStartDate()
					.plusMonths(loan.getDuration() - loan.getRemainingTenure());
			/*
			 * if(nextPayment.getMonth() != paidDate.getMonth()) { throw new
			 * ApplicationException("Invalid loan repayment month: "+
			 * paidDate.getMonth().name()+", "+loan.getPaymentStartDate().getYear()
			 * +". Next repayment month should be "+nextPayment.getMonth()+
			 * ", "+nextPayment.getYear()); }
			 */

			// If transactions is not empty for the next payment date then this is likely
			// another payment for the month
			if (Objects.nonNull(transactionsForMonth) && !transactionsForMonth.isEmpty()) {

				BigDecimal totalPayForMonth = transactionsForMonth.stream()
						.collect(Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add));
				log.info("----------------totalPayForMonth------------$$: " + totalPayForMonth);
				log.info("----------------PAID DATE------------$$: " + paidDate);
				// If total transactions for this month is already settled increment tenure set
				// paidDate to next month
				if (totalPayForMonth.compareTo(loan.getMonthlyRepaymentAmount()) == 0) {
					// switch to next month
					// loan.setRemainingTenure(loan.getRemainingTenure() - 1);
					throw new ApplicationException("Loan have been fully settled for this month("
							+ transaction.getPaidDate().getMonth().name() + ")");
				} else if (totalPayForMonth.compareTo(loan.getMonthlyRepaymentAmount()) < 0) {
					if (totalPayForMonth.add(transaction.getAmount())
							.compareTo(loan.getMonthlyRepaymentAmount()) == 0) {
						transaction.setStatus(TransactionStatus.PAID);
						BigDecimal outstanding = loan.getOutstanding().subtract(transaction.getAmount())
								.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO
										: loan.getOutstanding().subtract(transaction.getAmount());
						loan.setOutstanding(outstanding);
						loan.setRemainingTenure(loan.getRemainingTenure() - 1);
						log.info("----------------NOV0011------------");
					} else if (totalPayForMonth.add(transaction.getAmount())
							.compareTo(loan.getMonthlyRepaymentAmount()) > 0) {
						// get difference and push excess to wallet
						// BigDecimal sumUp =
						// totalPayForMonth.add(transaction.getAmount()).subtract(loan.getMonthlyRepaymentAmount());
						BigDecimal balanceToSettleeForMonth = loan.getMonthlyRepaymentAmount()
								.subtract(totalPayForMonth);
						double sumUp = transaction.getAmount().doubleValue() - balanceToSettleeForMonth.doubleValue();
						transaction.setAmount(balanceToSettleeForMonth);
						transaction.setStatus(TransactionStatus.OVERPAID);
						loan.setRemainingTenure(loan.getRemainingTenure() - 1);
						BigDecimal outstanding = loan.getOutstanding().subtract(balanceToSettleeForMonth)
								.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO
										: loan.getOutstanding().subtract(balanceToSettleeForMonth);
						loan.setOutstanding(outstanding);
						log.info("----------------OV0044------------" + sumUp);
						walletService.credit(loan.getCustomerId(),
								BigDecimal.valueOf(sumUp).setScale(2, RoundingMode.HALF_UP));
						transaction.setCarryOverAmount(BigDecimal.valueOf(sumUp).setScale(2, RoundingMode.HALF_UP));
					}
					/*
					 * else if(totalPayForMonth.compareTo(BigDecimal.ZERO) == 0) {
					 * if(transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) == 0)
					 * { transaction.setStatus(TransactionStatus.PAID);
					 * loan.setRemainingTenure(loan.getRemainingTenure() - 1);
					 * log.info("----------------NOO2------------"); } else
					 * if((transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) < 0))
					 * { transaction.setStatus(TransactionStatus.UNDERPAID);
					 * log.info("----------------NOO1------------"); } else {
					 * log.info("----------------NOO3------------");
					 * transaction.setStatus(TransactionStatus.OVERPAID);
					 * loan.setRemainingTenure(loan.getRemainingTenure() - 1); double overflow =
					 * transaction.getAmount().doubleValue() -
					 * loan.getMonthlyRepaymentAmount().doubleValue();
					 * walletService.credit(loan.getCustomerId(),
					 * BigDecimal.valueOf(overflow).setScale(2, RoundingMode.HALF_UP)); } BigDecimal
					 * outstanding =
					 * loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.
					 * ZERO) < 0?
					 * BigDecimal.ZERO:loan.getOutstanding().subtract(transaction.getAmount());
					 * loan.setOutstanding(outstanding);
					 * log.info("----------------NOV0011111------------");
					 * log.info("----------------TOTAL4MONTH------------"+totalPayForMonth); }
					 */
					else {
						if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0) {
							transaction.setStatus(TransactionStatus.DEFAULTED);
							loan.setRemainingTenure(loan.getRemainingTenure() - 1);
						}
						else {
							transaction.setStatus(TransactionStatus.UNDERPAID);
						}
						loan.setOutstanding(
								loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0
										? BigDecimal.ZERO
										: loan.getOutstanding().subtract(transaction.getAmount()));
						log.info("----------------NOV0044U------------");
					}
				}
				/*
				 * else if(totalPayForMonth.compareTo(BigDecimal.ZERO) == 0) {
				 * if(transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) == 0)
				 * { transaction.setStatus(TransactionStatus.PAID);
				 * loan.setRemainingTenure(loan.getRemainingTenure() - 1);
				 * log.info("----------------NOO299------------"); } else
				 * if((transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) < 0))
				 * { transaction.setStatus(TransactionStatus.UNDERPAID);
				 * log.info("----------------NOO199------------"); } else {
				 * log.info("----------------NOO399------------");
				 * transaction.setStatus(TransactionStatus.OVERPAID);
				 * loan.setRemainingTenure(loan.getRemainingTenure() - 1); double overflow =
				 * transaction.getAmount().doubleValue() -
				 * loan.getMonthlyRepaymentAmount().doubleValue();
				 * walletService.credit(loan.getCustomerId(),
				 * BigDecimal.valueOf(overflow).setScale(2, RoundingMode.HALF_UP)); } BigDecimal
				 * outstanding =
				 * loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.
				 * ZERO) < 0?
				 * BigDecimal.ZERO:loan.getOutstanding().subtract(transaction.getAmount());
				 * loan.setOutstanding(outstanding);
				 * log.info("----------------NOV00999------------");
				 * log.info("----------------TOTAL4MONTH999------------"+totalPayForMonth);
				 * 
				 * }
				 */
			} else {
				if (transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) == 0) {
					transaction.setStatus(TransactionStatus.PAID);
					BigDecimal outstanding = loan.getOutstanding().subtract(transaction.getAmount())
							.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO
									: loan.getOutstanding().subtract(transaction.getAmount());
					loan.setOutstanding(outstanding);
					loan.setRemainingTenure(loan.getRemainingTenure() - 1);
					log.info("----------------NOV0022------------");
				} else if (transaction.getAmount().compareTo(loan.getMonthlyRepaymentAmount()) > 0) {
					transaction.setStatus(TransactionStatus.OVERPAID);
					Double overpaid = transaction.getAmount().doubleValue()
							- loan.getMonthlyRepaymentAmount().doubleValue();
					loan.setRemainingTenure(loan.getRemainingTenure() - 1);
					BigDecimal outstanding = loan.getOutstanding().subtract(loan.getMonthlyRepaymentAmount())
							.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO
									: loan.getOutstanding().subtract(loan.getMonthlyRepaymentAmount());
					loan.setOutstanding(outstanding);
					transaction.setAmount(loan.getMonthlyRepaymentAmount());
					transaction.setCarryOverAmount(BigDecimal.valueOf(overpaid).setScale(2, RoundingMode.HALF_UP));
					// deal with overflow. Push excess to customer's wallet
					walletService.credit(loan.getCustomerId(),BigDecimal.valueOf(overpaid).setScale(2, RoundingMode.HALF_UP));
					log.info("----------------NOO4------------");
				} else {
					// this is an under payment
					if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0) {
						transaction.setStatus(TransactionStatus.DEFAULTED);
						loan.setRemainingTenure(loan.getRemainingTenure() - 1);
					}
					else {
						transaction.setStatus(TransactionStatus.UNDERPAID);
					}

					loan.setOutstanding(loan.getOutstanding().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0? BigDecimal.ZERO
									: loan.getOutstanding().subtract(transaction.getAmount()));
					log.info("----------------NOV0033U------------");
				}
			}
		}

		if (loan.getOutstanding().doubleValue() <= 0) {
			logger.info("******Settled IF********" + loan.getOutstanding());
			loan.setStatus(LoanStatus.SETTLED);
			loan.setRemainingTenure(0);
		}

		if (isWallet) {
			walletService.debit(loan.getCustomerId(), transaction.getAmount());
		}
		loanRepository.save(loan);
		Transaction txn = mapper.map(transaction, Transaction.class);
		return mapper.map(repository.save(txn), TransactionDTO.class);

	}

	@Override
	public Page<TransactionDTO> findTransactionsById(Long loanId, Pageable pageable) {
		Page<Transaction> transactions = repository.findByLoanId(loanId, pageable);

		if (transactions == null || transactions.isEmpty())
			throw new ApplicationException("Empty record!");

		return transactions.map(transaction -> {
			TransactionDTO transactionDTO = mapper.map(transaction, TransactionDTO.class);
			return transactionDTO;
		});
	}

	@Override
	public List<TransactionDTO> findTransactionsByLoanIdList(Long loanId, Pageable pageable) {
		List<Transaction> transactions = repository.findByLoanIdOrderByPaidDateAsc(loanId);

		if (transactions == null || transactions.isEmpty())
			throw new ApplicationException("Empty record!");

		return transactions.stream().map(txn -> mapper.map(txn, TransactionDTO.class)).collect(Collectors.toList());
	}

	public TransactionDTO computeRepayment(Long loanId, String source) {
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ApplicationException("Invalid Loan!"));

		if (loan == null)
			throw new ApplicationException("Invalid Loan");

		if (!(loan.getStatus().equals(LoanStatus.ACTIVE) || (loan.getStatus().equals(LoanStatus.TOPUP))))
			throw new ApplicationException(
					"Transactions cannot be created for a(n) " + loan.getStatus().getCode() + " Loan");

		List<Transaction> txns = repository.findByLoanIdOrderByPaidDateAsc(loanId);

		TransactionDTO txnDTO = null;

		int nextMonth = loan.getDuration() - loan.getRemainingTenure();
		LocalDate nextPaymentDate = loan.getPaymentStartDate().plusMonths(nextMonth);
		BigDecimal txnAmount = loan.getOutstanding().compareTo(BigDecimal.ZERO) > 0
				&& loan.getOutstanding().compareTo(loan.getMonthlyRepaymentAmount()) < 0 ? loan.getOutstanding()
						: loan.getMonthlyRepaymentAmount();

		if (txns == null || txns.isEmpty()) {
			txnDTO = new TransactionDTO();
			txnDTO.setLoanId(loanId);
			txnDTO.setAmount(txnAmount);
			txnDTO.setPaidDate(nextPaymentDate);
			txnDTO.setSource(TransactionSource.valueOf(source));
			txnDTO.setOutstanding(loan.getOutstanding());
		} else {
			Transaction tx = txns.get(0);
			if (loan.getMonthlyRepaymentAmount().compareTo(tx.getAmount()) > 0) {
				txnDTO = new TransactionDTO();
				BigDecimal amount = loan.getMonthlyRepaymentAmount().subtract(tx.getAmount());
				txnDTO.setCarryOverAmount(loan.getMonthlyRepaymentAmount().add(amount));
				txnDTO.setAmount(txnAmount);
				txnDTO.setPaidDate(nextPaymentDate);
				txnDTO.setLoanId(loanId);
				txnDTO.setSource(TransactionSource.valueOf(source));
				txnDTO.setOutstanding(loan.getOutstanding());
			} else if (loan.getMonthlyRepaymentAmount().compareTo(tx.getAmount()) < 0) {
				BigDecimal diffAmount = tx.getAmount().subtract(loan.getMonthlyRepaymentAmount());
				txnDTO = new TransactionDTO();
				txnDTO.setAmount(loan.getMonthlyRepaymentAmount().subtract(diffAmount));
				txnDTO.setPaidDate(nextPaymentDate);
				txnDTO.setLoanId(loanId);
				txnDTO.setSource(TransactionSource.valueOf(source));
				txnDTO.setOutstanding(loan.getOutstanding());
			} else {
				txnDTO = new TransactionDTO();
				txnDTO.setAmount(txnAmount);
				txnDTO.setPaidDate(nextPaymentDate);
				txnDTO.setLoanId(loanId);
				txnDTO.setSource(TransactionSource.valueOf(source));
				txnDTO.setOutstanding(loan.getOutstanding());
			}
		}

		txnDTO.setWalletBalance(walletService.getBalance(loan.getCustomerId()));

		return txnDTO;
	}

	private List<Transaction> getTransactionsByLoanIdAndPaidDate(Long loanId, LocalDate paidDate) {
		// LocalDate startDate = paidDate.with(TemporalAdjusters.firstDayOfMonth());
		// LocalDate endDate = paidDate.with(TemporalAdjusters.lastDayOfMonth());
		LocalDate startDate = paidDate.withDayOfMonth(1);
		LocalDate endDate = paidDate.withDayOfMonth(paidDate.lengthOfMonth());
		return repository.findByStartAndEndPaidDateAndLoanId(startDate, endDate, loanId);
	}

}
