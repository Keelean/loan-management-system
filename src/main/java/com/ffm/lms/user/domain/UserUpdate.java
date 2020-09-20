package com.ffm.lms.user.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffm.lms.user.domain.UserDTO.UserDTOBuilder;

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
public class UserUpdate implements Serializable{

	private Long id;
	
	private String firstname;
	private String lastname;
	private String middleName;
	private String username;
	private String mobileNo;
	private String email;
	private String role;
	

}
