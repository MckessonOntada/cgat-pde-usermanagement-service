package com.mckesson.cgat.pde.usermanagement.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface RoleService {

	Role createOrUpdateRole(Role role, String practiceId) throws ResourceNotFoundException;

	Role getRole(Long roleId) throws ResourceNotFoundException;

	void deleteRole(Long roleId) throws ResourceNotFoundException;

	Role updateRole(Long roleId, Role updatedRole) throws ResourceNotFoundException;

	Page<Role> findAll(Pageable paging, String query, String practiceId, List<String> status, LocalDate startDate,
			LocalDate endDate);

}
