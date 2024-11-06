package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.PermissionRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	private final PermissionRepository permissionRepository;

	private final RolePermissionMappingRepository rolePermissionMappingRepository;

	private final RoleRepository roleRepository;

	private final PracticeRepository practiceRepository;

	public RoleServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository2,
			PracticeRepository practiceRepository, RolePermissionMappingRepository rolePermissionMappingRepository) {
		this.permissionRepository = permissionRepository;
		this.rolePermissionMappingRepository = rolePermissionMappingRepository;
		this.roleRepository = roleRepository2;
		this.practiceRepository = practiceRepository;
	}

	@Override
	public List<Permission> getPermissionsByRoleId(String roleId) {
		List<String> permissionStrings = rolePermissionMappingRepository.findPermissionsByRoleId(roleId);
		return permissionStrings.stream().map(Permission::valueOf).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Role createOrUpdateRole(Role role, String practiceId) throws ResourceNotFoundException {
		Optional<Practice> practiceOptionale = practiceRepository.findById(Long.valueOf(practiceId));
		Practice practice = practiceOptionale
			.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + practiceId));
		role.setPracticeId(practice);
		return roleRepository.save(role);
	}

	@Override
	public Role getRole(String roleId) throws ResourceNotFoundException {
		Optional<Role> roleOpt = roleRepository.findById(Long.valueOf(roleId));
		return roleOpt.orElseThrow(() -> new ResourceNotFoundException("Practice not found with id : " + roleId));
	}

	@Override
	public List<Permission> getAllPermissions() {
		return Arrays.asList(Permission.values());
	}

	@Override
	public Page<Role> findAll(Pageable paging, String query, String practiceId, List<String> status) {
		query = StringUtils.toRootLowerCase(query);
		Page<Role> response;
		String statusActive = CustomUtils.object_status.ALL.name();
		if (!CollectionUtils.isEmpty(status)) {
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.ACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name();
			}
			if (status.stream().anyMatch(i -> i.equalsIgnoreCase(CustomUtils.object_status.INACTIVE.name()))) {
				statusActive = CustomUtils.object_status.ACTIVE.name().equals(statusActive)
						? CustomUtils.object_status.ALL.name() : CustomUtils.object_status.INACTIVE.name();
			}
		}
		response = roleRepository.findAllByQueryText(query, statusActive, Long.valueOf(practiceId), paging);
		return response;
	}

}
