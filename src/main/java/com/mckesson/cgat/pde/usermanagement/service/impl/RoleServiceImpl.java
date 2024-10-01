package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.RoleDto;
import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission2;
import com.mckesson.cgat.pde.usermanagement.model.Role2;
import com.mckesson.cgat.pde.usermanagement.repository.PermissionRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository2;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.Role2Utils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	private final PermissionRepository permissionRepository;
	private final RoleRepository2 roleRepository2;
	private final Role2Utils role2Utils;

	public RoleServiceImpl(PermissionRepository permissionRepository, RoleRepository2 roleRepository2,
			Role2Utils role2Utils) {
		this.permissionRepository = permissionRepository;
		this.roleRepository2 = roleRepository2;
		this.role2Utils = role2Utils;
	}

	@Override
	public String createOrUpdateRole(RoleDto dto) throws ResourceNotFoundException {
		Role2 role2 = Role2.builder().description(dto.getDesc()).name(dto.getName()).permissions(dto.getPermissions())
				.permissionType(dto.getPermission_type()).build();
		if (dto.getId() != null) {
			if (!roleRepository2.findById(UUID.fromString(dto.getId())).isPresent())
				throw new ResourceNotFoundException("Requested Role not found");
			role2.setId(UUID.fromString(dto.getId()));
			roleRepository2.save(role2);
			return CustomUtils.operation_type.UPDATE.name();
		}
		roleRepository2.save(role2);
		return CustomUtils.operation_type.CREATE.name();

	}

	@Override
	public Role2 getRole(String id) throws ResourceNotFoundException {
		Optional<Role2> byId = roleRepository2.findById(UUID.fromString(id));
		if (byId == null || byId.isEmpty() || !byId.isPresent()) {
			throw new ResourceNotFoundException("Requested Role not found");
		}
		return byId.get();
	}

	@Override
	public List<RoleResponseDto> getAllRoles() {
		List<Role2> allRoles = roleRepository2.findAll();
		List<Permission2> allPermissions = permissionRepository.findAll();
		return role2Utils.mapEntityToDto(allRoles, allPermissions);
	}

	@Override
	public String createPermission(String name, String label) {
		Permission2 permission = new Permission2();
		permission.setIsActive(false);
		permission.setLabel(label);
		permission.setName(name);

		Permission2 save = permissionRepository.save(permission);
		return save.getName();
	}

	@Override
	public String deletePermission(String id) throws ResourceNotFoundException {
		Optional<Permission2> permission = permissionRepository.findById(UUID.fromString(id));
		if (permission != null && !permission.isEmpty()) {
			permission.get().setIsActive(false);
			permissionRepository.save(permission.get());
			return permission.get().getName();
		}
		throw new ResourceNotFoundException("No Permission found with id : " + id);
	}

	@Override
	public List<Permission2> getAllPermissions() {
		return permissionRepository.findAll();
	}

}
