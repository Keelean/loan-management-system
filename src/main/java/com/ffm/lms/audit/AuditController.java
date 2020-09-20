package com.ffm.lms.audit;

import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Audit Controller", protocols = "http", tags = { "Audit-Controller" })
@RequestMapping("/lms/api/audit")
@RequiredArgsConstructor
@RestController
public class AuditController extends BaseController{

	private final AuditService service;
	
	@PostMapping("{audit}")
	//@Secured({ "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<AuditResponse>> list(@PathVariable("audit") String audit, @RequestBody AuditRequest request) {
        ApiResponseBase<AuditResponse> response = new ApiResponseBase<>();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        response.setResponse(service.getAllCustomers(request, audit));
        response.setStatus("success");
        response.setMessage("Successfully retrieved log");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
        /* Made changes that needs to be push to the repository */
    }
}
