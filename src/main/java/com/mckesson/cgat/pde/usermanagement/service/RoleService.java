package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface RoleService {

	Role createOrUpdateRole(Role role, String practiceId) throws ResourceNotFoundException;

	List<Role> getRolesByPracticeId(String practiceId) throws ResourceNotFoundException;

	List<Permission> getAllPermissions();

}
