package com.ffm.lms.user.domain;

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
import com.ffm.lms.commons.security.CurrentUser;
import com.ffm.lms.commons.security.UserPrincipal;
import com.ffm.lms.customer.domain.dto.CustomerDTO;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "User Controller", protocols = "http", tags = { "User-Controller" })
@RequestMapping("/lms/api/user")
@RequiredArgsConstructor
@RestController
public class UserController extends BaseController {

	private final UserService service;

	@PostMapping("/add")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<CreateUserDTO>> add(@RequestBody CreateUserDTO user) {
		log(user, "User Create Request: [{}]");
		ApiResponseBase<CreateUserDTO> response = new ApiResponseBase<>();
		response.setResponse(service.create(user));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully added user");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/remove/{userId}")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<Boolean>> remove(@PathVariable("userId") Long userId) {
		log(userId, "User Delete Request: [{}]");
		ApiResponseBase<Boolean> response = new ApiResponseBase<>();
		response.setResponse(service.delete(userId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully removed user");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping
	@Secured({ "ROLE_BASIC", "ROLE_STANDARD", "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<User>> currentUser(@CurrentUser UserPrincipal currentUser) {
		log(currentUser, "User Current Request: [{}]");
		ApiResponseBase<User> response = new ApiResponseBase<>();
		response.setResponse(service.getUser(currentUser.getUsername()));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully got user");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/admin/{userId}")
	@Secured({ "ROLE_ADMIN" })
	public ResponseEntity<ApiResponseBase<UserUpdate>> updateUserAndRole(@PathVariable("userId") Long userId, @RequestBody UserUpdate user) {
		log(userId, "Admin User Update Request: [{}]");
		log(user, "Admin User Update Request: [{}]");
		ApiResponseBase<UserUpdate> response = new ApiResponseBase<>();
		response.setResponse(service.modifyUserAndRole(user, userId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully updated user");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("{userId}")
	@Secured({ "ROLE_STANDARD", "ROLE_BASIC" })
	public ResponseEntity<ApiResponseBase<UserUpdate>> updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdate user) {
		log(userId, "User Update Request: [{}]");
		log(user, "User Update Request: [{}]");
		ApiResponseBase<UserUpdate> response = new ApiResponseBase<>();
		response.setResponse(service.modify(user, userId));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully updated user");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/list")
	@Secured({ "ROLE_ADMIN" })
    public ResponseEntity<ApiResponseBase<Page<User>>> list(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        ApiResponseBase<Page<User>> response = new ApiResponseBase<>();
        response.setResponse(service.getAllUsers(pageable));
        response.setStatus("success");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
        /* Made changes that needs to be push to the repository */
    }
}
