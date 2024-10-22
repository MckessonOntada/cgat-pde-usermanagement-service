package com.mckesson.jbat.usermanagement.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.usermanagement.controller.AbstractTest;

@SpringBootTest(classes = JacksonAutoConfiguration.class)
class PracticeServiceImplTest extends AbstractTest {

	public MockMvc mockMvc;

	@Mock
	private PracticeService practiceService;

	@Mock
	private PracticeRepository practiceRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void createPracticeTest() {
		Practice practice = Practice.builder().id(1L)
				.address(new Address("address1", "address2", "city", "state", "12345"))
				.contact(new Contact("email@gmail.com", "1212121212", "fax"))
				.contactPerson(new Name("first", "", "last", "MR")).isActive(Boolean.TRUE).logoUrl("logo url")
				.name("practicename").build();
		Mockito.when(practiceRepository.save(practice)).thenReturn(practice);
		assertEquals(practice, practiceRepository.save(practice));
		assertNotNull(practice);
	}

	@Test
	void getPracticeTest() throws ResourceNotFoundException {
		Long practiceID = 1L;
		Practice practice = Practice.builder().id(1L)
				.address(new Address("address1", "address2", "city", "state", "12345"))
				.contact(new Contact("email@gmail.com", "1212121212", "fax"))
				.contactPerson(new Name("first", "", "last", "MR")).isActive(Boolean.TRUE).logoUrl("logo url")
				.name("practicename").build();
		Mockito.when(practiceService.getPractice(practiceID.toString())).thenReturn(practice);
		assertNotNull(practiceService.getPractice(practiceID.toString()));
		assertNotNull(practiceRepository.findById(practiceID));
		assertEquals(practice, practiceService.getPractice(practiceID.toString()));
	}

//	@Test
//	void deletePracticeTest() {
//
//	}
}
