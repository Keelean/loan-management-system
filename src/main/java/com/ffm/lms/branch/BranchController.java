package com.ffm.lms.branch;

import java.util.List;

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

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Branch Controller", protocols = "http", tags = { "Branch-Controller" })
@RequestMapping("/lms/api/branch")
@RequiredArgsConstructor
@RestController
public class BranchController extends BaseController {

	private final BranchService service;

	@PostMapping
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<BranchResponseDTO>> add(@RequestBody BranchCreateDTO branch) {
		log(branch, "Branch Create Request: [{}]");
		ApiResponseBase<BranchResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.create(branch));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully created branch");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	@Secured({ "ROLE_ADMIN", "ROLE_STANDARD", "ROLE_BASIC"})
	public ResponseEntity<ApiResponseBase<List<Branch>>> list() {
		ApiResponseBase<List<Branch>> response = new ApiResponseBase<>();
		response.setResponse(service.list());
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved branches");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("{branchId}")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<BranchResponseDTO>> modify(@PathVariable("branchId") Long branchId,
			@RequestBody BranchUpdateDTO branch) {
		log(branchId, "Branch Update Request: [{}]");
		log(branch, "Branch Update Request: [{}]");
		ApiResponseBase<BranchResponseDTO> response = new ApiResponseBase<>();
		response.setResponse(service.modify(branch, branchId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully modified branch");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
