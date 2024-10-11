package com.mckesson.jbat.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.controller.UserController;
import com.mckesson.jbat.common.util.constant.UserRole;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.cgat.pde.usermanagement.dto.UserRoleRequest;
import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UsersRoleBulkUploadResponse;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.UserAbstractionSyncUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractTest {

	@InjectMocks
	private UserController controller;

	@Mock
	private UserService userService;

	@Mock
	private UserAbstractionSyncUtils userAbstractionSyncUtils;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void getAllUsers() throws Exception {

		ResultActions response = mockMvc.perform(get("/users").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		response.andExpect(status().isOk());
	}

	@Test
	void filterUsersByRole() throws Exception {
		UserRole role = UserRole.VENDOR_QC;
		List<UUID> userIds = new ArrayList<>();
		userIds.add(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120958"));
		userIds.add(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120957"));
		List<UserStudyResponse> userStudyResponses = new ArrayList<>();
		userStudyResponses.add(new UserStudyResponse("5ec5de42-dffb-11ec-9d64-0242ac120958", "Abi"));
		userStudyResponses.add(new UserStudyResponse("5ec5de42-dffb-11ec-9d64-0242ac120958", "Abi"));
		userStudyResponses.add(new UserStudyResponse("5ec5de42-dffb-11ec-9d64-0242ac120957", "Raj"));
		when(userService.getUsersBasedOnRole(role, userIds)).thenReturn(userStudyResponses);
		ResultActions result = mockMvc.perform(post("/users/filterByRole").param("role", role.name())
			.contentType(MediaType.APPLICATION_JSON)
			.content("[\"5ec5de42-dffb-11ec-9d64-0242ac120958\", \"5ec5de42-dffb-11ec-9d64-0242ac120957\"]"));
		result.andReturn();
		result.andExpect(status().isOk());
	}

	@Test
	public void getUsersToAssignStudy_UsersAssignedSuccessfully() throws Exception {
		ResponseEntity<String> successResponse = ResponseEntity.ok("Users assigned to study successfully.");
		when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(anyString()))
			.thenReturn(Mono.defer(() -> Mono.just(successResponse)));
		ResultActions result = mockMvc
			.perform(post("/users/assign/study").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isOk());
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result.andReturn().getAsyncResult();
		String responseBody = responseEntity.getBody();
		assertEquals("Users assigned to study successfully.", responseBody);
	}

	@Test
	public void getUsersToAssignStudy_UsersNotFound() throws Exception {
		ResponseEntity<String> successResponse = ResponseEntity.ok("No users found for syncing with study");
		when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(anyString()))
			.thenReturn(Mono.defer(() -> Mono.just(successResponse)));
		ResultActions result = mockMvc
			.perform(post("/users/assign/study").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isOk());
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result.andReturn().getAsyncResult();
		String responseBody = responseEntity.getBody();
		assertEquals("No users found for syncing with study", responseBody);
	}

	@Test
	public void getUsersToAssignStudy_UsersNotAssignedSuccessfully() throws Exception {
		ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("Error assigning users to study.");
		when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(anyString()))
			.thenReturn(Mono.defer(() -> Mono.just(errorResponse)));
		ResultActions result = mockMvc
			.perform(post("/users/assign/study").header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isOk());
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result.andReturn().getAsyncResult();
		String responseBody = responseEntity.getBody();
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
		assertEquals("Error assigning users to study.", responseBody);
	}

	@Test
	public void getUserById() throws Exception {
		Users sampleUser = new Users();
		sampleUser.setId(UUID.fromString("5e4988f0-130a-4c7f-89a1-af94a3cc02fd"));
		sampleUser.setDisplayName("User1");
		sampleUser.setIsActive(true);
		sampleUser.setEmail("john@yopmail.com");
		when(userService.getUser(anyString())).thenReturn(sampleUser);
		ResultActions result = mockMvc.perform(get("/users/{userId}", "5e4988f0-130a-4c7f-89a1-af94a3cc02fd")
			.header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isOk());
		String content = result.andReturn().getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();
		Users responseUser = objectMapper.readValue(content, Users.class);
		assertEquals(sampleUser.getId(), responseUser.getId());
		assertEquals(sampleUser.getDisplayName(), responseUser.getDisplayName());
		assertEquals(sampleUser.getEmail(), responseUser.getEmail());
		verify(userService, times(1)).getUser(sampleUser.getId().toString());
	}

	@Test
	public void getUser_UserNotFound() throws Exception {
		when(userService.getUser(anyString())).thenThrow(new ResourceNotFoundException("User Not Found"));
		ResultActions result = mockMvc.perform(get("/users/{userId}", "5e4988f0-130a-4c7f-89a1-af94a3cc02fd")
			.header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isNotFound());
		Exception ex = result.andReturn().getResolvedException();
		assertNotNull(ex);
		assertTrue(ex.getMessage().contains("User Not Found"));
		verify(userService, times(1)).getUser("5e4988f0-130a-4c7f-89a1-af94a3cc02fd");
	}

	@Test
	void assignRole_RoleAssignedSuccessfully() throws Exception {
		String userId = "5e4988f0-130a-4c7f-89a1-af94a3cc02fd";
		String roleName = "SYSTEM_ADMIN";
		Users user = new Users();
		user.setId(UUID.fromString(userId));
		Role role = new Role();
		role.setName(roleName);
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		when(userService.assignRole(any(), any())).thenReturn(user);

		ResultActions result = mockMvc.perform(post("/users/{userId}/role", userId).param("role", roleName)
			.header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isOk());
		MvcResult resd = result.andReturn();
		verify(userService, times(1)).assignRole(userId, roleName);
		result.andExpect(jsonPath("$.id").value(userId));
	}

	@Test
	public void assignRole_UserNotFound() throws Exception {
		when(userService.assignRole(anyString(), anyString()))
			.thenThrow(new ResourceNotFoundException("User Not Found"));
		ResultActions result = mockMvc
			.perform(post("/users/{userId}/role", "5e4988f0-130a-4c7f-89a1-af94a3cc02fd").param("role", "SYSTEM_ADMIN")
				.header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isNotFound());
		Exception ex = result.andReturn().getResolvedException();
		assertNotNull(ex);
		assertTrue(ex.getMessage().contains("User Not Found"));
		verify(userService, times(1)).assignRole("5e4988f0-130a-4c7f-89a1-af94a3cc02fd", "SYSTEM_ADMIN");
		;
	}

	@Test
	public void getUser_RoleNotFound() throws Exception {
		when(userService.assignRole(anyString(), anyString()))
			.thenThrow(new ResourceNotFoundException("Role Not Found"));
		ResultActions result = mockMvc
			.perform(post("/users/{userId}/role", "5e4988f0-130a-4c7f-89a1-af94a3cc02fd").param("role", "ADMIN")
				.header(HttpHeaders.AUTHORIZATION, AUTH_TOKEN));
		result.andExpect(status().isNotFound());
		Exception ex = result.andReturn().getResolvedException();
		assertNotNull(ex);
		assertTrue(ex.getMessage().contains("Role Not Found"));
		verify(userService, times(1)).assignRole("5e4988f0-130a-4c7f-89a1-af94a3cc02fd", "ADMIN");
	}

	@Test
	public void assignRoleForAll_Success() throws Exception {
		Role role1 = new Role();
		role1.setName("SYSTEM_ADMIN");
		role1.setLabel("Admin");
		Role role2 = new Role();
		role2.setName("VENDOR_QC");
		role2.setLabel("Vendor Qc");
		List<String> roles = new ArrayList<>();
		roles.add(role1.getName().toString());
		roles.add(role2.getName().toString());
		UserRoleRequest userRoleRequest1 = new UserRoleRequest("user1", "user1@yopmail.com", roles, "Active");
		UserRoleRequest userRoleRequest2 = new UserRoleRequest("user2", "user2@yopmail.com", roles, "Active");
		List<UserRoleRequest> userRoleRequests = new ArrayList<>();
		userRoleRequests.add(userRoleRequest1);
		userRoleRequests.add(userRoleRequest2);
		UsersRoleBulkUploadResponse mockResponse = new UsersRoleBulkUploadResponse();
		mockResponse.setHasErrors(true);
		mockResponse.setRecords(userRoleRequests);
		when(userService.assignRoleforAllUsers(any(MultipartFile.class), anyBoolean(), anyString()))
			.thenReturn(mockResponse);
		MockMultipartFile file = new MockMultipartFile("userRoleMappingCSV", "users.csv", "text/csv",
				"username,role\nuser1,SYSTEM_ADMIN\nuser2,VENDOR_QC\n".getBytes());
		ResultActions result = mockMvc
			.perform(multipart("/users/roles").file(file)
				.param("preview", "false")
				.header(HttpHeaders.AUTHORIZATION, "Bearer authToken"))
			.andExpect(status().isOk());
		result.andExpect(jsonPath("$.hasErrors").value(true)).andExpect(jsonPath("$.records.length()").value(2));
	}

}