package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.Permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionsPatchDTO {

	private Long roleId;

	private Set<Permission> permissions;

}
