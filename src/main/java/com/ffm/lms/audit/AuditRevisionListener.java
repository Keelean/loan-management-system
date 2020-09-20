package com.ffm.lms.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuditRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = "";
		if (authentication == null || !authentication.isAuthenticated()) {
			//return username;
			username = "System";
		}
		else {
			username = authentication.getName();
		}
		
		AuditRevisionEntity audit = (AuditRevisionEntity) revisionEntity;
		audit.setUser(username);
	}

}
