package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.User;
import com.mckesson.cgat.pde.usermanagement.service.UserService;
import com.mckesson.jbat.usermanagement.controller.AbstractTest;

class UserControllerTest extends AbstractTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private User user;

	private ObjectMapper objectMapper;

	@BeforeAll
	public void beforeAll() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		objectMapper = new ObjectMapper();
		user = User.builder().id(1L).practiceId(200L).name(new Name("John", "Doe", "doe", "Mr"))
				.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
				.address(Address.builder().address1("123 Main St").address2("Suite 500").city("New York").state("NY")
						.zip("10001").build())
				.isActive(true).build();
	}

	@Test
	void testCreateUser() throws Exception {
		Long practiceId = 1L;

		when(userService.createUser(user)).thenReturn(user);

		mockMvc.perform(post("/practice/{practiceId}/users", practiceId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isOk());
//                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
	}

}
