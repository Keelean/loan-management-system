package com.ffm.lms.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.report.data.ReportParam;
import com.ffm.lms.report.service.ReportService;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Report Controller", protocols = "http", tags = { "Report-Controller" })
@RequestMapping("/lms/api/report")
@RequiredArgsConstructor
@RestController
public class ReportController extends BaseController {

	private final ReportService service;
	
	@PostMapping
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<String>> generate(@RequestBody ReportParam reportParam) {
		ApiResponseBase<String> response = new ApiResponseBase<>();
		response.setResponse(service.generateReport(reportParam));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully generated report");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
