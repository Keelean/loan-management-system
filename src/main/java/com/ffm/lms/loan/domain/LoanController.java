package com.ffm.lms.loan.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.customer.domain.dto.CustomerResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanApprovalDTO;
import com.ffm.lms.loan.domain.dto.LoanDTO;
import com.ffm.lms.loan.domain.dto.LoanResponseDTO;
import com.ffm.lms.loan.domain.dto.LoanUpdateDTO;
import com.ffm.lms.loan.domain.dto.TopUpDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@Api(value = "Loan Controller", protocols = "http", tags = { "Loan-Controller" })
@RequestMapping("/lms/api/loan")
@RequiredArgsConstructor
@RestController
public class LoanController extends BaseController {

	private final LoanService service;
	private final TopupService topupService;

	@PostMapping("/create")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> add(@RequestBody LoanDTO loanDTO) {
		log(loanDTO, "Loan Create Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.create(loanDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully created loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/update")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Loan>> update(@RequestBody LoanUpdateDTO loanDTO)
			throws ApplicationException {
		log(loanDTO, "Loan Update Request: [{}]");
		ApiResponseBase<Loan> response = new ApiResponseBase<>();
		response.setResponse(service.modify(loanDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully modified loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{loanId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Boolean>> delete(@PathVariable("loanId") Long loanId)
			throws ApplicationException {
		log(loanId, "Loan Deelte Request: [{}]");
		ApiResponseBase<Boolean> response = new ApiResponseBase<>();
		response.setMessage("Successfully removed loan");
		response.setResponse(service.delete(loanId));
		response.setStatus("success");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("{loanId}")
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> findById(@PathVariable("loanId") Long loanId) {
		log(loanId, "Loan Find By ID Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.findById(loanId));
		response.setStatus("success");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/topup")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> topUp(@RequestBody TopUpDTO loanDTO) {
		log(loanDTO, "Loan Top Up Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(topupService.topUp(loanDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully topped up customer loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/compute")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> computeLoan(@RequestBody LoanDTO loanDTO) {
		log(loanDTO, "Loan Compute Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.calculateLoan(loanDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully computed loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/page/{customerId}")
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Page<LoanResponseDTO>>> getCustomerLoansPage(@PathVariable("customerId") Long customerId,
			@PageableDefault(size = 10, page = 0) Pageable pageable) {
		log(customerId, "Loan Page Request: [{}]");
		ApiResponseBase<Page<LoanResponseDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.page(customerId, pageable));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved loan(s)");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/list/{customerId}")
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<List<LoanResponseDTO>>> getCustomerLoansList(@PathVariable("customerId") Long customerId) {
		log(customerId, "Loan List Request: [{}]");
		ApiResponseBase<List<LoanResponseDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.list(customerId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved loan(s)");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/approve/{loanId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> approveLoan(@PathVariable("loanId") Long loanId, @RequestBody LoanApprovalDTO approvalDTO) {
		log(approvalDTO, "Loan Approval Request: [{}]");
		log(loanId, "Loan Approval Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.approve(loanId, approvalDTO));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully approved loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	
    @GetMapping
    @Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Page<LoanResponseDTO>>> filter(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        ApiResponseBase<Page<LoanResponseDTO>> response = new ApiResponseBase<>();
        response.setMessage("Successfully retrieved loans(s)");
        response.setStatus("success");
        response.setResponse(service.list(pageable));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
	@PostMapping("/liquidate/{loanId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<LoanResponseDTO>> liquidate(@PathVariable("loanId") Long loanId) {
		log(loanId, "Loan Liquidate Request: [{}]");
		ApiResponseBase<LoanResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.liquidate(loanId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully liquidated loan");
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
}
