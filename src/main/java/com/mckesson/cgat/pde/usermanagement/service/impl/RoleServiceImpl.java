package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.PermissionRepository;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
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
			throw new ResourceNotFoundException("Practice not found with id:" + practiceId);
		role.setPracticeId(byId.get());
		return roleRepository.save(role);
	}

	@Override
	public List<Role> getRolesByPracticeId(String practiceId) throws ResourceNotFoundException {
		Optional<Practice> byId = practiceRepository.findById(Long.valueOf(practiceId));
		if (!byId.isPresent())
			throw new ResourceNotFoundException("Practice not found with id:" + practiceId);
		return roleRepository.findByPracticeId(Long.valueOf(practiceId));
	}

	@Override
	public List<Permission> getAllPermissions() {
		return permissionRepository.findAll();
	}

	@Override
	public Page<Role> findAll(Pageable paging, String query) {
		query = StringUtils.toRootLowerCase(query);
		Page<Role> response;
		response = roleRepository.findAllByQueryText(query, paging);
		return response;
	}

}
