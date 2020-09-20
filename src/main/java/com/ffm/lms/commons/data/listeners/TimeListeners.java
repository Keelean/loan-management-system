package com.ffm.lms.commons.data.listeners;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import com.ffm.lms.customer.commons.domain.BaseEntity;

public class TimeListeners {
	
	@PrePersist
	public void onPersist(BaseEntity baseEntity) {
		baseEntity.setCreated(LocalDateTime.now());
		baseEntity.setUpdated(LocalDateTime.now());
	}
	
	@PreUpdate
	public void onUpdate(BaseEntity baseEntity) {
		baseEntity.setUpdated(LocalDateTime.now());
	}

}
