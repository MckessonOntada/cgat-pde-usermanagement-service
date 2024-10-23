package com.mckesson.jbat.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.repository.UserRepository;
import com.mckesson.cgat.pde.usermanagement.service.impl.UserServiceImpl;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.usermanagement.controller.AbstractTest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends AbstractTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	private User user;

	@BeforeEach
	public void setUp() {
		user = User.builder().id(1L).practiceId(200L).name(new Name("John", "Doe", "doe", "Mr"))
				.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
				.address(Address.builder().address1("123 Main St").address2("Suite 500").city("New York").state("NY")
						.zip("10001").build())
				.isActive(true).build();
	}

	@DisplayName("Test : Create User ")
	@Test
	void createUserTest() {
		when(userRepository.save(user)).thenReturn(user);
		assertEquals(user, userService.createUser(user));

	}

	@Test
	void getUserByIdTest() throws ResourceNotFoundException {
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		assertEquals(user, userService.getUser(user.getId()));

	}

	@Test
	void testUpdateUserTest() throws ResourceNotFoundException {

		Long userId = 1L;
		Name name = new Name("ram", null, "kumar", "Mr");
		Contact contact = new Contact("ram@gmail.com", "0123456789", "12345");
		Address address = new Address("vijay nagar", null, "indore", "mp", "452010");

		User existingUser = new User(null, 2L, name, contact, address, null, null, true);
		User updateUser = new User(userId, 2L, new Name("Ram", null, "Kumar", "Mr"),
				new Contact("ram.test@gmail.com", "0123456789", "12345"),
				new Address("vijay nagar", null, "indore", "mp", "452010"), null, null, true);

		when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenReturn(updateUser);

		User result = userService.updateUser(existingUser.getId(), updateUser);

		assertNotNull(result);
		assertEquals(updateUser.getName(), result.getName());
		assertEquals(updateUser.getContact().getEmail(), result.getContact().getEmail());
		verify(userRepository).findById(existingUser.getId());
		verify(userRepository).save(updateUser);
	}

	@Test
	void deleteUserTest() {
		userRepository.delete(user);
		verify(userRepository, times(1)).delete(user);

	}

	@Test
	void findAllWithPageableTest() {

		Name name1 = new Name("ram", null, "kumar", "Mr");
		Name name2 = new Name("neeraj", null, "kumar", "Mr");
		Contact contact1 = new Contact("ram@gmail.com", "0123456789", "12345");
		Contact contact2 = new Contact("neeraj@gmail.com", "0123456789", "12345");
		Address address1 = new Address("vijay nagar", null, "indore", "mp", "452010");
		Address address2 = new Address("vijay nagar", null, "indore", "mp", "452010");

		User user1 = new User(null, 2L, name1, contact1, address1, null, null, true);
		User user2 = new User(null, 3L, name2, contact2, address2, null, null, true);
		List<User> users = Arrays.asList(user1, user2);

		Pageable paging = PageRequest.of(1, 20, Sort.by("first_name").ascending());
		Page<User> userPage = new PageImpl<>(users, paging, users.size());

		when(userRepository.findAllByQueryText("", "ALL", paging, 1)).thenReturn(userPage);
		Page<User> result = userService.findAll(paging, "", List.of("ALL"), "1");
		assertNotNull(result);
		assertEquals(users, result.getContent());

		verify(userRepository, times(1)).findAllByQueryText("", "ALL", paging, 1);
	}

}
