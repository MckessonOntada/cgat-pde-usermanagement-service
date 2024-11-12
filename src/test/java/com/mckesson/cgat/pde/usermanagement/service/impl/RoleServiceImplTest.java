package com.mckesson.cgat.pde.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mckesson.cgat.pde.usermanagement.AbstractTest;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

class RoleServiceImplTest extends AbstractTest {

	@Mock
	RoleRepository roleRepository;

	@Mock
	PracticeRepository practiceRepository;

	@InjectMocks
	RoleServiceImpl roleService;

	private Role role;

	private Practice practice;

	@BeforeEach
	public void beforeEach() {
		practice = Practice.builder()
			.id(1L)
			.address(new Address("address1", "address2", "city", "state", "12345"))
			.contact(new Contact("email@gmail.com", "1212121212", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("logo")
			.name("DefaultName")
			.build();

		role = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
	}

	@DisplayName("Test : Create Role")
	@Test
	void createRoleTest() throws IllegalStateException, IOException, ResourceNotFoundException {
		String practiceId = "1";

		when(practiceRepository.findById(Long.valueOf(practiceId))).thenReturn(Optional.of(practice));
		when(roleRepository.save(any())).thenReturn(role);
		assertEquals(role, roleService.createOrUpdateRole(role, practiceId));
		verify(roleRepository, times(1)).save(role);
	}

	@DisplayName("Test : Update Role")
	@Test
	void updateRoleTest() throws IllegalStateException, IOException, ResourceNotFoundException {
		String practiceId = "1";

		when(practiceRepository.findById(Long.valueOf(practiceId))).thenReturn(Optional.of(practice));
		when(roleRepository.save(any())).thenReturn(role);
		assertEquals(role, roleService.createOrUpdateRole(role, practiceId));
		verify(roleRepository, times(1)).save(role);
	}

	@DisplayName("Test : Get Role")
	@Test
	void getRoleTest() throws ResourceNotFoundException {
		Long roleId = 1L;
		when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
		assertEquals(role, roleService.getRole(roleId.toString()));
		verify(roleRepository, times(1)).findById(roleId);
	}

	@DisplayName("Test ResourceNotFoundException : Get Role")
	@Test
	void getRole_ExceptionTest() {
		Long roleId = 1L;
		when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> roleService.getRole(roleId.toString()));
	}

	@DisplayName("Test : FindAll Roles")
	@Test
	void findAll_role_filter_Test() {
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		String query = null;
		Long practiceId = 1L;
		String statusActive = "ALL";
		Page<Role> mockResponse = getRoles();

		when(roleRepository.findAllByQueryText(query, statusActive, practiceId, paging)).thenReturn(mockResponse);
		Page<Role> result = roleService.findAll(paging, query, practiceId.toString(), List.of(statusActive));
		verify(roleRepository, times(1)).findAllByQueryText(query, statusActive, practiceId, paging);
		assertTrue(result.getSize() == mockResponse.getSize());
		assertIterableEquals(mockResponse, result);
	}

	private Page<Role> getRoles() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<Role> roles = new ArrayList<>();
		Role role1 = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
		Role role2 = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
		Role role3 = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
		Role role4 = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
		Role role5 = Role.builder()
			.id(1L)
			.name("ADMIN_ROLE")
			.description("Admin role")
			.isActive(Boolean.TRUE)
			.permissions(null)
			.practiceId(practice)
			.build();
		roles.add(role1);
		roles.add(role2);
		roles.add(role3);
		roles.add(role4);
		roles.add(role5);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(roles, pageable, roles.size());
	}

}
