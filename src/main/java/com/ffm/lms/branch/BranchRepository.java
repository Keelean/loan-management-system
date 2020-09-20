package com.ffm.lms.branch;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface BranchRepository extends CommonRepository<Branch> {

	Optional<Branch> findByCode(String code);
}
