package com.mckesson.cgat.pde.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mckesson.cgat.pde.usermanagement.model.Account;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Permission;
import com.mckesson.cgat.pde.usermanagement.model.Role;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.repository.AccountRepository;
import com.mckesson.cgat.pde.usermanagement.repository.RoleRepository;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;

@SpringBootTest(classes = UserServiceImplTest.class)
class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private RoleRepository roleRepository;

	private User user;

	@BeforeEach
	public void setUp() {
		Set<Permission> permissions = new HashSet<>(Set.of(new Permission(123L, "READ", "read", Boolean.TRUE)));
		user = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("John", "Doe", "doe", "Mr"))
			.permissions(permissions)
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
	}

	@DisplayName("CreateUser ")
	@Test
	void createUserTest() {
		when(userRepository.save(user)).thenReturn(user);
		assertEquals(user, userService.createUser(user));
		verify(userRepository, times(1)).save(user);
	}

	@DisplayName("GetUser")
	@Test
	void getUserByIdTest() throws ResourceNotFoundException {
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		assertNotNull(userService.getUser(user.getId()));
		assertEquals(user, userService.getUser(user.getId()));

	}

	@DisplayName("UpdateUser")
	@Test
	void updateUserTest() throws ResourceNotFoundException {

		User existingUser = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("ram", "kumar", "kumar", "Mr"))
			.contact(Contact.builder().email("ram.kumar@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("12 Main Street")
				.address2("Suite 501")
				.city("New York")
				.state("NY")
				.zip("10001")
				.build())
			.isActive(true)
			.build();

		User updateUser = User.builder()
			.id(1L)
			.practiceId(300L)
			.name(new Name("neeraj", "kumar", "kumar", "Mr"))
			.contact(Contact.builder().email("neeraj.kumar@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("15 Main Street")
				.address2("Suite 502")
				.city("New York")
				.state("NY")
				.zip("10002")
				.build())
			.isActive(true)
			.build();

		when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenReturn(updateUser);

		User result = userService.updateUser(existingUser.getId(), updateUser);

		assertNotNull(result);
		assertEquals(updateUser.getName(), result.getName());
		assertEquals(updateUser.getContact().getEmail(), result.getContact().getEmail());
		verify(userRepository).findById(existingUser.getId());
		verify(userRepository).save(updateUser);
	}

	@DisplayName("DeleteUser")
	@Test
	void deleteUserTest() throws ResourceNotFoundException {
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		userService.deleteUser(user.getId());
		assertFalse(user.getIsActive());
		verify(userRepository, times(1)).save(user);
	}

	@DisplayName("UserNotFoundException : DeleteUser")
	@Test
	void deleteUser_userNotFoundTest() {
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(user.getId()));
		verify(userRepository, never()).save(any(User.class));
	}

	@DisplayName("FindAllUser")
	@Test
	void findAllWithPageableTest() {
		Page<User> userPage = getUserList();
		Pageable paging = PageRequest.of(0, 20, Sort.by("first_name").ascending());
		when(userRepository.findAllByQueryText(null, "ALL", paging, 1)).thenReturn(userPage);
		Page<User> result = userService.findAll(paging, null, List.of("ALL"), "1");
		assertNotNull(result);
		assertEquals(userPage, result);
		verify(userRepository, times(1)).findAllByQueryText(null, "ALL", paging, 1);
	}

	@DisplayName("UserNotFoundException : GetUser")
	@Test
	void getUserById_userNotFoundException() {
		Long userId = 123L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> userService.getUser(userId));

	}

	@DisplayName("LinkUserToAccounts")
	@Test
	void linkUserToAccountsTest() throws ResourceNotFoundException {
		List<Long> newAccountIds = Arrays.asList(200L, 201L);

		Account oldAccount = Account.builder()
			.id(100L)
			.dcId("100")
			.practiceId(100L)
			.dcName("name")
			.name("acc_name")
			.isActive(true)
			.user(null)
			.build();
		oldAccount.setId(100L);
		user.setAccounts(new HashSet<>(Arrays.asList(oldAccount)));

		Account account1 = Account.builder()
			.id(200L)
			.dcId("200")
			.practiceId(200L)
			.dcName("name")
			.name("acc1_name")
			.isActive(true)
			.user(null)
			.build();

		Account account2 = Account.builder()
			.id(201L)
			.dcId("201")
			.practiceId(201L)
			.dcName("name")
			.name("acc2_name")
			.isActive(true)
			.user(null)
			.build();

		List<Account> newAccounts = Arrays.asList(account1, account2);

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(accountRepository.findAllById(newAccountIds)).thenReturn(newAccounts);
		when(userRepository.save(any(User.class))).thenReturn(user);

		User result = userService.linkUserToAccounts(user.getId(), newAccountIds);

		assertNotNull(result);
		assertEquals(2, result.getAccounts().size());
		assertTrue(result.getAccounts().containsAll(newAccounts));
		assertFalse(result.getAccounts().contains(oldAccount));

		verify(userRepository, times(1)).findById(user.getId());
		verify(accountRepository, times(1)).findAllById(newAccountIds);
		verify(userRepository, times(1)).save(user);
	}

	@DisplayName("UserNotFoundException : LinkUserToAccounts")
	@Test
	void linkUserToAccounts_userNotFoundTest() {
		List<Long> newAccountIds = Arrays.asList(200L, 201L);

		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class,
				() -> userService.linkUserToAccounts(user.getId(), newAccountIds));

		verify(accountRepository, never()).findAllById(newAccountIds);
		verify(userRepository, never()).save(any(User.class));
	}

	@DisplayName("SomeAccountsNotFound : LinkUserToAccounts")
	@Test
	void linkUserToAccounts_someAccountsNotFound() {
		List<Long> newAccountIds = Arrays.asList(200L, 201L, 202L);

		Account account1 = Account.builder()
			.id(200L)
			.dcId("200")
			.practiceId(200L)
			.dcName("name")
			.name("acc1_name")
			.isActive(true)
			.user(null)
			.build();

		Account account2 = Account.builder()
			.id(201L)
			.dcId("201")
			.practiceId(201L)
			.dcName("name")
			.name("acc2_name")
			.isActive(true)
			.user(null)
			.build();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(accountRepository.findAllById(newAccountIds)).thenReturn(Arrays.asList(account1, account2));

		assertThrows(ResourceNotFoundException.class,
				() -> userService.linkUserToAccounts(user.getId(), newAccountIds));

		verify(userRepository, times(1)).findById(user.getId());
		verify(accountRepository, times(1)).findAllById(newAccountIds);
		verify(userRepository, never()).save(any(User.class));
	}

	// @DisplayName("LinkUserToRoleTest")
	// @Test
	void linkUserToRoleTest() throws ResourceNotFoundException {

		Set<Permission> permissions = new HashSet<>(Set.of(new Permission(123L, "READ", "read", Boolean.TRUE)));
		user = User.builder()
			.id(1L)
			.practiceId(200L)
			.name(new Name("John", "Doe", "doe", "Mr"))
			.permissions(permissions)
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
		Role role = Role.builder()
			.id(2L)
			.name("role1")
			.description("description")
			.permissions(permissions)
			.isActive(true)
			.build();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByIdAndIsActiveTrue(role.getId())).thenReturn(Optional.of(role));
		when(userRepository.save(user)).thenReturn(user);

		User result = userService.linkUserToRoleAndPermissions(user.getId(), role.getId(), permissions);

		assertNotNull(result);
		assertEquals(role, result.getRole());

		verify(userRepository, times(1)).findById(user.getId());
		verify(roleRepository, times(1)).findByIdAndIsActiveTrue(role.getId());
		verify(userRepository, times(1)).save(user);
	}

	// @DisplayName("UserNotFound : LinkUserToRoleTest")
	// @Test
	void linkUserToRole_UserNotFoundTest() {
		Long roleId = 2L;
		Set<Permission> permissions = new HashSet<>();
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class,
				() -> userService.linkUserToRoleAndPermissions(user.getId(), roleId, permissions));

		verify(userRepository, times(1)).findById(user.getId());
		verify(roleRepository, never()).findByIdAndIsActiveTrue(roleId);
		verify(userRepository, never()).save(any(User.class));
	}

	// @DisplayName("RoleNotFound : LinkUserToRoleTest")
	// @Test
	void linkUserToRole_RoleNotFoundTest() {
		Long roleId = 2L;
		Set<Permission> permissions = new HashSet<>();
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByIdAndIsActiveTrue(roleId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> userService.linkUserToRoleAndPermissions(user.getId(), roleId, permissions));

		verify(userRepository, times(1)).findById(user.getId());
		verify(roleRepository, times(1)).findByIdAndIsActiveTrue(roleId);
		verify(userRepository, never()).save(any(User.class));
	}

	private Page<User> getUserList() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "first_name";
		String sortOrder = "asc";

		User user1 = User.builder()
			.id(1L)
			.practiceId(100L)
			.name(new Name("user1_firstName", "user1_middleName", "user1_lastName", "Mr"))
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
			.name(new Name("user2_firstName", "user2_middleName", "user2_lastName", "Mr"))
			.contact(Contact.builder().email("user1@gmail.com").phone("1234567890").fax("9876543210").build())
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
			.name(new Name("user3_firstName", "user3_middleName", "user3_lastName", "Mr"))
			.contact(Contact.builder().email("user1@gmail.com").phone("1234567890").fax("9876543210").build())
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
			.name(new Name("user4_firstName", "user4_middleName", "user4_lastName", "Mr"))
			.contact(Contact.builder().email("user1@gmail.com").phone("1234567890").fax("9876543210").build())
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
			.name(new Name("user5_firstName", "user5_middleName", "user5_lastName", "Mr"))
			.contact(Contact.builder().email("user1@gmail.com").phone("1234567890").fax("9876543210").build())
			.address(Address.builder()
				.address1("15 Main Street")
				.address2("Suite 505")
				.city("New York")
				.state("NY")
				.zip("10005")
				.build())
			.isActive(true)
			.build();
		List<User> users = Arrays.asList(user1, user2, user3, user4, user5);

		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(users, pageable, users.size());

	}

}
