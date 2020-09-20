package com.ffm.lms.commons.aop.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.ffm.lms.commons.aop.security.annotations.Permission;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class PermissionChecker {
	
	@Pointcut("bean(Impl*)")
	public void serviceJoinPoints() {}
	
	@Pointcut("bean(*Controller)")
	public void controllerJoinPoints() {}

	@Before("controllerJoinPoints() && @annotation(permission)")
	public void isUserPermitted(JoinPoint joinPoint, Permission permission) {
		log.info("---------Default Permission: " + permission.permitActions()[0]);
	}
}
