package com.ffm.lms.user.domain;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ffm.lms.customer.commons.data.CommonRepository;

@Repository
public interface UserRepository extends CommonRepository<User>{

	Optional<User> findByUsernameOrEmailOrMobileNo(String username, String email, String mobileNo);
}
