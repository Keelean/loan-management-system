package com.ffm.lms.user.domain;


import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.aop.security.annotations.DefaultPassword;
import com.ffm.lms.commons.constants.Constants;
import com.ffm.lms.commons.exceptions.handler.ApplicationException;
import com.ffm.lms.commons.messages.EmailService;
import com.ffm.lms.commons.parameters.domain.ParameterService;
import com.ffm.lms.commons.parameters.domain.dto.ParameterDTO;
import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.customer.commons.data.DeleteFlag;
import com.ffm.lms.user.domain.types.DefaultCode;
import com.ffm.lms.user.domain.types.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

	private final UserRepository repository;
	private final UserValidator validator;
	private final PasswordEncoder passwordEncoder;
	private final ParameterService parameterService;
	private final EmailService emailService;

	//@RolesAllowed("ADMIN")
	@Override
	public CreateUserDTO create(CreateUserDTO createUserDTO) {
		User user = mapper.map(createUserDTO, User.class);
		user.setRole(Enum.valueOf(Role.class, createUserDTO.getRole()));
		validator.validate(user);

		if (repository.findByUsernameOrEmailOrMobileNo(user.getUsername(), user.getEmail(), user.getMobileNo())
				.isPresent()) {
			throw new ApplicationException("Username or Email or Mobile No. already exist.");
		}
		
		String password = generateDefaultPassword();

		user.setPassword(passwordEncoder.encode(password));
		user.setDefaultCode(DefaultCode.YES);
		CreateUserDTO u = mapper.map(repository.save(user), CreateUserDTO.class);
		
		String message = String.format(Constants.RESET_PASSWORD, user.getFirstname(), user.getUsername(),
				password);
		try {
			emailService.send(user.getEmail(), "User Login Details", message);
		} catch (Exception e) {
			//return "Error sending mail";
		}
		return u;
	}

	//@RolesAllowed("ADMIN")
	@Override
	public boolean delete(Long userId) {
		User user = repository.findById(userId).orElseThrow(() -> new ApplicationException("Invalid User"));
		user.setDelFlag(DeleteFlag.DELETED);
		return repository.softDelete(user);
	}

	@Override
	public User findByUsernameOrEmailOrMoblieNo(String usernameEmailMobileNo) {
		return repository
				.findByUsernameOrEmailOrMobileNo(usernameEmailMobileNo, usernameEmailMobileNo, usernameEmailMobileNo)
				.orElseThrow(() -> new ApplicationException("User does not exist"));
	}

	@Override
	public UserUpdate modify(UserUpdate userUpdate, Long userId) {

		if (userUpdate.getId().longValue() != userId.longValue()) {
			throw new ApplicationException("User does not exist");
		}
		
		Optional<User> user = getUser(userId);

		if (user.isPresent()) {
			User existingUser = user.get();
			String username = userUpdate.getUsername();
			String email = userUpdate.getEmail();
			String mobileNo = userUpdate.getMobileNo();
			if(!existingUser.getUsername().equals(username)) {
				Optional<User> u = repository.findByUsernameOrEmailOrMobileNo(username, username, username);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This username(%s) belongs to another user", username));
				}
				
				u = repository.findByUsernameOrEmailOrMobileNo(email, email, email);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This email (%s) belongs to another user", email));
				}
				
				u = repository.findByUsernameOrEmailOrMobileNo(mobileNo, mobileNo, mobileNo);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This mobile Phone Number(%s) belongs to another user", mobileNo));
				}
			}
			existingUser.setFirstname(userUpdate.getFirstname());
			existingUser.setLastname(userUpdate.getLastname());
			existingUser.setMiddleName(userUpdate.getMiddleName());
			existingUser.setUsername(username);
			existingUser.setEmail(email);
			existingUser.setMobileNo(mobileNo);
			//existingUser.setRole(Role.valueOf(Role.class, userUpdate.getRole()));
			// User user = mapper.map(userUpdate, User.class);
			return mapper.map(repository.save(existingUser), UserUpdate.class);
		}

		throw new ApplicationException("System error. User update failed.");
	}
	
	public UserUpdate modifyUserAndRole(UserUpdate userUpdate, Long userId) {

		if (userUpdate.getId().longValue() != userId.longValue()) {
			throw new ApplicationException("User does not exist");
		}
		
		Optional<User> user = getUser(userId);
		
		if (user.isPresent()) {
			User existingUser = user.get();
			String username = userUpdate.getUsername();
			String email = userUpdate.getEmail();
			String mobileNo = userUpdate.getMobileNo();
			if(!existingUser.getUsername().equals(username)) {
				Optional<User> u = repository.findByUsernameOrEmailOrMobileNo(username, username, username);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This username(%s) belongs to another user", username));
				}
				
				u = repository.findByUsernameOrEmailOrMobileNo(email, email, email);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This email (%s) belongs to another user", email));
				}
				
				u = repository.findByUsernameOrEmailOrMobileNo(mobileNo, mobileNo, mobileNo);
				// If user name is present and is not for the same user throw Exception
				if(u.isPresent() && (u.get().getId().longValue() != existingUser.getId().longValue())) {
					throw new ApplicationException(String.format("This mobile Phone Number(%s) belongs to another user", mobileNo));
				}
			}
			existingUser.setFirstname(userUpdate.getFirstname());
			existingUser.setLastname(userUpdate.getLastname());
			existingUser.setMiddleName(userUpdate.getMiddleName());
			existingUser.setUsername(username);
			existingUser.setEmail(email);
			existingUser.setMobileNo(mobileNo);
			existingUser.setRole(Role.valueOf(Role.class, userUpdate.getRole()));
			return mapper.map(repository.save(existingUser), UserUpdate.class);
		}

		throw new ApplicationException("System error. User update failed.");
	}

	@Override
	public boolean isUserExist(Long userId) {

		return repository.findById(userId).isPresent();
	}
	


	@Override
	public boolean isUserExist(String userId) {

		return repository.findByUsernameOrEmailOrMobileNo(userId, userId, userId).isPresent();
	}
	
	

	@Override
	//@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@DefaultPassword
	public User getUser(String username) {
		return findByUsernameOrEmailOrMoblieNo(username);
	}

	//@Secured("ROLE_ADMIN")
	public Page<User> getAllUsers(Pageable pageable) {
		return repository.findAll(pageable);
	}

	//@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	public Optional<User> getUser(Long id) {
		return repository.findById(id);
	}

	@Override
	public String setDefaultPassword(String username) {
		User user = repository.findByUsernameOrEmailOrMobileNo(username, username, username)
				.orElseThrow(() -> new ApplicationException("User does not exist"));
		String resetPassword = UUID.randomUUID().toString().replaceAll("_", "").substring(0, 9);
		user.setDefaultCode(DefaultCode.YES);
		user.setPassword(passwordEncoder.encode(resetPassword));
		repository.save(user);
		String message = String.format(Constants.RESET_PASSWORD, user.getFirstname(), user.getUsername(),
				resetPassword);
		try {
			emailService.send(user.getEmail(), "Reset Password", message);
		} catch (Exception e) {
			e.printStackTrace();
			return "Error sending mail";
		}
		return "Successfully sent mail";
	}

	@Override
	public String resetPassword(String username, String password) {

		User user = repository.findByUsernameOrEmailOrMobileNo(username, username, username)
				.orElseThrow(() -> new ApplicationException("User does not exist"));
		
		user.setPassword(passwordEncoder.encode(password));
		String message = String.format(Constants.RESET_PASSWORD, user.getFirstname(), user.getUsername(),
				password);
		user.setDefaultCode(DefaultCode.NO);
		user = repository.save(user);
		try {
			emailService.send(user.getEmail(), "Reset Password", message);
		} catch (Exception e) {
			//return "Error sending mail";
		}
		//return "Successfully sent mail";
		if(user != null) {
			return "Password was successfully reset";
		}
		
		return "Password reset failed";
	}

}
