package com.ffm.lms.loan.transaction.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.loan.transaction.domain.dto.CreateTransactionDTO;
import com.ffm.lms.loan.transaction.domain.dto.TransactionDTO;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Loan Transaction Controller", protocols = "http", tags = { "Transaction-Controller" })
@RequestMapping("/lms/api/transaction")
@RequiredArgsConstructor
@RestController
public class TransactionController extends BaseController{

	private final TransactionService service;

	@PostMapping("/create")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<TransactionDTO>> add(@RequestBody CreateTransactionDTO transaction) {
		log(transaction, "Transaction Create Request: [{}]");
		ApiResponseBase<TransactionDTO> response = new ApiResponseBase<>();
		response.setResponse(service.repay(transaction));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully created loan transaction");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/{loanId}")
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Page<TransactionDTO>>> transactions(
			@PageableDefault(size = 10, page = 0) Pageable pageable, @PathVariable("loanId") Long loanId) {
		log(loanId, "Transaction List Request: [{}]");
		ApiResponseBase<Page<TransactionDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.findTransactionsById(loanId, pageable));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved transacions");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/compute/{loanId}/{source}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<TransactionDTO>> add(@PathVariable("loanId") Long loanId,@PathVariable("source") String source) {
		log(source, "Transaction Computation Request: [{}]");
		log(loanId, "Transaction Computation Request: [{}]");
		ApiResponseBase<TransactionDTO> response = new ApiResponseBase<>();
		response.setResponse(service.computeRepayment(loanId, source));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully computed loan repayment");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
