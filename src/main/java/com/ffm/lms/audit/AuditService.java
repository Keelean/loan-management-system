package com.ffm.lms.audit;

import java.util.Date;
import java.util.List;

import com.ffm.lms.customer.commons.domain.BaseEntity;
import com.ffm.lms.customer.domain.Customer;

public interface AuditService {

	AuditResponse getAllCustomers(AuditRequest auditRequest, String className);
}
