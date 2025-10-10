package com.mckesson.cgat.pde.usermanagement.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionsRoleUserDto {

	private Permission permission;

	private Map<String, List<String>> roleAndUsersList;

}
