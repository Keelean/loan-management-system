package com.ffm.lms.user.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

	CreateUserDTO create(CreateUserDTO user);

	boolean delete(Long userId);

	User findByUsernameOrEmailOrMoblieNo(String usernameEmail);
	
	UserUpdate modify(UserUpdate user, Long userId);
	public UserUpdate modifyUserAndRole(UserUpdate userUpdate, Long userId);
	
	boolean isUserExist(Long userId);
	
	boolean isUserExist(String userId);
	
	User getUser(String currentUser);
	
	default Page<User> getAllUsers(Pageable pageable) {
		return Page.empty(pageable);
	}

	default Optional<User> getUser(Long id) {
		return Optional.empty();
	}
	
	String setDefaultPassword(String username);
	
	String resetPassword(String username, String password);
	

}
