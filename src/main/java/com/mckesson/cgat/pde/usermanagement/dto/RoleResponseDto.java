package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {

	private String role_id;

	private String name;

	private String description;

	private String permission_type;

	private List<CustomDTOs.PermissionDto> permissions;

}
