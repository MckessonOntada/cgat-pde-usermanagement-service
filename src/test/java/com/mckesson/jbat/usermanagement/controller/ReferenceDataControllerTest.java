package com.mckesson.jbat.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.controller.ReferenceDataController;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.service.ReferenceDataService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReferenceDataControllerTest extends AbstractTest {

	@InjectMocks
	private ReferenceDataController controller;

	@Mock
	private ReferenceDataService service;

	@Mock
	private RoleRepository roleRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void allStaticReferenceData() throws Exception {

		ResultActions response = mockMvc.perform(get("/reference").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		response.andExpect(status().isOk());
	}

	@Test
	public void testGetAllRolesAndPermissions_RoleNotFound() throws Exception {
		when(service.getRoleByname(anyString())).thenThrow(new ResourceNotFoundException("Role not found"));
		ResultActions result = mockMvc.perform(
				get("/reference/role/{rolename}/permissions", "ADMIN").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
					.contentType(MediaType.APPLICATION_JSON));
		result.andExpect(status().isNotFound());
		Exception ex = result.andReturn().getResolvedException();
		assertNotNull(ex);
	}

	@Test
	public void testGetAllRolesAndPermissions() throws Exception {
		Role role = new Role();
		role.setName("test");
		role.setRoleType("Vendor QC");
		role.setIsActive(true);
		role.setLabel("Test");
		String roleName = "VENDOR_QC";
		when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
		ResultActions result = mockMvc.perform(
				get("/reference/role/{rolename}/permissions", "VENDOR_QC").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
					.contentType(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
	}

}