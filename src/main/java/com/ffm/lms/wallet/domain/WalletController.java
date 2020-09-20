package com.ffm.lms.wallet.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.wallet.transaction.domain.WalletDTO;
import com.ffm.lms.wallet.transaction.domain.WalletTransaction;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Customer Wallet Controller", protocols = "http", tags = { "Wallet-Controller" })
@RequestMapping("/lms/api/wallets")
@RequiredArgsConstructor
@RestController
public class WalletController extends BaseController{

	private final WalletService service;

	@PostMapping("{customerId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Wallet>> credit(@PathVariable("loanId") Long customerId,
			@RequestBody WalletDTO wallet) {
		log(customerId, "Wallet Credit Request: [{}]");
		log(wallet, "Wallet Credit Request: [{}]");
		ApiResponseBase<Wallet> response = new ApiResponseBase<>();
		response.setResponse(service.credit(customerId, wallet.getAmmount()));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully credited customer wallet");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("{customerId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Wallet>> debit(@PathVariable("customerId") Long customerId,
			@RequestBody WalletDTO wallet) {
		log(customerId, "Wallet Debit Request: [{}]");
		log(wallet, "Wallet Debit Request: [{}]");
		ApiResponseBase<Wallet> response = new ApiResponseBase<>();
		response.setResponse(service.debit(customerId, wallet.getAmmount()));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully credited customer wallet");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("{customerId}")
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Page<WalletTransaction>>> transactions(
			@PathVariable("customerId") Long customerId, @PageableDefault(size = 10, page = 0) Pageable pageable) {
		log(customerId, "Wallet List Request: [{}]");
		ApiResponseBase<Page<WalletTransaction>> response = new ApiResponseBase<>();
		response.setResponse(service.transactions(customerId, pageable));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved customer wallet");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
