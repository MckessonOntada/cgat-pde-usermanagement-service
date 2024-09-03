package com.mckesson.jbat.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.ReferenceDataService;
import com.mckesson.cgat.pde.usermanagement.service.impl.ReferenceDataServiceImpl;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.dto.ReferenceDataResponse;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.ReferenceDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;


@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceImplTest {
    @Mock
    RoleRepository roleRepository;

    ReferenceDataService service;

    @BeforeEach
    void setUp() {
        service = new ReferenceDataServiceImpl(roleRepository);
    }

    @Test
    public void testFindAll() {
        Role role1 = new Role();
        role1.setName("SYSTEM_ADMIN");
        role1.setLabel("System Admin");
        role1.setShortLabel("ADMIN");
        Role role2 = new Role();
        role2.setName("STUDY_MANAGER");
        role2.setLabel("Study Manager");
        role2.setShortLabel("STUDY MGR");
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        ReferenceDataResponse result = service.findAll();
        Assertions.assertNotNull(result);
        assertEquals(roles.size(), result.getRoles().size());
    }

    @Test
    public void testGetRoleByname_WhenRoleExists() throws ResourceNotFoundException {
        Role mockRole = new Role();
        mockRole.setName("SYSTEM_ADMIN");
        Mockito.when(roleRepository.findByName("SYSTEM_ADMIN")).thenReturn(Optional.of(mockRole));
        Role resultRole = service.getRoleByname("SYSTEM_ADMIN");
        assertEquals(mockRole, resultRole);
    }

    @Test
    public void testGetRoleByname_RoleDoesNotExist() {
        Mockito.when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getRoleByname("ADMIN"));
        assertEquals("Role not found :ADMIN", exception.getMessage());
    }

    @Test
    public void testGetAllRolesAndPermissions() {
        Role role1 = new Role();
        role1.setName("SYSTEM_ADMIN");
        role1.setLabel("System Admin");
        role1.setShortLabel("ADMIN");
        Role role2 = new Role();
        role2.setName("STUDY_MANAGER");
        role2.setLabel("Study Manager");
        role2.setShortLabel("STUDY MGR");
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        List<Role> resultRoles = service.getAllRolesAndPermissions();
        assertEquals(roles, resultRoles);
    }

}
