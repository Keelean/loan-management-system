package com.ffm.lms.commons.security;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ffm.lms.commons.service.BaseService;
import com.ffm.lms.user.domain.User;
import com.ffm.lms.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService extends BaseService implements UserDetailsService {
	
    private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Let people login with either username or email
        User user = userRepository.findByUsernameOrEmailOrMobileNo(username, username, username)
                .orElseThrow(() -> 
                        new UsernameNotFoundException("User not found with username or email : " + username)
        );

        return UserPrincipal.create(user);
	}
	
	// This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return UserPrincipal.create(user);
    }

}
