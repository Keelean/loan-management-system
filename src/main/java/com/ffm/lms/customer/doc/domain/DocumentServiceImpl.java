package com.ffm.lms.customer.doc.domain;

import static com.ffm.lms.commons.constants.Constants.MAX_DOC_UPLOAD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.doc.domain.dto.DocumentDTO;
import com.ffm.lms.customer.domain.CustomerService;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl extends BaseService implements DocumentService {
	
	private final ParameterService parameterService;
	
	@Value("${file.upload.path}")
	private String filePath;

	@Value("${file.upload.url.protocol}")
	private String protocol;

	@Value("${file.upload.customer.doc}")
	private String documentPath;

	@Value("${file.upload.path.url}")
	private String filePathUrl;
	
	@Value("${file.upload.report.path}")
	private String fileReportPath;

	private final CustomerService customerService;
	private final DocumentValidator validator;
	private final DocumentRepository repository;
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public DocumentDTO upload(DocumentDTO documentDTO) throws FileNotFoundException, IOException {

		validator.validate(documentDTO);

		Document document = generateFileInfo(documentDTO);

		document.setFile(null);
		
		log.debug("DocumentString: " + document);

		return mapper.map(repository.save(document), DocumentDTO.class);

	}

	private Document generateFileInfo(DocumentDTO document) throws FileNotFoundException, IOException {

		CustomerResponseDTO customer = customerService.findById(document.getCustomerId());
		String timeStamp = dateFormatter.format(new Date()).replaceAll(":", "_");
		String fileName = timeStamp.replaceAll("\\s+", "TS") + "_"+ document.getFileName().trim().replaceAll("\\s+", "_");
		String type = Objects.nonNull(document.getType())? document.getType().trim().replaceAll("\\s+", "_").toLowerCase():"";

		if (customer == null)
			throw new ApplicationException("Invalid Customer!");

		//String path = documentPath + File.separator + customer.getId() + File.separator + type + File.separator + fileName;
		String path = filePath + File.separator + customer.getId() + "_" + type + "_" + fileName;

		createDirectory(filePath);// + File.separator + customer.getId() + File.separator + type);

		try (FileOutputStream stream = new FileOutputStream(path)) {
			stream.write(document.getFile());
		}

		document.setLocation(path);
		document.setFileName(fileName);
		document.setType(type);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(filePathUrl)
				.path(customer.getId() + "_" + type + "_" + fileName)
				.toUriString();
		//document.setUrl(protocol + filePathUrl + customer.getId() + "/" + type + "/"+ fileName);
		document.setUrl(fileDownloadUri);
		Document doc = mapper.map(document, Document.class);
		return doc;
	}

	@Override
	public List<DocumentDTO> getDocumentByType(Long customerId, String documentType) {
		return repository.findByCustomerIdAndType(customerId, documentType).stream()
				.map(m -> mapper.map(m, DocumentDTO.class)).collect(Collectors.toList());
	}

	@Override
	public List<DocumentDTO> getCustomerDocuments(Long customerId) {
		return repository.findByCustomerId(customerId).stream().map(m -> mapper.map(m, DocumentDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public String upload(List<DocumentDTO> documents) {
		if (documents == null || documents.size() < 0)
			throw new ApplicationException("Empty document(s)!");
		
		ParameterDTO paramFormFee = parameterService.getParameterByName(Constants.MAX_DOC_UPLOAD);
		Integer noOfDocs = Integer.parseInt(paramFormFee.getValue());
		
		if(documents.size() > 5) 
			throw new ApplicationException("Maximum number of documents exceeds " + noOfDocs);
		
		DocumentDTO documentDTO = null;
		// List<DocumentDTO> docs = new ArrayList<>();
		int counter = 0;
		for (DocumentDTO document : documents) {
			try {
				documentDTO = upload(document);
				counter++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return counter > 0 ? counter + " of " + documents.size() + " documents was sucessfully uploaded"
				: "Documents upload failed";
	}
	
	@Override
	public boolean delete(Long documentId) {
		Document document = repository.findById(documentId).orElseThrow(() -> new ApplicationException("Document does not exist"));
		Path path = Paths.get(document.getLocation());
		boolean deleted = false;
		try {
			 Files.deleteIfExists(path);
		}catch(IOException e) {
			throw new ApplicationException("Error deleting document");
		}
		deleted = repository.softDelete(document);
		return deleted;
	}

}
