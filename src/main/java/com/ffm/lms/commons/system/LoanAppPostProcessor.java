package com.ffm.lms.commons.system;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

//@Component
public class LoanAppPostProcessor {//implements BeanPostProcessor {

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
			System.out.println("In AuditCheckBeanPostProcessor. postProcessBeforeInitialization, processing bean type: " + bean.getClass());
			return bean;
			}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
