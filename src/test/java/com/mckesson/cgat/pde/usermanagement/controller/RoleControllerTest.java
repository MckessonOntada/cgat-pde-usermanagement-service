package com.mckesson.cgat.pde.usermanagement.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.service.RoleService;
import com.mckesson.cgat.pde.usermanagement.util.TestUtils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@WebMvcTest(RoleController.class)
@ContextConfiguration(classes = { RoleController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class RoleControllerTest {

	@MockBean
	private RoleService roleService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Practice practice;

	private Role role;

	@BeforeEach
	public void beforeEach() {
		practice = Practice.builder()
			.id(1L)
			.address(new Address("address1", "", "city", "state", "12345"))
			.contact(new Contact("email@gmail.com", "+1", "1212121212", ""))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("logo")
			.name("DefaultName")
			.build();

		role = Role.builder()
			.id(1L)
			.name("ORG ADMIN")
			.isActive(Boolean.TRUE)
			.permissions(Set.of(Permission.values()))
			.practiceId(practice)
			.build();
	}

	@Test
	@DisplayName("Test : Create Role")
	void createRoleTest() throws Exception {
		String practiceId = "1";
		String roleJsonString = objectMapper.writeValueAsString(role);
		when(roleService.createOrUpdateRole(any(Role.class), any(String.class))).thenReturn(role);

		mockMvc
			.perform(post("/practices/{practiceId}/roles", practiceId).content(roleJsonString)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andDo(print())
			.andReturn();
	}

	@Test
	@DisplayName("Test : Get Role")
	void getRoleTest() throws Exception {
		Long roleId = 1L;
		String practiceId = "1";

		when(roleService.getRole(anyLong())).thenReturn(role);
		mockMvc
			.perform(get("/practices/{practiceId}/roles/{roleId}", practiceId, roleId)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("Test: Delete Role API")
	@Test
	void deleteRoleTest() throws Exception {
		String practiceId = "1";
		Long roleId = 1L;

		doNothing().when(roleService).deleteRole(roleId);

		mockMvc
			.perform(delete("/practices/{practiceId}/roles/{roleId}", practiceId, roleId)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Test ResourceNotFoundException : Get Role")
	void getRole_ExceptionTest() throws Exception {
		String roleId = "1";
		String practiceId = "1";

		when(roleService.getRole(1L)).thenThrow(new ResourceNotFoundException("Role not found with id : " + roleId));
		mockMvc
			.perform(get("/practices/{practiceId}/roles/{roleId}", practiceId, roleId)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
	}

	@DisplayName("Test : FindAll Role")
	@Test
	void findAllRoleTest() throws Exception {
		Long practiceId = 1L;
		String pageNo = "0";
		String pageSize = "20";
		String sortBy = "name";
		String sortOrder = "asc";
		String query = "";
		String statusActive = "ALL";
		LocalDate startDate = TestUtils.parseLocalDate("10/11/2024", CustomUtils.API_DATE_FORMAT);
		LocalDate endDate = TestUtils.parseLocalDate("13/11/2014", CustomUtils.API_DATE_FORMAT);
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		Page<Role> mockResponse = getRoles();

		when(roleService.findAll(paging, query, practiceId.toString(), List.of(statusActive), startDate, endDate))
			.thenReturn(mockResponse);

		mockMvc
			.perform(get("/practices/{practiceId}/roles", practiceId).param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("sortBy", sortBy)
				.param("sortOrder", sortOrder)
				.param("query", query)
				.param("status", statusActive)
				.param("startDate", "2024-11-10")
				.param("endDate", "2024-11-13")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

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
