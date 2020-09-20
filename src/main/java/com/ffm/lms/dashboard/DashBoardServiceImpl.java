package com.ffm.lms.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.loan.domain.type.LoanStatus;
import com.ffm.lms.loan.transaction.domain.Transaction;
import com.ffm.lms.loan.transaction.domain.TransactionRepository;
import com.ffm.lms.loan.transaction.domain.type.TransactionStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DashBoardServiceImpl extends BaseService implements DashBoardService {

	private final TransactionRepository transactionRepository;
	private final LoanRepository loanRepository;

	public DashBoardContainer generateDashBoardDate() {

		List<LocalDate> localDates = new ArrayList<>();
		LocalDate startDate = LocalDate.now().withDayOfMonth(1);
		localDates.add(startDate);
		DashBoardContainer container = new DashBoardContainer();

		int i = 1;
		while (true) {
			LocalDate previousDate = startDate.minusMonths(i);
			localDates.add(previousDate);
			if (i == 11)
				break;
			i++;
		}

		List<DashBoard> totalInflows = getAllInflows(localDates);
		container.setTotalInflows(totalInflows);
		
		List<DashBoard> totalActiveLoans = getLoanCounts(localDates, LoanStatus.ACTIVE, LoanStatus.TOPUP);
		container.setTotalActiveLoans(totalActiveLoans);
		
		List<DashBoard> totalCompletedLoans = getLoanCounts(localDates, LoanStatus.SETTLED, LoanStatus.SETTLED);
		container.setTotalCompletedLoans(totalCompletedLoans);
		
		List<DashBoard> totalDefaultedLoans = getLoanCounts(localDates, LoanStatus.DEFAULTED, LoanStatus.DEFAULTED);
		container.setTotalDefaultedLaons(totalDefaultedLoans);
		
		List<DashBoard> totalPaid = getTransactionCounts(localDates, TransactionStatus.PAID);
		container.setTotalPaid(totalPaid);
		
		List<DashBoard> totalOverflow = getTransactionCounts(localDates, TransactionStatus.OVERPAID);
		container.setTotalOverflow(totalOverflow);
		
		List<DashBoard> totalUnderPaid = getTransactionCounts(localDates, TransactionStatus.UNDERPAID);
		container.setTotalUnderflow(totalUnderPaid);
		
		List<DashBoard> totalDefaulted = getTransactionCounts(localDates, TransactionStatus.DEFAULTED);
		container.setTotalDefaulted(totalDefaulted);

		return container;

	}

	public List<DashBoard> getAllInflows(List<LocalDate> localDates) {
		List<DashBoard> dashboards = new ArrayList<>();
		for (LocalDate date : localDates) {
			LocalDate start = date.withDayOfMonth(1);
			LocalDateTime startTime = start.atStartOfDay();
			LocalDate end = start.withDayOfMonth(date.lengthOfMonth());
			LocalDateTime endTime = end.atStartOfDay();
			BigDecimal total = transactionRepository.getSumOfMonthyInflows(startTime, endTime);
			String count = "0.0";
			if(Objects.isNull(total) || total.equals(BigDecimal.ZERO)) {
				count = "0.0";
			}
			else {	
				count = total.toString();
			}
			String year = String.valueOf(start.getYear());
			String month = start.getMonth().name();
			DashBoard dashboard = new DashBoard(count, month, year);
			dashboards.add(dashboard);
		}

		return dashboards;
	}
	
	public List<DashBoard> getLoanCounts(List<LocalDate> localDates, LoanStatus statusOne,LoanStatus statusTwo) {
		List<DashBoard> dashboards = new ArrayList<>();
		for (LocalDate date : localDates) {
			LocalDate start = date.withDayOfMonth(1);
			LocalDateTime startTime = start.atStartOfDay();
			LocalDate end = start.withDayOfMonth(date.lengthOfMonth());
			LocalDateTime endTime = end.atStartOfDay();
			Integer total = loanRepository.getLoanCountsByStatus(startTime, endTime, statusOne, statusTwo);
			String count = "0";
			if(Objects.isNull(total) || total == 0) {
				count = "0";
			}
			else {	
				count = total.toString();
			}
			String year = String.valueOf(start.getYear());
			String month = start.getMonth().name();
			DashBoard dashboard = new DashBoard(count, month, year);
			dashboards.add(dashboard);
		}

		return dashboards;
	}
	
	public List<DashBoard> getTransactionCounts(List<LocalDate> localDates, TransactionStatus status) {
		List<DashBoard> dashboards = new ArrayList<>();
		for (LocalDate date : localDates) {
			LocalDate start = date.withDayOfMonth(1);
			LocalDateTime startTime = start.atStartOfDay();
			LocalDate end = start.withDayOfMonth(date.lengthOfMonth());
			LocalDateTime endTime = end.atStartOfDay();
			Integer total = transactionRepository.getTransactionCountByStatus(startTime, endTime, status);
			String count = "0";
			if(Objects.isNull(total) || total == 0) {
				count = "0";
			}
			else {	
				count = total.toString();
			}
			String year = String.valueOf(start.getYear());
			String month = start.getMonth().name();
			DashBoard dashboard = new DashBoard(count, month, year);
			dashboards.add(dashboard);
		}

		return dashboards;
	}

}
