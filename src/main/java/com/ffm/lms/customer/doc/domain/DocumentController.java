package com.ffm.lms.customer.doc.domain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.doc.domain.dto.DocumentDTO;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Document Manager Controller", protocols = "http", tags = { "Document-Controller" })
@RequestMapping("/lms/api/document")
@RequiredArgsConstructor
@RestController
public class DocumentController extends BaseController{

	private final DocumentService service;

	@PostMapping("/create")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<String>> add(@RequestBody List<DocumentDTO> documents)
			throws FileNotFoundException, IOException {
		log(documents, "Customer Document Upload Request: [{}]");
		ApiResponseBase<String> response = new ApiResponseBase<>();
		String message = service.upload(documents);
		response.setResponse(message);
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage(message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{customerId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<List<DocumentDTO>>> getCustomerDocuments(
			@PathVariable("customerId") Long customerId) {
		log(customerId, "Customer Document Get Request: [{}]");
		ApiResponseBase<List<DocumentDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.getCustomerDocuments(customerId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successful");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/list/{type}/{customerId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<List<DocumentDTO>>> getDocumentByType(@PathVariable("type") String type,
			@PathVariable("customerId") Long customerId) {
		log(type, "Customer Document Get By Type Request: [{}]");
		log(customerId, "Customer Document Get By Type Request: [{}]");
		ApiResponseBase<List<DocumentDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.getDocumentByType(customerId, type));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successful");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
    @DeleteMapping("{documentId}")
    @Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Boolean>> delete(@PathVariable("documentId") Long documentId) throws ApplicationException {
    	log(documentId, "Customer Document Delete Request: [{}]");
        ApiResponseBase<Boolean> response = new ApiResponseBase<>();
        response.setMessage("Successfully deleted document");
        response.setResponse(service.delete(documentId));
        response.setStatus("success");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
