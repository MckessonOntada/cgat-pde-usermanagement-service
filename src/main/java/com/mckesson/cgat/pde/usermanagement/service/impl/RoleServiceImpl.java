package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

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

	private final RoleRepository roleRepository;

	private final PracticeRepository practiceRepository;

	public RoleServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository2,
			PracticeRepository practiceRepository) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository2;
		this.practiceRepository = practiceRepository;
	}

	@Override
	@Transactional
	public Role createOrUpdateRole(Role role, String practiceId) throws ResourceNotFoundException {
		Optional<Practice> byId = practiceRepository.findById(Long.valueOf(practiceId));
		if (!byId.isPresent())
			throw new ResourceNotFoundException(practiceId);
		role.setPracticeId(byId.get());
		return roleRepository.save(role);
	}

	@Override
	public Role getRole(String roleId) throws ResourceNotFoundException {
		Optional<Role> byId = roleRepository.findById(Long.valueOf(roleId));
		return byId.orElseThrow(() -> new ResourceNotFoundException(roleId));
	}

	@Override
	public List<Role> getRolesByPracticeId(String practiceId) throws ResourceNotFoundException {
		Optional<Practice> byId = practiceRepository.findById(Long.valueOf(practiceId));
		if (!byId.isPresent())
			throw new ResourceNotFoundException(practiceId);
		return roleRepository.findByPracticeId(byId.get());
	}

	@Override
	public List<Permission> getAllPermissions() {
		return permissionRepository.findAll();
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
		response = roleRepository.findAllByQueryText(query, statusActive, practiceId, paging);
		return response;
	}

}
