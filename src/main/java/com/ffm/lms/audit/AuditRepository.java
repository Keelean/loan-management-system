package com.ffm.lms.audit;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ffm.lms.commons.parameters.domain.Parameter;
import com.ffm.lms.commons.parameters.domain.ParameterRepository;
import com.ffm.lms.customer.domain.Customer;
import com.ffm.lms.customer.domain.CustomerRepository;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.loan.domain.Loan;
import com.ffm.lms.loan.domain.LoanRepository;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserRepository;
import com.ffm.lms.wallet.domain.Wallet;
import com.ffm.lms.wallet.domain.WalletRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditRepository {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private CustomerRepository customerRepository;
	private UserRepository userRepository;
	private WalletRepository walletRepository;
	private LoanRepository loanRepository;
	private ParameterRepository parameterRepository;

	public AuditResponse getRevisions(Long id, Date startDate, Date endDate, String className) {
		AuditQuery query = null;
		AuditResponse auditResponses = new AuditResponse();
		switch (className) {
		case "customer":
			query = executeAuditQuery(id, Customer.class);
			List<Customer> customers = getCustomerLogs(query, startDate, endDate, id);
			auditResponses.getAuditObjects().addAll(customers);
			break;
		case "wallet":
			query = executeAuditQuery(id, Wallet.class);
			List<Wallet> wallets = getWalletLogs(query, startDate, endDate, id);
			auditResponses.getAuditObjects().addAll(wallets);
			break;
		case "parameter":
			query = executeAuditQuery(id, Parameter.class);
			List<Parameter> parameters = getParameterLogs(query, startDate, endDate, id);
			auditResponses.getAuditObjects().addAll(parameters);
			break;
		case "loan":
			query = executeAuditQuery(id, Parameter.class);
			List<Loan> loans = getLoanLogs(query, startDate, endDate, id);
			auditResponses.getAuditObjects().addAll(loans);
			break;
		case "user":
			query = executeAuditQuery(id, Parameter.class);
			List<User> users = getUserLogs(query, startDate, endDate, id);
			auditResponses.getAuditObjects().addAll(users);
			break;
		}

		return auditResponses;
	}

	private AuditQuery executeAuditQuery(Long id, Class clazz) {
		AuditReader auditReader = AuditReaderFactory.get(em);

		AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, false, true);
		query.add(AuditEntity.id().eq(id)).addOrder(AuditEntity.revisionNumber().desc());
		return query;
	}

	private List<Customer> getCustomerLogs(AuditQuery query, Date startDate, Date endDate, Long id) {
		List<Customer> customers = new ArrayList<>();
		List<Object[]> resultLists = query.getResultList();
		Customer customer = customerRepository.findById(id).get();
		resultLists.forEach(objects -> {
			Customer c = (Customer) objects[0];
			AuditRevisionEntity auditRevision = (AuditRevisionEntity) objects[1];
			RevisionType revisionType = (RevisionType) objects[2];
			if (auditRevision.getRevisionDate().compareTo(startDate) >= 0
					|| auditRevision.getRevisionDate().compareTo(endDate) <= 0) {
				c.setCreated(customer.getCreated());
				c.setCreatedBy(customer.getCreatedBy());
				c.setUpdated(
						auditRevision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				c.setVersion(auditRevision.getId());
				c.setUpdatedBy(auditRevision.getUser());
				c.setUserAction(getUserAction(revisionType));
				customers.add((Customer) objects[0]);
			}
		});
		return customers;
	}

	private List<Wallet> getWalletLogs(AuditQuery query, Date startDate, Date endDate, Long id) {
		List<Wallet> wallets = new ArrayList<>();
		List<Object[]> resultLists = query.getResultList();
		Wallet wallet = walletRepository.findById(id).get();
		resultLists.forEach(objects -> {
			Wallet w = (Wallet) objects[0];
			AuditRevisionEntity auditRevision = (AuditRevisionEntity) objects[1];
			RevisionType revisionType = (RevisionType) objects[2];
			if (auditRevision.getRevisionDate().compareTo(startDate) >= 0
					|| auditRevision.getRevisionDate().compareTo(endDate) <= 0) {
				w.setCreated(wallet.getCreated());
				w.setCreatedBy(wallet.getCreatedBy());
				w.setUpdated(
						auditRevision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				w.setVersion(auditRevision.getId());
				w.setUpdatedBy(auditRevision.getUser());
				w.setUserAction(getUserAction(revisionType));
				wallets.add((Wallet) objects[0]);
			}
		});
		return wallets;
	}

	private List<User> getUserLogs(AuditQuery query, Date startDate, Date endDate, Long id) {
		List<User> users = new ArrayList<>();
		List<Object[]> resultLists = query.getResultList();
		User user = userRepository.findById(id).get();
		resultLists.forEach(objects -> {
			User u = (User) objects[0];
			AuditRevisionEntity auditRevision = (AuditRevisionEntity) objects[1];
			RevisionType revisionType = (RevisionType) objects[2];
			if (auditRevision.getRevisionDate().compareTo(startDate) >= 0
					|| auditRevision.getRevisionDate().compareTo(endDate) <= 0) {

				u.setCreated(user.getCreated());
				u.setCreatedBy(user.getCreatedBy());
				u.setUpdated(
						auditRevision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				u.setVersion(auditRevision.getId());
				u.setUpdatedBy(auditRevision.getUser());
				u.setUserAction(getUserAction(revisionType));
				users.add((User) objects[0]);
			}
		});
		return users;
	}

	private List<Loan> getLoanLogs(AuditQuery query, Date startDate, Date endDate, Long id) {
		List<Loan> loans = new ArrayList<>();
		List<Object[]> resultLists = query.getResultList();
		Loan loan = loanRepository.findById(id).get();
		resultLists.forEach(objects -> {
			User u = (User) objects[0];
			AuditRevisionEntity auditRevision = (AuditRevisionEntity) objects[1];
			RevisionType revisionType = (RevisionType) objects[2];
			if (auditRevision.getRevisionDate().compareTo(startDate) >= 0
					|| auditRevision.getRevisionDate().compareTo(endDate) <= 0) {
					u.setCreated(loan.getCreated());
					u.setCreatedBy(loan.getCreatedBy());
				u.setUpdated(
						auditRevision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				u.setVersion(auditRevision.getId());
				u.setUpdatedBy(auditRevision.getUser());
				u.setUserAction(getUserAction(revisionType));
				loans.add((Loan) objects[0]);
			}
		});
		return loans;
	}

	private List<Parameter> getParameterLogs(AuditQuery query, Date startDate, Date endDate, Long id) {
		List<Parameter> parameters = new ArrayList<>();
		List<Object[]> resultLists = query.getResultList();
		AuditReader auditReader = AuditReaderFactory.get(em);
		Parameter parameter = parameterRepository.findById(id).get();
		resultLists.forEach(objects -> {

			Parameter p = (Parameter) objects[0];
			AuditRevisionEntity auditRevision = (AuditRevisionEntity) objects[1];
			RevisionType revisionType = (RevisionType) objects[2];
			if (auditRevision.getRevisionDate().compareTo(startDate) >= 0
					|| auditRevision.getRevisionDate().compareTo(endDate) <= 0) {
				p.setCreated(parameter.getCreated());
				p.setCreatedBy(parameter.getCreatedBy());
				p.setUpdated(
						auditRevision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				p.setVersion(auditRevision.getId());
				p.setUpdatedBy(auditRevision.getUser());
				p.setUserAction(getUserAction(revisionType));
				parameters.add((Parameter) objects[0]);
			}
		});
		return parameters;
	}

	private String getUserAction(RevisionType revType) {
		String userAction = null;
		switch (revType) {
		case ADD:
			userAction = "CREATED";
			break;
		case DEL:
			userAction = "DELETED";
			break;
		case MOD:
			userAction = "UPDATED";
			break;
		default:
			userAction = "USER ACTION UNKNOWN";
			break;
		}
		return userAction;
	}
}
