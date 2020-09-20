package com.ffm.lms.report.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.Customer;
import com.ffm.lms.customer.domain.CustomerRepository;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.transaction.domain.Transaction;
import com.ffm.lms.loan.transaction.domain.TransactionRepository;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;
import com.ffm.lms.report.data.AccountStatement;
import com.ffm.lms.report.data.OtherPayment;
import com.ffm.lms.report.data.PaidCustomer;
import com.ffm.lms.report.data.ReportParam;
import com.ffm.lms.report.generator.ReportPdfGenerator;
import com.ffm.lms.report.template.ThymeleafReportTemplate;
import com.ffm.lms.report.template.types.TemplateName;
import com.lowagie.text.DocumentException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

	private final ReportPdfGenerator pdfGenerator;
	private final ThymeleafReportTemplate reportTemplate;
	private final TransactionRepository transactionRepository;
	private final LoanRepository loanRepository;
	private final CustomerRepository customerRepository;

	@Override
	public String generateReport(ReportParam reportParam) {
		String reportUrl = "";

		List<OtherPayment> otherPayments = new ArrayList<>();
		switch (reportParam.getReportType()) {
		case ACCOUNT_STATEMENT:
			List<AccountStatement> accountStatements = processCustomerAccountStatement(reportParam);
			reportUrl = buildReport(accountStatements, TemplateName.ACCOUNT_STATEMENT);
			accountStatements.clear();
			break;
		case SHORT_PAID_FOR_PERIOD:
			otherPayments = otherCustomers(reportParam, TransactionStatus.UNDERPAID);
			reportUrl = buildReport(otherPayments, TemplateName.SHORT_PAID_FOR_PERIOD);
			otherPayments.clear();
			break;
		case PAID_UP_CUSTOMER:
			List<PaidCustomer> paidCustomers = paidUpCustomers(reportParam);
			reportUrl = buildReport(paidCustomers, TemplateName.PAID_UP_CUSTOMER);
			paidCustomers.clear();
			break;
		case OVER_PAYMENT_FOR_PERIOD:
			otherPayments = otherCustomers(reportParam, TransactionStatus.OVERPAID);
			reportUrl = buildReport(otherPayments, TemplateName.OVER_PAYMENT_FOR_PERIOD);
			otherPayments.clear();
			break;
		case LEDGER:
			throw new ApplicationException("Ledger Report Not Implemented");
		case DEFAULTERS_FOR_PERIOD:
			otherPayments = otherCustomers(reportParam, TransactionStatus.DEFAULTED);
			reportUrl = buildReport(otherPayments, TemplateName.DEFAULTERS_FOR_PERIOD);
			otherPayments.clear();
			break;
		case ACCOUNT_STATUS:
			throw new ApplicationException("Account Status Report No Implemented");
		// break;
		default:
			throw new ApplicationException("Invalid Report Type");
		}

		return reportUrl;
	}

	private <T> String buildReport(List<T> data, TemplateName name) {
		String reportUrl = "";
		String html = reportTemplate.parseThymeleafTemplate(name.getName(), data);
		try {
			reportUrl = pdfGenerator.generatePdfFromHtml(html, name.getName());
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportUrl;
	}

	private List<AccountStatement> processCustomerAccountStatement(ReportParam reportParam) {
		Customer customer = customerRepository.findById(reportParam.getCustomerId())
				.orElseThrow(() -> new ApplicationException("Customer does not exist"));
		List<Loan> loans = loanRepository.findByCustomerId(reportParam.getCustomerId()).stream()
				.filter(Loan::isApproved).collect(Collectors.toList());
		List<Transaction> aggregateTransactions = new ArrayList<>();
		if (Objects.nonNull(loans) && !loans.isEmpty()) {
			for (int i = 0; i < loans.size(); i++) {
				List<Transaction> transactions = transactionRepository.findByPaidDateBetweenAndLoanId(
						reportParam.getStartDate(), reportParam.getEndDate(), loans.get(i).getId());
				aggregateTransactions = addTransaction(transactions, aggregateTransactions);
			}
		} else {
			throw new ApplicationException("Customer does not have transactions");
		}

		List<AccountStatement> accountStatements = generateCustomerAccountStatement(aggregateTransactions);
		if (Objects.isNull(accountStatements) || accountStatements.isEmpty()) {
			throw new ApplicationException("Customer does not have transactions");
		}
		return accountStatements;
	}

	List<Transaction> addTransaction(List<Transaction> transactions, List<Transaction> aggregateTransactions) {
		aggregateTransactions.addAll(transactions);
		return aggregateTransactions;
	}

	List<AccountStatement> generateCustomerAccountStatement(List<Transaction> transactions) {
		List<AccountStatement> statements = new ArrayList<>();
		if (Objects.nonNull(transactions) && !transactions.isEmpty()) {
			Map<Integer, AccountStatement> map = new HashMap<>();
			for (int i = 0; i < transactions.size(); i++) {
				AccountStatement statement = null;
				if (i == 0) {
					Loan loan = loanRepository.findById(transactions.get(i).getLoanId()).get();
					statement = new AccountStatement();
					statement.setDebit(loan.getTotalRepaymentAmount());
					statement.setBalance(loan.getTotalRepaymentAmount());
					statement.setPaidDate(transactions.get(i).getPaidDate());
					statements.add(statement);
				} else if (transactions.get(i).getLoanId().longValue() != transactions.get(i - 1).getLoanId()
						.longValue()) {
					log.info("*****PREV LOAN*****:" + transactions.get(i - 1).getLoanId() + "*******CURRENT******:"
							+ transactions.get(i).getLoanId());
					AccountStatement zerorize = new AccountStatement();
					zerorize.setBalance(statements.get(i - 1).getBalance().subtract(statements.get(i - 1).getCredit()));
					zerorize.setPaidDate(statements.get(i - 1).getPaidDate());
					map.put(i, zerorize);
					Loan loan = loanRepository.findById(transactions.get(i).getLoanId()).get();
					statement = new AccountStatement();
					statement.setDebit(loan.getTotalRepaymentAmount());
					statement.setBalance(loan.getTotalRepaymentAmount().add(zerorize.getBalance()));
					statement.setPaidDate(transactions.get(i).getPaidDate());
					statements.add(statement);
				} else {
					AccountStatement prevState = statements.get(i - 1);
					statement = new AccountStatement();
					statement.setBalance(prevState.getBalance().subtract(transactions.get(i).getAmount()));
					statement.setPaidDate(transactions.get(i).getPaidDate());
					statement.setCredit(transactions.get(i).getAmount());
					statements.add(statement);
				}
			}
			statements.get(statements.size() - 1).setTotalBalance(
					statements.stream().map(AccountStatement::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add));
			statements.get(statements.size() - 1).setTotalCredit(
					statements.stream().map(AccountStatement::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add));
			statements.get(statements.size() - 1).setTotalDebit(
					statements.stream().map(AccountStatement::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add));

			if (!map.isEmpty())
				map.forEach((k, v) -> statements.add(k, v));
			map.clear();
			AccountStatement zerorize = new AccountStatement();
			zerorize.setCredit(BigDecimal.ZERO);
			zerorize.setDebit(BigDecimal.ZERO);
			zerorize.setBalance(BigDecimal.ZERO);
			zerorize.setPaidDate(statements.get(statements.size() - 1).getPaidDate());
			statements.add(zerorize);

		}
		return statements;
	}

	private List<PaidCustomer> paidUpCustomers(ReportParam reportParam) {
		List<PaidCustomer> paidCustomers = new ArrayList<>();
		List<Transaction> transactions = getTransactionsForTheMonth(reportParam.getStartDate(), TransactionStatus.PAID);
		if (Objects.isNull(transactions) || transactions.isEmpty()) {
			throw new ApplicationException(
					"Transaction not available for month(" + reportParam.getStartDate().getMonth().name() + ")");
		}
		for (Transaction t : transactions) {
			PaidCustomer paidCustomer = null;
			log.info("---PAID LOAN ID: "+t.getLoanId());
			Loan loan = loanRepository.findById(t.getLoanId()).orElse(null);
			if(loan == null)
				continue;
			String loanNumber = loan.getLoanNo();
			Customer customer = customerRepository.findById(loan.getCustomerId()).orElse(null);
			if(customer == null)
				continue;
			String customerName = customer.getFirstName() + " " + customer.getLastName();
			paidCustomer = new PaidCustomer();
			paidCustomer.setCustomerName(customerName);
			paidCustomer.setLoanNo(loanNumber);
			paidCustomer.setEndBalance(loan.getMonthlyRepaymentAmount().subtract(t.getAmount()));
			paidCustomers.add(paidCustomer);
		}
		return paidCustomers;
	}

	private List<OtherPayment> otherCustomers(ReportParam reportParam, TransactionStatus status) {
		List<Transaction> transactions = getTransactionsForTheMonth(reportParam.getStartDate(), status);
		if (Objects.isNull(transactions) || transactions.isEmpty()) {
			throw new ApplicationException(
					"Transaction not available for month(" + reportParam.getStartDate().getMonth().name() + ")");
		}
		List<OtherPayment> otherPayments = new ArrayList<>();
		for (Transaction t : transactions) {
			OtherPayment otherPayment = null;
			log.info("---OTHER LOAN ID: "+t.getLoanId());
			Loan loan = loanRepository.findById(t.getLoanId()).orElse(null);
			if(loan == null)
				continue;
			String loanNumber = loan.getLoanNo();
			Customer customer = customerRepository.findById(loan.getCustomerId()).orElse(null);
			if(customer == null)
				continue;
			String customerName = customer.getFirstName() + " " + customer.getLastName();
			otherPayment = new OtherPayment();
			otherPayment.setCustomerName(customerName);
			otherPayment.setLoanNo(loanNumber);
			otherPayment.setExpected(loan.getMonthlyRepaymentAmount());
			otherPayment.setPaid(t.getAmount());
			otherPayment.setDifference(loan.getMonthlyRepaymentAmount().subtract(t.getAmount()));
			otherPayments.add(otherPayment);
		}
		return otherPayments;

	}

	private List<Transaction> getTransactionsForTheMonth(LocalDate date, TransactionStatus status) {
		LocalDate startDate = date.withDayOfMonth(1);
		LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
		List<Transaction> transactions = transactionRepository.findByPaidDateBetweenAndStatus(startDate, endDate,
				status);
		return transactions;
	}
}
