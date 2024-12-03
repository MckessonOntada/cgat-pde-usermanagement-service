package com.mckesson.cgat.pde.usermanagement.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.dto.RolePermissionsPatchDTO;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.model.UserType;
import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.cgat.pde.usermanagement.util.TestUtils;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import com.mckesson.cgat.pde.usermanagement.utils.UserCurrentRequestContext;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = { UserController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)

class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private AccountService accountService;

	@InjectMocks
	private UserController userController;

	private User user;

	@Autowired
	private ObjectMapper objectMapper;

	String apiUrl = "/practices/{practiceId}/users";

	@BeforeEach
	public void setUp() {
		user = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("John", "Doe", "doe"))
			.contact(Contact.builder().email("john.doe1@example.com").phone("1234567899").fax("9876543211").build())
			.address(Address.builder()

				.address1("122 Main St")
				.address2("Suite 100")
				.city("New York ")
				.state("NY")
				.zip("10000")
				.build())
			.permissions(null)
			.isActive(true)
			.build();
		setupPrincipal();
	}

	private void setupPrincipal() {
		LoggedInUserDetails loggedInUser = LoggedInUserDetails.builder()
			.id("1")
			.name(Name.builder().firstName("John").lastName("Doe").build())
			.role(Role.builder().name("System Admin").build())
			.permissions(new HashSet(Arrays.asList(Permission.values())))
			.practiceId(1)
			.userName("John Doe")
			.build();
		List<String> roleNames = new ArrayList<>();
		roleNames.add(loggedInUser.getRole().getName());
		Collection<? extends GrantedAuthority> authorities = roleNames.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
		UserCurrentRequestContext principal = new UserCurrentRequestContext(loggedInUser.getUserName(), "", authorities,
				loggedInUser);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
		OrganizationContext.setCurrentOrgId(1L);
	}

	@AfterEach
	public void tearDown() {
		OrganizationContext.clear();
	}

	@DisplayName("CreateUserTest")
	@Test
	void createUserTest() throws Exception {
		String objectString = objectMapper.writeValueAsString(user);
		when(userService.createUser(any(User.class))).thenReturn(user);
		mockMvc
			.perform(post(apiUrl, user.getPracticeId()).param("userType", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectString))
			.andExpect(status().isOk())
			// .andDo(print())
			.andReturn();

	}

	@DisplayName("ValidationError :CreateUserTest")
	@Test
	void createUser_validationErrorTest() throws Exception {
		user = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("", "Doe", "doe"))
			.contact(Contact.builder().email("john.doe2@example.com").phone("1234567891").fax("9876543212").build())
			.address(Address.builder()
				.address1("124 Main St")
				.address2("Suite 200")
				.city("New York2")
				.state("NY")
				.zip("10002")
				.build())
			.isActive(true)
			.build();

		ResultActions result = mockMvc
			.perform(post("/practices/{practiceId}/users", user.getPracticeId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
			.andExpect(status().isBadRequest());
		assertNotNull(result);
	}

	@DisplayName("UpdateUserTest")
	@Test
	void updateUserTest() throws Exception {
		User requestdUser = User.builder()
			.id(null)
			.practiceId(200L)
			.name(new Name("John", "Doe", "doe"))
			.contact(Contact.builder()
				.email("john.doe3@example.com")
				.phone("1234567893")
				.fax("9876543213")
				.countryCode("123")
				.build())
			.address(Address.builder()
				.address1("1234 Main St")
				.address2("Suite 300")
				.city("New York")
				.state("NY")
				.zip("10003")
				.build())
			.isActive(true)
			.build();

		User updatedUser = User.builder()
			.id(user.getId())
			.practiceId(200L)
			.name(new Name("John", "updated", "doe"))
			.contact(Contact.builder()
				.email("john.doe@example.com")
				.phone("1234567890")
				.fax("9876543210")
				.countryCode("1234")
				.build())
			.address(Address.builder()
				.address1("123 Main St")
				.address2("Suite 500")
				.city("New York")
				.state("NY")
				.zip("10001")
				.build())
			.isActive(true)
			.build();

		when(userService.updateUser(user.getId(), requestdUser)).thenReturn(updatedUser);
		ResultActions result = mockMvc
			.perform(patch(apiUrl + "/{userId}", user.getPracticeId(), user.getId()).param("userType", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestdUser)))
			.andExpect(status().isOk());
		assertNotNull(result);
	}

	@DisplayName("GetUserTest")
	@Test
	void getUserTest() throws Exception {
		when(userService.getUser(user.getId())).thenReturn(user);
		mockMvc
			.perform(get("/practices/{practiceId}/users/{userId}", user.getPracticeId(), user.getId())
				.param("userType", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			// .andDo(print())
			.andExpect(status().isOk());

	}

	@DisplayName("UserNotFoundException :UpdateUser")
	@Test
	void userNotFoundTest() throws Exception {
		User updatedUser = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("John", "Doe", "doe"))
			.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("123 Main St")
				.address2("Suite 500")
				.city("New York")
				.state("NY")
				.zip("10001")
				.build())
			.isActive(true)
			.build();
		when(userService.updateUser(user.getId(), updatedUser))
			.thenThrow(new ResourceNotFoundException("User not found"));

		mockMvc
			.perform(put("/{userId}", user.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser)))
			.andExpect(status().isNotFound());
	}

	@DisplayName("DeleteUserTest")
	@Test
	void deleteUserTest() throws Exception {
		doNothing().when(userService).deleteUser(user.getId());
		mockMvc
			.perform(delete(apiUrl + "/{userId}", user.getPracticeId(), user.getId()).param("userType", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk());

	}

	@DisplayName("UserNotFoundException :DeleteUser")
	@Test
	void deleteUser_userNotFoundTest() throws Exception {
		doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(user.getId());
		mockMvc.perform(delete("/{userId}", user.getId())).andExpect(status().isNotFound());
	}

	@DisplayName("FindAllTest")
	@Test
	void findAllTest() throws Exception {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "first_name";
		String sortOrder = "ASC";
		String statusActive = "ALL";
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		List<User> users = getUserList();
		PageImpl<User> userPage = new PageImpl<>(users, pageable, users.size());
		LocalDate startDate = TestUtils.parseLocalDate("10/11/2024", CustomUtils.API_DATE_FORMAT);
		LocalDate endDate = TestUtils.parseLocalDate("18/11/2024", CustomUtils.API_DATE_FORMAT);

		when(userService.findAll(any(PageRequest.class), anyString(), anyList(), anyLong(), any(LocalDate.class),
				any(LocalDate.class)))
			.thenReturn(userPage);

		ResultActions result = mockMvc
			.perform(get("/practices/{practiceId}/users", user.getPracticeId()).param("userType", "ADMIN")
				.param("pageNo", "0")
				.param("pageSize", "20")
				.param("sortBy", sortBy)
				.param("sortOrder", sortOrder)
				.param("userType", "ADMIN")
				.param("query", "")
				.param("status", statusActive)
				.param("startDate", "2024-11-10")
				.param("endDate", "2024-11-18")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertNotNull(result);
		verify(userService, times(1)).findAll(pageable, "", List.of(statusActive), 1L, startDate, endDate);
	}

	@DisplayName("FindAllTestForDesc")
	@Test
	void findAllTestForDesc() throws Exception {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "first_name";
		String sortOrder = "DESC";
		String statusActive = "ALL";
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		List<User> users = getUserList();
		PageImpl<User> userPage = new PageImpl<>(users, pageable, users.size());
		LocalDate startDate = TestUtils.parseLocalDate("10/11/2024", CustomUtils.API_DATE_FORMAT);
		LocalDate endDate = TestUtils.parseLocalDate("18/11/2024", CustomUtils.API_DATE_FORMAT);

		when(userService.findAll(any(Pageable.class), anyString(), anyList(), anyLong(), any(LocalDate.class),
				any(LocalDate.class)))
			.thenReturn(userPage);

		ResultActions result = mockMvc
			.perform(get(apiUrl, user.getPracticeId()).param("userType", "ADMIN")
				.param("pageNo", "0")
				.param("pageSize", "20")
				.param("sortBy", "first_name")
				.param("sortOrder", "desc")
				.param("query", "")
				.param("status", statusActive)
				.param("startDate", "2024-11-10")
				.param("endDate", "2024-11-18")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertNotNull(result);
		verify(userService).findAll(pageable, "", List.of(statusActive), 1L, startDate, endDate);
	}

	@DisplayName("LinkUserToAccountsTest")
	@Test
	void linkUserToAccountsTest() throws Exception {
		List<Long> accountIds = Arrays.asList(101L, 102L);
		when(userService.linkUserToAccounts(user.getId(), accountIds)).thenReturn(user);
		ResultActions result = mockMvc
			.perform(patch(apiUrl + "/{userId}/accounts", user.getPracticeId(), user.getId()).param("userType", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountIds)))
			.andExpect(status().isOk());
		assertNotNull(result);
	}

	@DisplayName("LinkUserToRoleTest")
	@Test
	void linkUserToRoles_permissionsTest() throws Exception {
		Long roleId = 101L;
		List<Long> accountIds = List.of(1L, 2L);
		RolePermissionsPatchDTO rolePermissions = new RolePermissionsPatchDTO(roleId, user.getPermissions());
		when(userService.linkUserToRoleAndPermissions(user.getId(), UserType.ADMIN, rolePermissions)).thenReturn(user);
		ResultActions result = mockMvc
			.perform(patch(apiUrl + "/{userId}/roles-permissions", user.getPracticeId(), user.getId())
				.param("userType", "ADMIN")
				.param("roleId", roleId.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(rolePermissions)))

			.andExpect(status().isOk());
		assertNotNull(result);
	}

	private List<User> getUserList() {

		User user1 = User.builder()
			.id(1L)
			.practiceId(100L)
			.name(new Name("user1_firstName", "user1_middleName", "user1_lastName"))
			.contact(Contact.builder().email("user1@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("10 Main Street")
				.address2("Suite 500")
				.city("New York")
				.state("NY")
				.zip("10001")
				.build())
			.isActive(true)
			.build();

		User user2 = User.builder()
			.id(2L)
			.practiceId(101L)
			.name(new Name("user2_firstName", "user2_middleName", "user2_lastName"))
			.contact(Contact.builder().email("user2@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("11 Main Street")
				.address2("Suite 501")
				.city("New York")
				.state("NY")
				.zip("10002")
				.build())
			.isActive(true)
			.build();

		User user3 = User.builder()
			.id(3L)
			.practiceId(102L)
			.name(new Name("user3_firstName", "user3_middleName", "user3_lastName"))
			.contact(Contact.builder().email("user3@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("12 Main Street")
				.address2("Suite 503")
				.city("New York")
				.state("NY")
				.zip("10003")
				.build())
			.isActive(true)
			.build();

		User user4 = User.builder()
			.id(4L)
			.practiceId(104L)
			.name(new Name("user4_firstName", "user4_middleName", "user4_lastName"))
			.contact(Contact.builder().email("user4@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("13 Main Street")
				.address2("Suite 504")
				.city("New York")
				.state("NY")
				.zip("10004")
				.build())
			.isActive(true)
			.build();

		User user5 = User.builder()
			.id(5L)
			.practiceId(105L)
			.name(new Name("user5_firstName", "user5_middleName", "user5_lastName"))
			.contact(Contact.builder().email("user5@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("15 Main Street")
				.address2("Suite 505")
				.city("New York")
				.state("NY")
				.zip("10005")
				.build())
			.isActive(true)
			.build();
		return Arrays.asList(user1, user2, user3, user4, user5);

	}

}
