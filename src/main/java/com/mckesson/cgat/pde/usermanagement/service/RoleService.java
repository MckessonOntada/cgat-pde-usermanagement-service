package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface RoleService {

	Role createOrUpdateRole(Role role);
	
	Role getRole(String id) throws ResourceNotFoundException;

//	List<RoleResponseDto> getAllRoles();
//
//	String createPermission(String name, String label);
//
//	String deletePermission(String id) throws ResourceNotFoundException;
//
//	List<Permission> getAllPermissions();

}
