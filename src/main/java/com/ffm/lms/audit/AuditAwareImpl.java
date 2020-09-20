package com.ffm.lms.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;


public class AuditAwareImpl implements AuditorAware<String> {


	@Override
	public Optional<String> getCurrentAuditor() {
		Optional<String> system = Optional.of("System");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null || !authentication.isAuthenticated()) {
			return system;
		}
		
		return Optional.of(authentication.getName());
	}

}
