package com.mckesson.cgat.pde.usermanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface RoleService {

	Role createOrUpdateRole(Role role, String practiceId) throws ResourceNotFoundException;

	Role getRole(String roleId) throws ResourceNotFoundException;

	Role linkPermissionToRole(Long roleId, Set<Permission> permissions) throws ResourceNotFoundException;

	Page<Role> findAll(Pageable paging, String query, String practiceId, List<String> status, LocalDate startDate,
			LocalDate endDate);

}
