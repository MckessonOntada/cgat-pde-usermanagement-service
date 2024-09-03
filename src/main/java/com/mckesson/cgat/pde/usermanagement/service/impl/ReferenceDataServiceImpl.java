package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.dto.KeyValueDto;
import com.mckesson.cgat.pde.usermanagement.dto.ReferenceDataResponse;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.ReferenceDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ReferenceDataServiceImpl implements ReferenceDataService {

    private final RoleRepository roleRepository;

    public ReferenceDataServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public ReferenceDataResponse findAll() {
        ReferenceDataResponse referenceData = new ReferenceDataResponse();
        referenceData.setRoles(getRoles());
        return referenceData;
    }

    private List<KeyValueDto> getRoles() {
        return roleRepository.findAll().stream().map(role -> new KeyValueDto(role.getLabel(),role.getShortLabel(),role.getName(), role.getRoleType())).collect(Collectors.toList());
    }

	@Override
	public List<Role> getAllRolesAndPermissions() {
		return roleRepository.findAll();
	}

	@Override
	public Role getRoleByname(String roleName) throws ResourceNotFoundException {
		Optional<Role> role=roleRepository.findByName(roleName);
		return role.orElseThrow(() -> (new ResourceNotFoundException("Role not found :" + roleName)));
	}

	
}
