package com.ffm.lms.commons.parameters.domain;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface ParameterRepository extends CommonRepository<Parameter> {
	
	//@Query(value = "select p from Parameter p where p.name = :name")
	Optional<Parameter> findByName(String name);
}
