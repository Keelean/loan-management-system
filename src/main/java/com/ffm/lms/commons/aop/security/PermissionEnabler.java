package com.ffm.lms.commons.aop.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class PermissionEnabler {
	
	//@Pointcut("within(com.ffm.lms.domain..*)")
	@Pointcut("bean(*Controller) || bean(*Impl)")
    public void allControllerOrServiceImplMethods() {};
    
    @Pointcut("bean(*Impl)")
    public void allServiceImplMethods() {
    	
    }

	@Before("allControllerOrServiceImplMethods() && @annotation(getMapping)")
	public void logAspect(JoinPoint joinPoint, GetMapping getMapping) {
	
			MethodSignature signature = (MethodSignature)joinPoint.getSignature();
			Method method = signature.getMethod();
			GetMapping myAnnotation = method.getAnnotation(GetMapping.class);
			if(myAnnotation != null) {
				//log.info("************Annotation Value: ");
			}
	}
}
