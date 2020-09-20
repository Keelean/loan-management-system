package com.ffm.lms.commons.parameters.domain;

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
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.user.domain.UserUpdate;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Parameter Controller", protocols = "http", tags = { "Parameter-Controller" })
@RequestMapping("/lms/api/parameter")
@RequiredArgsConstructor
@RestController
public class ParameterController extends BaseController {

	private final ParameterService service;

	@PostMapping("/create")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<ParameterDTO>> add(@RequestBody ParameterDTO parameter) {
		log(parameter, "Parameter Create Request: [{}]");
		ApiResponseBase<ParameterDTO> response = new ApiResponseBase<>();
		response.setResponse(service.add(parameter));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully created parameter");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{parameterId}")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Boolean>> delete(@PathVariable("parameterId") Long parameterId)
			throws ApplicationException {
		log(parameterId, "Parameter Delete Request: [{}]");
		ApiResponseBase<Boolean> response = new ApiResponseBase<>();
		response.setMessage("Successfully removed parameter");
		response.setResponse(service.delete(parameterId));
		response.setStatus("success");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Page<ParameterDTO>>> list(
			@PageableDefault(size = 10, page = 0) Pageable pageable) throws ApplicationException {
		//log(parameterId, "Parameter Delete Request: [{}]");
		ApiResponseBase<Page<ParameterDTO>> response = new ApiResponseBase<>();
		response.setMessage("Successfully retrieved parameters");
		response.setResponse(service.list(pageable));
		response.setStatus("success");
		response.setSuccess(true);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("{paramId}")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<ParameterDTO>> update(@PathVariable("paramId") Long paramId, @RequestBody ParameterDTO parameter) {
		log(paramId, "Parameter Update Request: [{}]");
		log(parameter, "Parameter Update Request: [{}]");
		ApiResponseBase<ParameterDTO> response = new ApiResponseBase<>();
		response.setResponse(service.modify(parameter, paramId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully updated parameter");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
