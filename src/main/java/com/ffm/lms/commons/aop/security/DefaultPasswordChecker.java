package com.ffm.lms.commons.aop.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ffm.lms.commons.aop.security.annotations.DefaultPassword;
import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserService;
import com.ffm.lms.user.domain.types.DefaultCode;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class DefaultPasswordChecker {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ParameterService parameterService;

	@Pointcut("bean(*Controller) || bean(*Impl)")
    public void allControllerOrServiceImplMethods() {};

	@Before("allControllerOrServiceImplMethods() && @annotation(password)")
	public void checkDefaultPassword(JoinPoint joinPoint, DefaultPassword password) {

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DefaultPassword myAnnotation = method.getAnnotation(DefaultPassword.class);
		
		if (Objects.nonNull(myAnnotation)) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			User user = userService.findByUsernameOrEmailOrMoblieNo(username);

			if(user.getDefaultCode().equals(DefaultCode.YES)) {
				throw new ApplicationException("Change default password", Constants.AUTH_RESET_PASSWORD,
						Arrays.asList("You cannot login with default password!!"));
			}
		}

	}
}
