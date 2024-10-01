package com.mckesson.cgat.pde.usermanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.RoleDto;
import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.Permission2;
import com.mckesson.cgat.pde.usermanagement.model.Role2;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@Service
public interface RoleService {

	String createOrUpdateRole(RoleDto dto) throws ResourceNotFoundException;

	Role2 getRole(String id) throws ResourceNotFoundException;

	List<RoleResponseDto> getAllRoles();

	String createPermission(String name, String label);

	String deletePermission(String id) throws ResourceNotFoundException;

	List<Permission2> getAllPermissions();

}
