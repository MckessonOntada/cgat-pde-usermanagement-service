package com.mckesson.cgat.pde.usermanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mckesson.cgat.pde.usermanagement.dto.RoleResponseDto;
import com.mckesson.cgat.pde.usermanagement.model.HiloSequenceGenerator;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.PermissionRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	private final PermissionRepository permissionRepository;
	private final RoleRepository roleRepository;

	public RoleServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository2) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository2;
	}

	@Override
	public Role createOrUpdateRole(Role role){
		return roleRepository.save(role);
	}

	@Override
	public Role getRole(String id) throws ResourceNotFoundException {
		Optional<Role> role = roleRepository.findById(id);
		return role.orElseThrow(() -> new ResourceNotFoundException("Role not found for the id:"+id));
	}
	/*
	 * @Override public List<RoleResponseDto> getAllRoles() { List<Role> allRoles =
	 * roleRepository.findAll(); List<Permission> allPermissions =
	 * permissionRepository.findAll(); return role2Utils.mapEntityToDto(allRoles,
	 * allPermissions); }
	 * 
	 * @Override public String createPermission(String name, String label) {
	 * Permission permission = new Permission(); permission.setIsActive(false);
	 * permission.setLabel(label); permission.setName(name); permission.setId(new
	 * HiloSequenceGenerator().getId().toString());
	 * 
	 * Permission save = permissionRepository.save(permission); return
	 * save.getName(); }
	 * 
	 * @Override public String deletePermission(String id) throws
	 * ResourceNotFoundException { Optional<Permission> permission =
	 * permissionRepository.findById(id); if (permission != null &&
	 * !permission.isEmpty()) { permission.get().setIsActive(false);
	 * permissionRepository.save(permission.get()); return
	 * permission.get().getName(); } throw new
	 * ResourceNotFoundException("No Permission found with id : " + id); }
	 * 
	 * @Override public List<Permission> getAllPermissions() { return
	 * permissionRepository.findAll(); }
	 */

}
