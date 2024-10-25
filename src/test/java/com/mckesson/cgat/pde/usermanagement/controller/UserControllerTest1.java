//package com.mckesson.cgat.pde.usermanagement.controller;
//
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mckesson.cgat.pde.usermanagement.model.Address;
//import com.mckesson.cgat.pde.usermanagement.model.Contact;
//import com.mckesson.cgat.pde.usermanagement.model.Name;
//import com.mckesson.cgat.pde.usermanagement.model.User;
//import com.mckesson.cgat.pde.usermanagement.service.UserService;
//import com.mckesson.jbat.usermanagement.controller.AbstractTest;
//
//@TestInstance(Lifecycle.PER_CLASS)
//class UserControllerTest1 extends AbstractTest {
//
//	private MockMvc mockMvc;
//
//	@Mock
//	private UserService userService;
//
//	@InjectMocks
//	private UserController userController;
//
//	private User user;
//
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	@BeforeAll
//	public void beforeAll() {
//		MockitoAnnotations.initMocks(this);
//		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//	}
//
//	@BeforeEach
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//		objectMapper = new ObjectMapper();
//		user = User.builder().id(1L).practiceId(200L).name(new Name("John", "Doe", "doe", "Mr"))
//				.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
//				.address(Address.builder().address1("123 Main St").address2("Suite 500").city("New York").state("NY")
//						.zip("10001").build())
//				.isActive(true).build();
//	}
//
//	@Test
//	void createUserTest() throws Exception {
//		Name name1 = new Name("aa", "bb", "cc", "mr");
//		User user1 = User.builder().id(1L).practiceId(200L).name(name1)
//				.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
//				.address(Address.builder().address1("123 Main St").address2("Suite 500").city("New York").state("NY")
//						.zip("10001").build())
//				.isActive(true).build();
//
//		String objectString = objectMapper.writeValueAsString(user1);
//
//		when(userService.createUser(user1)).thenReturn(user1);
//
//		mockMvc.perform(post("/practice/{practiceId}/users", user1.getPracticeId())
//				.contentType(MediaType.APPLICATION_JSON).content(objectString)).andDo(print())
//				.andExpect(status().isOk())
//
//				.andExpect(jsonPath("$.id").value(1L));
////      .andExpect(jsonPath("$.name").value("Jane Doe"))
////      .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
////}
//	}
//
////}
////
////import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
////import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
////import static org.mockito.Mockito.*;
//// 
////import com.fasterxml.jackson.databind.ObjectMapper;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.MockitoAnnotations;
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
////import org.springframework.http.MediaType;
////import org.springframework.test.web.servlet.MockMvc;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//// 
////import java.util.Optional;
//// 
////@WebMvcTest(UserController.class)
////public class UserControllerTest {
//// 
////    @Autowired
////    private MockMvc mockMvc;
//// 
////    @MockBean
////    private UserService userService;
//// 
////    private ObjectMapper objectMapper;
//// 
////    @BeforeEach
////    public void setup() {
////        objectMapper = new ObjectMapper();
////    }
//// 
//	// Test case for GET /users/{id} - Success Scenario
//	@Test
//	void getUserTest() throws Exception {
//		// Arrange
////        Long userId = 1L;
////        User user = new User(userId, "John Doe", "john.doe@example.com");
//		when(userService.getUser(user.getId())).thenReturn(user);
//
//		// Act & Assert
////        mockMvc.perform(get("/practice/{practiceId}/users/{userId}",user.getPracticeId(), user.getId()))
////        		.acc 
////        		.andExpect(status().isOk())
////                .andExpect(jsonPath("$.id").value(user.getId()))
////                .andExpect(jsonPath("$.name.first_name").value("John"))
////                .andExpect(jsonPath("$.contact.email").value("john.doe@example.com"));
//
//		mockMvc.perform(get("/practice/{practiceId}/users/{userId}", 200, 1))
////                .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk()));
////        		.accept("Application/json"))
//				.andExpect(status().isOk());
//
//	}
//
//// 
////    // Test case for GET /users/{id} - User Not Found
////    @Test
////    public void testGetUserById_NotFound() throws Exception {
////        // Arrange
////        Long userId = 1L;
////        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User not found"));
//// 
////        // Act & Assert
////        mockMvc.perform(get("/users/{id}", userId))
////                .andExpect(status().isNotFound());
////    }
//// 
////    // Test case for POST /users - Success Scenario
////    @Test
////    public void testCreateUser_Success() throws Exception {
////        // Arrange
////        User user = new User(null, "Jane Doe", "jane.doe@example.com");
////        User savedUser = new User(1L, "Jane Doe", "jane.doe@example.com");
////        when(userService.saveUser(any(User.class))).thenReturn(savedUser);
//// 
////        // Act & Assert
////        mockMvc.perform(post("/users")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(user)))
////                .andExpect(status().isCreated())
////                .andExpect(jsonPath("$.id").value(1L))
////                .andExpect(jsonPath("$.name").value("Jane Doe"))
////                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
////    }
//// 
//// Test case for POST /users - Validation Error (Missing Name)
//	@Test
//	void createUser_ValidationErrorTest() throws Exception {
//
//		user = User.builder().id(1L).practiceId(200L).name(new Name("", "Doe", "doe", "Mr"))
//				.contact(Contact.builder().email("john.doe@example.com").phone("1234567890").fax("9876543210").build())
//				.address(Address.builder().address1("123 Main St").address2("Suite 500").city("New York").state("NY")
//						.zip("10001").build())
//				.isActive(true).build();
//
//		ResultActions result = mockMvc
//				.perform(post("/practice/{practiceId}/users", user.getPracticeId())
//						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
//				.andExpect(status().isBadRequest());
//		assertNotNull(result);
//	}
//}
