package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.service.AccountService;
import com.mckesson.cgat.pde.usermanagement.util.TestUtils;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalUserController.class)
@ContextConfiguration(classes = { GlobalUserController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class GlobalUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@BeforeEach
	public void setUp() {
		TestUtils.setupPrincipal();
	}

	@AfterEach
	public void tearDown() {
		OrganizationContext.clear();
	}

	@DisplayName("GetLoggedInUserTest")
	@Test
	void getLoggedInUserDetailsTest() throws Exception {
		mockMvc.perform(get("/user/current").contentType(MediaType.APPLICATION_JSON_VALUE))
			.andDo(print())
			.andExpect(status().isOk());

	}

}
