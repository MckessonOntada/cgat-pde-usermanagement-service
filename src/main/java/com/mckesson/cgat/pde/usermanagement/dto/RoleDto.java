package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

	private String id;

	@NotNull
	@NotEmpty(message = "Enter valid role name")
	private String name;

	private String desc;

	private String permission_type;

	@NotNull
	@NotEmpty(message = "Grant some permission")
	private List<String> permissions;

}
