package com.ffm.lms.commons.security.auth;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.controller.BaseController;
import com.ffm.lms.commons.data.response.ApiResponseBase;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.commons.security.JwtTokenProvider;
import com.ffm.lms.user.domain.CreateUserDTO;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserService;
import com.ffm.lms.user.domain.types.DefaultCode;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;

@Api(value = "Authentication Controller", protocols = "http", tags = { "Authentication-Controller" })
@RequestMapping("/lms/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthenticationController extends BaseController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final ParameterService parameterService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponseBase<JwtAuthenticationResponse>> authenticateUser(
			@Valid @RequestBody LoginRequest loginRequest) {
		ApiResponseBase<JwtAuthenticationResponse> response = new ApiResponseBase<>();
		JwtAuthenticationResponse jwt = new JwtAuthenticationResponse();
		//ParameterDTO parameter = parameterService.getParameterByName(Constants.CHECK_DEFAULT_PASS);
		//if(parameter.getValue().equals(Constants.YES)) {
			disAllowDefaultLogin(loginRequest.getPassword(),loginRequest.getUsernameOrEmailOrPhone());
		//}
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmailOrPhone(), loginRequest.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateToken(authentication);
			jwt.setAccessToken(token);
			response.setResponse(jwt);
			response.setStatus("success");
			response.setSuccess(true);
			response.setMessage("Successfully signed in");
			return new ResponseEntity<>(response, HttpStatus.OK);
		
		}
		catch(AuthenticationException e) {
			//throw new ApplicationContextException("Password or Username is incorrect!");
			jwt.setAccessToken("");
			response.setResponse(jwt);
			response.setStatus("failed");
			response.setSuccess(false);
			response.setErrors(Arrays.asList("Username or password is incorrect!"));
			response.setMessage("Username or password is incorrect!");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping("/login/default")
	public ResponseEntity<ApiResponseBase<String>> sendDefaultPassword(@RequestBody LoginRequest loginRequest) {
		ApiResponseBase<String> response = new ApiResponseBase<>();
		response.setResponse(userService.setDefaultPassword(loginRequest.getUsernameOrEmailOrPhone()));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Successfully sent a default password to your mail");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/login/reset")
	public ResponseEntity<ApiResponseBase<String>> sendDefaultPassword(@RequestBody PasswordResetRequest reset) {
		ApiResponseBase<String> response = new ApiResponseBase<>();
		response.setResponse(userService.resetPassword(reset.getUsernameOrEmailOrPhone(), reset.getPassword()));
		response.setStatus("success");
		response.setSuccess(true);
		response.setMessage("Password successfully reset");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	private void disAllowDefaultLogin(String password, String username){
		
		User user = userService.findByUsernameOrEmailOrMoblieNo(username);
		if(user.getDefaultCode().equals(DefaultCode.YES)) {
			throw new ApplicationException("Change default password", Constants.AUTH_RESET_PASSWORD, Arrays.asList("You cannot login with default password!!"));
		}
	}

}
