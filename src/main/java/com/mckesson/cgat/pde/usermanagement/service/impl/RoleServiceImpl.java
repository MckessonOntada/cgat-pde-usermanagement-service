package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
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

	private final RoleRepository roleRepository;

	private final PracticeRepository practiceRepository;

	public RoleServiceImpl(RoleRepository roleRepository2, PracticeRepository practiceRepository) {
		this.roleRepository = roleRepository2;
		this.practiceRepository = practiceRepository;
	}

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
	public Page<Role> findAll(Pageable paging, String query, String practiceId, List<String> status,
			LocalDate startDate, LocalDate endDate) {
		query = StringUtils.toRootLowerCase(query);
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
		return roleRepository.findAllByQueryText(query, statusActive, Long.valueOf(practiceId), paging, startDate,
				endDate);

	}

	@Override
	public Role linkPermissionToRole(Long roleId, Set<Permission> permissions) throws ResourceNotFoundException {
		Role role = roleRepository.findById(roleId)
			.orElseThrow(() -> new ResourceNotFoundException("Role not found for id :" + roleId));
		role.getPermissions().clear();
		role.setPermissions(permissions);
		return roleRepository.save(role);
	}

}
