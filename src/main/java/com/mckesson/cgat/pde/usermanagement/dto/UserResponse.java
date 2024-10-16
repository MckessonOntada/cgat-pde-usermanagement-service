package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserResponse {

	private String id;

	private String email;

	private List<KeyValueDto> roles;

	private String displayName;

	private Date updatedDate;

	private boolean active;

}
