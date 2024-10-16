package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserRoleRequest {

	@NotNull(message = "Name must not be null")
	private String displayName;

	@NotNull(message = "User email must not be null")
	@Email
	private String email;

	@NotNull(message = "Roles must not be null")
	@NotEmpty
	private List<String> roles;

	@NotNull(message = "Status must not be null")
	private String status;

	private Set<String> validationMessages = new HashSet<>();

	public UserRoleRequest(@NotNull(message = "Name must not be null") String displayName,
			@NotNull(message = "User email must not be null") @Email String email,
			@NotNull(message = "Roles must not be null") @NotEmpty List<String> roles,
			@NotNull(message = "User email must not be null") @Email String status) {
		super();
		this.displayName = displayName;
		this.email = email;
		this.roles = roles;
		this.status = status;
	}

}
