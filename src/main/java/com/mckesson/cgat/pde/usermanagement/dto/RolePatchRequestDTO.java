package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.Permission;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolePatchRequestDTO {

	@Size(max = 100)
	private String name;

	@Size(max = 100)
	private String description;

	private Set<Permission> permissions;

}
