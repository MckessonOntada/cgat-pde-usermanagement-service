package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
import com.mckesson.cgat.pde.usermanagement.service.impl.utils.PracticeUtils;

@WebMvcTest(PracticeController.class)
@ContextConfiguration(classes = { PracticeController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class PracticeControllerTest {

	@MockBean
	private PracticeService practiceService;

	@MockBean
	private PracticeUtils practiceUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Practice practice;

	@BeforeEach
	public void beforeEach() {
		practice = Practice.builder()
			.id(1L)
			.address(new Address("address1", "address2", "city", "state", "12345"))
			.contact(new Contact("email@gmail.com", "1212121212", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("logo url")
			.name("practice2")
			.build();
	}

	@DisplayName("Test : Get Practice")
	@Test
	void getPracticeTest() throws Exception {
		String practiceId = "1";
		when(practiceService.getPractice(practiceId)).thenReturn(practice);
		mockMvc.perform(get("/practices/{practiceId}", practiceId)).andDo(print()).andExpect(status().isOk());
	}

	@DisplayName("Test : Delete Practice")
	@Test
	void deletePracticeTest() throws Exception {
		String practiceId = "1";
		doNothing().when(practiceService).deletePractice(practiceId);
		mockMvc.perform(delete("/practices/{practiceId}", practiceId)).andDo(print()).andExpect(status().isOk());
	}

	@DisplayName("Test : FindAll Practice")
	@Test
	void findAllTest() throws Exception {
		String pageNo = "0";
		String pageSize = "20";
		String sortBy = "name";
		String sortOrder = "asc";
		String query = "";
		String statusActive = "ALL";
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		Page<Practice> mockResponse = getPractices();

		when(practiceService.findAll(paging, query, List.of(statusActive))).thenReturn(mockResponse);

		mockMvc
			.perform(get("/practices").param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("sortBy", sortBy)
				.param("sortOrder", sortOrder)
				.param("query", query)
				.param("status", statusActive)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
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
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/1")
			.name("practicename")
			.build();
		Practice practice2 = Practice.builder()
			.id(3L)
			.address(new Address("address1_2", "address2_2", "city2", "state4", "23451"))
			.contact(new Contact("email@gmail.com", "2222222222", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/2")
			.name("practicename")
			.build();
		Practice practice3 = Practice.builder()
			.id(5L)
			.address(new Address("address1_3", "address2_3", "city3", "state3", "34512"))
			.contact(new Contact("email@gmail.com", "9999999999", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/3")
			.name("practicename")
			.build();
		Practice practice4 = Practice.builder()
			.id(7L)
			.address(new Address("address1_4", "address2_4", "city4", "state2", "45123"))
			.contact(new Contact("email@gmail.com", "7777777777", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("http://localhost:8080/4")
			.name("practicename")
			.build();
		Practice practice5 = Practice.builder()
			.id(9L)
			.address(new Address("address1_5", "address2_5", "city5", "state1", "51234"))
			.contact(new Contact("email@gmail.com", "6666666666", "fax"))
			.contactPerson(new Name("first", "", "last"))
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

	// @DisplayName("Test : Create Practice")
	// @Test
	void createPractice() throws Exception {
		ClassPathResource fileResource = new ClassPathResource("");
		MockMultipartFile file = new MockMultipartFile("logo.jpg", fileResource.getFilename(),
				MediaType.MULTIPART_FORM_DATA_VALUE, fileResource.getInputStream());

		byte[] content = objectMapper.writeValueAsBytes(practice);
		MockPart somePart = new MockPart("practiceStr", content);
		somePart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		when(practiceService.createPractice(any(Practice.class), any(MockMultipartFile.class))).thenReturn(practice);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/practices").file(file).part(somePart))
			.andExpect(status().isCreated())
			.andDo(print())
			.andReturn();
	}

	@DisplayName("Test : Update Practice")
	@Test
	void updatePractice() throws Exception {

		Practice newPractice = Practice.builder()
			.id(1L)
			.address(new Address("address1", "address2", "city", "state", "12345"))
			.contact(new Contact("email@gmail.com", "1212121212", "fax"))
			.contactPerson(new Name("first", "", "last"))
			.isActive(Boolean.TRUE)
			.logoUrl("changed logo url")
			.name("changedpracticename")
			.build();

		ClassPathResource fileResource = new ClassPathResource("");
		MockMultipartFile file = new MockMultipartFile("logo", fileResource.getFilename(),
				MediaType.MULTIPART_FORM_DATA_VALUE, fileResource.getInputStream());

		byte[] content = objectMapper.writeValueAsBytes(newPractice);
		MockPart somePart = new MockPart("practiceStr", content);
		somePart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		when(practiceService.createPractice(any(Practice.class), any(MockMultipartFile.class))).thenReturn(newPractice);

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/practices/{practiceId}",
				newPractice.getId().toString());
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
		mockMvc.perform(builder.file(file).part(somePart)).andExpect(status().isOk()).andDo(print()).andReturn();
	}

}
