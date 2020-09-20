package com.ffm.lms.dashboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.data.response.ApiResponseBase;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "DashBoard Controller", protocols = "http", tags = { "DashBoard-Controller" })
@RequestMapping("/lms/api/dashboard")
@RequiredArgsConstructor
@RestController
public class DashBoardController {

	private final DashBoardService service;

	@GetMapping
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<DashBoardContainer>> getDashBoard() {
		ApiResponseBase<DashBoardContainer> response = new ApiResponseBase<>();
		response.setResponse(service.generateDashBoardDate());
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully retrieved dashboard");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
