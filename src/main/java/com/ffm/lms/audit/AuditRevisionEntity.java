package com.ffm.lms.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "revision_info")
@RevisionEntity(AuditRevisionListener.class)
/*@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "rev_timestamp")),
		@AttributeOverride(name = "id", column = @Column(name = "revision_id")) })*/
@Getter
@Setter
public class AuditRevisionEntity extends DefaultRevisionEntity{
	
	//@Column(name = "user")
	private String user;
	
	
}
