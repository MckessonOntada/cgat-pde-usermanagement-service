package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.Permission;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionsPatchDTO {

	@NotNull(message = "Role Id can not be null")
	private Long roleId;

	@NotEmpty(message = "Permissions can not be empty")
	@NotNull(message = "Permissions can not be null")
	private Set<Permission> permissions;

}
