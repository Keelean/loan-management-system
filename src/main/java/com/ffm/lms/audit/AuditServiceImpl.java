package com.ffm.lms.audit;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.customer.domain.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService{
	
	private final AuditRepository repository;

	public AuditResponse getAllCustomers(AuditRequest auditRequest, String className){
		
		
		Date startDate = Date.from(auditRequest.getStartDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(auditRequest.getEndDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		
		AuditResponse auditResponse = repository.getRevisions(auditRequest.getId(), startDate, endDate, className);
		
		if(auditResponse.getAuditObjects().isEmpty()) {
			throw new ApplicationException("No audit logs to show");
		}
		
		return auditResponse;
	}
}
