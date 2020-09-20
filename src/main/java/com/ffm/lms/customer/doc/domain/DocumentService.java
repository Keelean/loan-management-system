package com.ffm.lms.customer.doc.domain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.ffm.lms.customer.doc.domain.dto.DocumentDTO;

public interface DocumentService {

	DocumentDTO upload(DocumentDTO document) throws FileNotFoundException, IOException;

	List<DocumentDTO> getCustomerDocuments(Long customerId);

	List<DocumentDTO> getDocumentByType(Long customerId, String documentType);
	
	String upload(List<DocumentDTO> document);
	
	boolean delete(Long documentId);
}
