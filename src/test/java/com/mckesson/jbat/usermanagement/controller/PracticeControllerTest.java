package com.mckesson.jbat.usermanagement.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.controller.PracticeController;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;

@TestInstance(Lifecycle.PER_CLASS)
class PracticeControllerTest extends AbstractTest {

	@InjectMocks
	private PracticeController controller;

	@Mock
	private PracticeService practiceService;

	@Autowired
	ObjectMapper objectMapper;

	private Practice practice;

	@BeforeAll
	public void beforeAll() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@BeforeEach
	public void beforeEach() {
		practice = Practice.builder()
			.id(1L)
			.address(new Address("address1", "address2", "city", "state", "12345"))
			.contact(new Contact("email@gmail.com", "1212121212", "fax"))
			.contactPerson(new Name("first", "", "last", "MR"))
			.isActive(Boolean.TRUE)
			.logoUrl("logo url")
			.name("practicename")
			.build();
	}

	@DisplayName("Test : Get Practice")
	@Test
	void getPracticeTest() throws Exception {
		String practiceId = "1";
		when(practiceService.getPractice(practiceId)).thenReturn(practice);
		ResultActions response = mockMvc.perform(get("/practice/{practiceId}", practiceId));
		response.andExpect(status().isOk());
		verify(practiceService, times(1)).getPractice(practiceId);
	}

}
