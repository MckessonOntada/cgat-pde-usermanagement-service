package com.mckesson.jbat.usermanagement.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.controller.PracticeController;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;

public class PracticeControllerTest extends AbstractTest {

	public MockMvc mockMvc;

	@InjectMocks
	PracticeController practiceController;

	@Mock
	private PracticeService practiceService;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(practiceController).build();
	}

	@Test
	void getPracticeTest() throws Exception {
		ResultActions response = mockMvc.perform(get("/practice/1"));// .param("practiceId", "1"));
		response.andExpect(status().isOk());
	}

}
