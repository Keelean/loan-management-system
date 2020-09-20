package com.ffm.lms.user.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffm.lms.user.domain.UserDTO.UserDTOBuilder;
import com.ffm.lms.user.domain.types.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDTO implements Serializable {
	
	private Long id;
	
	@NotNull
	private String firstname;
	@NotNull
	private String lastname;
	@NotNull
	private String username;
	private String middleName;
	private String name;

	@NotNull
	private String email;
	
	@JsonIgnore
	private String password;
	private String mobileNo;
	
	@NotNull
	private String role;
	
    private LocalDateTime created;
    
    private LocalDateTime updated;
}
