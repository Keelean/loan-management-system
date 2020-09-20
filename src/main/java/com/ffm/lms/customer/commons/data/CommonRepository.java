package com.ffm.lms.customer.commons.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.ffm.lms.customer.commons.domain.BaseEntity;


@NoRepositoryBean
public interface CommonRepository<T> extends JpaRepository<T, Long>{
	
	public Page<T> findUsingPattern(String pattern, Pageable details);
//	public Page<T> findAll(Pageable details);
//	public List<T> findAll();

	public void detach(T entity);
	public void delete(BaseEntity entity);
	public boolean softDelete(BaseEntity entity);

	public boolean isLockOk(Object a, Object b);
}
