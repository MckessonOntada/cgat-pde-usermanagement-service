package com.mckesson.jbat.usermanagement.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.service.impl.PracticeServiceImpl;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import com.mckesson.jbat.usermanagement.controller.AbstractTest;

@SpringBootTest(classes = JacksonAutoConfiguration.class)
class PracticeServiceImplTest extends AbstractTest {

	@InjectMocks
	private PracticeServiceImpl practiceService;

	@Mock
	private PracticeRepository practiceRepository;

	@Autowired
	ObjectMapper objectMapper;

	private Practice practice;

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

	@DisplayName("Test : Create Practice")
	@Test
	void createPracticeTest() {
		Mockito.when(practiceRepository.save(practice)).thenReturn(practice);
		assertEquals(practice, practiceRepository.save(practice));
		assertNotNull(practice);
	}

	@DisplayName("Test : Get Practice")
	@Test
	void getPracticeTest() throws ResourceNotFoundException {
		Long practiceID = 1L;
		Mockito.when(practiceRepository.findById(practiceID)).thenReturn(Optional.of(practice));
		assertNotNull(practiceService.getPractice(practiceID.toString()));
		assertEquals(practice, practiceService.getPractice(practiceID.toString()));
	}

	@DisplayName("Test : Delete Practice")
	@Test
	void deletePracticeTest() throws ResourceNotFoundException {
		Long practiceId = 1L;
		when(practiceRepository.findById(practiceId)).thenReturn(Optional.of(practice));
		Practice foundPractice = practiceService.getPractice(practiceId.toString());
		doNothing().when(practiceRepository).deleteById(foundPractice.getId());
		practiceService.deletePractice(foundPractice.getId().toString());
		verify(practiceRepository, times(0)).deleteById(practiceId);

	}

	@DisplayName("Test : FindAll Practice")
	@Test
	void findAll_practice_filter_Test() {
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		String query = null;

		String statusActive = "ALL";
		Page<Practice> mockResponse = getPractices();

		Mockito.when(practiceRepository.findAllByQueryText(query, statusActive, paging)).thenReturn(mockResponse);
		Page<Practice> result = practiceService.findAll(paging, query, List.of(statusActive));
		verify(practiceRepository, times(1)).findAllByQueryText(query, statusActive, paging);
		assertTrue(result.getSize() == mockResponse.getSize());
	}

	private Page<Practice> getPractices() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<Practice> practices = new ArrayList<>();
		Practice practice1 = Practice.builder()
			.id(1L)
			.address(new Address("address1_1", "address2_1", "city1", "state5", "12345"))
			.contact(new Contact("email@gmail.com", "4444444444", "fax"))
			.contactPerson(new Name("first", "", "last", "MR"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/1")
			.name("practicename")
			.build();
		Practice practice2 = Practice.builder()
			.id(3L)
			.address(new Address("address1_2", "address2_2", "city2", "state4", "23451"))
			.contact(new Contact("email@gmail.com", "2222222222", "fax"))
			.contactPerson(new Name("first", "", "last", "MRS"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/2")
			.name("practicename")
			.build();
		Practice practice3 = Practice.builder()
			.id(5L)
			.address(new Address("address1_3", "address2_3", "city3", "state3", "34512"))
			.contact(new Contact("email@gmail.com", "9999999999", "fax"))
			.contactPerson(new Name("first", "", "last", "MS"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/3")
			.name("practicename")
			.build();
		Practice practice4 = Practice.builder()
			.id(7L)
			.address(new Address("address1_4", "address2_4", "city4", "state2", "45123"))
			.contact(new Contact("email@gmail.com", "7777777777", "fax"))
			.contactPerson(new Name("first", "", "last", "MS"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/4")
			.name("practicename")
			.build();
		Practice practice5 = Practice.builder()
			.id(9L)
			.address(new Address("address1_5", "address2_5", "city5", "state1", "51234"))
			.contact(new Contact("email@gmail.com", "6666666666", "fax"))
			.contactPerson(new Name("first", "", "last", "JR"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/5")
			.name("practicename")
			.build();
		practices.add(practice1);
		practices.add(practice2);
		practices.add(practice3);
		practices.add(practice4);
		practices.add(practice5);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(practices, pageable, practices.size());
	}

}
