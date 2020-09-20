package com.ffm.lms.commons.fileIO.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.aop.security.annotations.Permission;
import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.fileIO.data.FileUploadDTO;
import com.ffm.lms.commons.fileIO.domain.dto.FileUploadInfoDTO;
import com.ffm.lms.commons.security.CurrentUser;
import com.ffm.lms.commons.security.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(value = "File Upload Controller", protocols = "http", tags = { "File-Upload-Controller" })
@RequestMapping("/lms/api/file")
@RequiredArgsConstructor
@RestController
public class FileUploadController extends BaseController {

	private final FileUploadService service;

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully uploaded file") })
	@ApiOperation(value = "For Uploading File", response = FileUploadInfoDTO.class)
	@PostMapping("/upload")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<FileUploadInfoDTO>> add(@RequestBody FileUploadInfoDTO fileInfo) {
		log(fileInfo, "File Info Upload Request: [{}]");
		ApiResponseBase<FileUploadInfoDTO> response = new ApiResponseBase<>();
		response.setResponse(service.upload(fileInfo));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully uploaded file");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	//@Permission(permitActions = { "CREATE", "DELETE" })
	@GetMapping(value = "/uploaded-files")
	@Secured({ "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Page<FileUploadDTO>>> uploadedFiles(
			@PageableDefault(size = 10, page = 0) Pageable pageable) {
		ApiResponseBase<Page<FileUploadDTO>> response = new ApiResponseBase<>();
		response.setResponse(service.getFilesUploaded(pageable));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successful");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
