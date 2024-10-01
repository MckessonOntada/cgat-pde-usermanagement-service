package com.mckesson.cgat.pde.usermanagement.service.impl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs;
import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.PermissionDto;
import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission2;
import com.mckesson.cgat.pde.usermanagement.model.Role2;

@Service
public class Role2Utils {

	public List<RoleResponseDto> mapEntityToDto(List<Role2> allRoles, List<Permission2> allPermissions) {
		Map<String, Permission2> permissionIDWithObjMap = new HashMap<>();
		allPermissions.stream().map(a -> permissionIDWithObjMap.put(a.getId().toString(), a))
				.collect(Collectors.toList());
		List<RoleResponseDto> response = new ArrayList<>();
		for (Role2 role : allRoles) {
			RoleResponseDto roleRes = RoleResponseDto.builder().description(role.getDescription()).name(role.getName())
					.role_id(role.getId().toString()).permission_type(role.getPermissionType()).build();
			List<PermissionDto> allPermDtos = role.getPermissions().stream().map(a -> {
				Permission2 perm = permissionIDWithObjMap.get(a);
				CustomDTOs.PermissionDto dto = new CustomDTOs.PermissionDto(perm.getId().toString(), perm.getName(),
						perm.getLabel(), perm.getIsActive());
				return dto;
			}).collect(Collectors.toList());
			roleRes.setPermissions(allPermDtos);
			response.add(roleRes);
		}
		return response;
	}

}
