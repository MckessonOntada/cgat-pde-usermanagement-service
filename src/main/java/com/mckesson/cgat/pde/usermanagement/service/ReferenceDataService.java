package com.mckesson.cgat.pde.usermanagement.service;

import com.mckesson.cgat.pde.usermanagement.dto.ReferenceDataResponse;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import java.util.List;

public interface ReferenceDataService {
    ReferenceDataResponse findAll();

	List<Role> getAllRolesAndPermissions();
	
	Role getRoleByname(String roleName) throws ResourceNotFoundException;
}
