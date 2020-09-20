package com.ffm.lms.customer.doc.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface DocumentRepository extends CommonRepository<Document> {

	List<Document> findByType(String type);

	List<Document> findByCustomerId(Long customerId);

	List<Document> findByCustomerIdAndType(Long customerId, String type);
}
