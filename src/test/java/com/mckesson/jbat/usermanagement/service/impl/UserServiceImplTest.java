package com.mckesson.jbat.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.UserResponse;
import com.mckesson.cgat.pde.usermanagement.dto.UserStudyResponse;
import com.mckesson.cgat.pde.usermanagement.mapper.Transformer;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.Users;
import com.mckesson.cgat.pde.usermanagement.model.UserProfileAudit;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserActivityAuditRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserProfileAuditRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.service.impl.UserServiceImpl;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.UserAbstractionSyncUtils;
import com.mckesson.jbat.common.util.constant.UserRole;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	UserRepository userRepository;

	@Mock
	RoleRepository roleRepository;

	@Mock
	UserAbstractionSyncUtils userAbstractionSyncUtils;

	@Mock
	UserProfileAuditRepository userProfileAuditRepository;

	@Mock
	UserActivityAuditRepository userActivityAuditRepository;

	@Mock
	Transformer transformer;

	UserService service;

	String AUTH_TOKEN = "Bearer authToken";

	@BeforeEach
	void setUp() {
		service = new UserServiceImpl(userRepository, roleRepository, transformer, userAbstractionSyncUtils,
				userProfileAuditRepository, userActivityAuditRepository);
	}

	@Test
	void findAll_no_filter() {

		List<String> status = null;
		Pageable paging = PageRequest.ofSize(20);
		String query = null;
		List<String> roles = null;

		String statusActive = "ALL";
		Page<Users> mockResponse = getUsers();

		Mockito.when(userRepository.findAllByQueryText(query, statusActive, paging)).thenReturn(mockResponse);

		Page<UserResponse> result = service.findAll(paging, query, roles, status);

		verify(userRepository).findAllByQueryText(query, statusActive, paging);
		assertThat(result).isNotEmpty();
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	@Test
	void findAll_status_filter_only() {

		List<String> status = Arrays.asList("Active");
		Pageable paging = PageRequest.ofSize(20);
		String query = null;
		List<String> roles = null;

		String statusActive = "ACTIVE";
		Page<Users> mockResponse = getUsers();

		Mockito.when(userRepository.findAllByQueryText(query, statusActive, paging)).thenReturn(mockResponse);

		Page<UserResponse> result = service.findAll(paging, query, roles, status);

		verify(userRepository).findAllByQueryText(query, statusActive, paging);
		assertThat(result).isNotEmpty();
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	@Test
	void findAll_status_all_filter_only() {

		List<String> status = Arrays.asList("Active", "InActive");
		Pageable paging = PageRequest.ofSize(20);
		String query = null;
		List<String> roles = null;

		String statusActive = "ALL";
		Page<Users> mockResponse = getUsers();

		Mockito.when(userRepository.findAllByQueryText(query, statusActive, paging)).thenReturn(mockResponse);

		Page<UserResponse> result = service.findAll(paging, query, roles, status);

		verify(userRepository).findAllByQueryText(query, statusActive, paging);
		assertThat(result).isNotEmpty();
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	@Test
	void findAll_role_filter_only() {

		List<String> status = null;
		Pageable paging = PageRequest.ofSize(20);
		String query = null;
		List<String> roles = Arrays.asList("ADMIN");

		String statusActive = "ALL";
		Page<Users> mockResponse = getUsers();

		Mockito.when(userRepository.findAllByQueryTextAndRoles(query, roles, statusActive, paging))
			.thenReturn(mockResponse);

		Page<UserResponse> result = service.findAll(paging, query, roles, status);

		verify(userRepository).findAllByQueryTextAndRoles(query, roles, statusActive, paging);
		assertThat(result).isNotEmpty();
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	@Test
	void findAll_role_unassigned_filter_only() {

		List<String> status = null;
		Pageable paging = PageRequest.ofSize(20);
		String query = null;
		List<String> roles = Arrays.asList("UNASSIGNED");

		String statusActive = "ALL";
		Page<Users> mockResponse = getUsers();

		Mockito.when(userRepository.findAllByQueryTextRoleUnAssigned(query, statusActive, paging))
			.thenReturn(mockResponse);

		Page<UserResponse> result = service.findAll(paging, query, roles, status);

		verify(userRepository).findAllByQueryTextRoleUnAssigned(query, statusActive, paging);
		assertThat(result).isNotEmpty();
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	private Page<Users> getUsers() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "displayName";
		String sortOrder = "asc";
		List<Users> users = new ArrayList<>();
		Users user1 = new Users();
		user1.setDisplayName("User one");
		user1.setEmail("user.one@test.com");
		Users user2 = new Users();
		user2.setDisplayName("User two");
		user2.setEmail("user.two@test.com");
		Users user3 = new Users();
		user3.setDisplayName("User three");
		user3.setEmail("user.three@test.com");
		Users user4 = new Users();
		user4.setDisplayName("User four");
		user4.setEmail("user.four@test.com");
		Users user5 = new Users();
		user5.setDisplayName("User five");
		user5.setEmail("user.five@test.com");
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(users, pageable, users.size());

	}

	@Test
	void filterUsersByRole() {
		List<Users> users = new ArrayList<>();
		Users user1 = new Users();
		user1.setId(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120958"));
		user1.setDisplayName("User one");
		user1.setEmail("user.one@test.com");
		Users user2 = new Users();
		user2.setId(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120957"));
		user2.setDisplayName("User two");
		user2.setEmail("user.two@test.com");
		users.add(user1);
		users.add(user2);
		UserRole role = UserRole.VENDOR_QC;
		List<UUID> userIds = new ArrayList<>();
		userIds.add(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120958"));
		userIds.add(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120957"));
		List<UserStudyResponse> userStudyResponses = new ArrayList<>();
		userStudyResponses.add(new UserStudyResponse("5ec5de42-dffb-11ec-9d64-0242ac120958", "Abi"));
		userStudyResponses.add(new UserStudyResponse("5ec5de42-dffb-11ec-9d64-0242ac120957", "Raj"));
		Mockito.when(userRepository.findUsersByRoles_NameAndIdIn(any(), any())).thenReturn(users);
		List<UserStudyResponse> result = service.getUsersBasedOnRole(role, userIds);
		verify(userRepository).findUsersByRoles_NameAndIdIn(role.name(), userIds);
		assertNotNull(result);
	}

	@Test
	void getUsersToAssignStudy_UsersAssignedSuccessfully() {
		ResponseEntity<String> successResponse = ResponseEntity.ok("Users assigned to study successfully.");
		Mono<ResponseEntity<String>> successMono = Mono.just(successResponse);
		Mockito.when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(any())).thenReturn(successMono);
		Mono<ResponseEntity<String>> resultMono = userAbstractionSyncUtils.syncPatientswithChartAbstraction(AUTH_TOKEN);
		ResponseEntity<String> responseEntity = resultMono.block();
		assertEquals(successResponse, responseEntity);
		assertEquals(200, responseEntity.getStatusCodeValue());
	}

	@Test
	void getUsersToAssignStudy_UsersNotFound() {
		ResponseEntity<String> notFoundResponse = ResponseEntity.ok("No users found for syncing with study");
		Mono<ResponseEntity<String>> successMono = Mono.just(notFoundResponse);
		Mockito.when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(any())).thenReturn(successMono);
		Mono<ResponseEntity<String>> resultMono = userAbstractionSyncUtils
			.syncPatientswithChartAbstraction("authToken");
		ResponseEntity<String> responseEntity = resultMono.block();
		assertEquals(notFoundResponse, responseEntity);
	}

	@Test
	void getUsersToAssignStudy_UsersNotAssignedSuccessfully() {
		ResponseEntity<String> errorResponse = ResponseEntity.status(INTERNAL_SERVER_ERROR)
			.body("Error assigning users to study.");
		Mono<ResponseEntity<String>> errorMono = Mono.just(errorResponse);
		Mockito.when(userAbstractionSyncUtils.syncPatientswithChartAbstraction(any())).thenReturn(errorMono);
		Mono<ResponseEntity<String>> resultMono = userAbstractionSyncUtils
			.syncPatientswithChartAbstraction("authToken");
		ResponseEntity<String> responseEntity = resultMono.block();
		assertEquals(errorResponse, responseEntity);
	}

	@Test
	void getUser_UserExists() throws ResourceNotFoundException {
		UUID userId = UUID.randomUUID();
		Users mockUser = new Users();
		mockUser.setId(userId);
		mockUser.setDisplayName("John Doe");
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		Users retrievedUser = service.getUser(userId.toString());
		verify(userRepository).findById(userId);
		assertEquals(mockUser, retrievedUser);
	}

	@Test
	void getUser_UserNotFound() {
		UUID userId = UUID.randomUUID();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			service.getUser(userId.toString());
		});
		verify(userRepository).findById(userId);
	}

	@Test
	public void testAssignRole_Success() throws Exception {
		Users user = new Users();
		user.setId(UUID.fromString("5ec5de42-dffb-11ec-9d64-0242ac120958"));
		user.setDisplayName("User one");
		user.setEmail("user.one@test.com");
		Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
		Role role = new Role();
		role.setName("SYSTEM_ADMIN");
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		user.setRoles(roles);
		Mockito.when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
		service.assignRole("5ec5de42-dffb-11ec-9d64-0242ac120958", "SYSTEM_ADMIN");
		verify(userRepository, times(1)).save(user);
		verify(userProfileAuditRepository, times(1)).save(any(UserProfileAudit.class));
		assertThat(user.getRoles()).contains(role);
		assertThat(user.getRoles().get(0).getName()).isEqualTo("SYSTEM_ADMIN");
	}

	@Test
	public void testAssignRole_UserNotFound() {
		Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			service.assignRole("5ec5de42-dffb-11ec-9d64-0242ac120950", "SYSTEM_ADMIN");
		});
		verify(userRepository, never()).save(any());
		verify(userProfileAuditRepository, never()).save(any(UserProfileAudit.class));

	}

	@Test
	public void testAssignRole_RoleNotFound() {
		Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(new Users()));
		Mockito.when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> {
			service.assignRole("5ec5de42-dffb-11ec-9d64-0242ac120958", "ADMIN");
		});
		verify(userRepository, never()).save(any());
		verify(userProfileAuditRepository, never()).save(any(UserProfileAudit.class));

	}

}