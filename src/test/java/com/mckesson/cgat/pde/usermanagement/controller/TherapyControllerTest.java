package com.mckesson.cgat.pde.usermanagement.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.Practice;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@WebMvcTest(TherapyController.class)
@ContextConfiguration(classes = { TherapyController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
//public class TherapyControllerTest {
//
//	@MockBean
//	TherapyService therapyService;
//
//	@Autowired
//	MockMvc mockMvc;
//
//	
//	private Page<TherapyCatalogResponseDTO> getTherapies() {
//		Integer pageNo = 0;
//		Integer pageSize = 20;
//		String sortBy = "name";
//		String sortOrder = "asc";
//		List<Practice> practices = new ArrayList<>();
//		TherapyCatalogResponseDTO practice1 = new TherapyCatalogResponseDTO();
//			.id(1L)
//			.address(new Address("address1_1", "address2_1", "city1", "state5", "12345"))
//			.contact(new Contact("email@gmail.com", "+1", "4444444444", "fax"))
//			.contactPerson(new Name("first", "", "last"))
//			.isActive(Boolean.TRUE)
//			.logoUrl("http://localhost:8080/1")
//			.name("practicename")
//			.build();
//		TherapyCatalogResponseDTO practice2 = TherapyCatalogResponseDTO.builder()
//			.id(3L)
//			.address(new Address("address1_2", "address2_2", "city2", "state4", "23451"))
//			.contact(new Contact("email@gmail.com", "+1", "2222222222", "fax"))
//			.contactPerson(new Name("first", "", "last"))
//			.isActive(Boolean.TRUE)
//			.logoUrl("http://localhost:8080/2")
//			.name("practicename")
//			.build();
//		TherapyCatalogResponseDTO practice3 = TherapyCatalogResponseDTO.builder()
//			.id(5L)
//			.address(new Address("address1_3", "address2_3", "city3", "state3", "34512"))
//			.contact(new Contact("email@gmail.com", "+1", "9999999999", "fax"))
//			.contactPerson(new Name("first", "", "last"))
//			.isActive(Boolean.TRUE)
//			.logoUrl("http://localhost:8080/3")
//			.name("practicename")
//			.build();
//		Practice practice4 = Practice.builder()
//			.id(7L)
//			.address(new Address("address1_4", "address2_4", "city4", "state2", "45123"))
//			.contact(new Contact("email@gmail.com", "+1", "7777777777", "fax"))
//			.contactPerson(new Name("first", "", "last"))
//			.isActive(Boolean.TRUE)
//			.logoUrl("http://localhost:8080/4")
//			.name("practicename")
//			.build();
//		Practice practice5 = Practice.builder()
//			.id(9L)
//			.address(new Address("address1_5", "address2_5", "city5", "state1", "51234"))
//			.contact(new Contact("email@gmail.com", "+1", "6666666666", "fax"))
//			.contactPerson(new Name("first", "", "last"))
//			.isActive(Boolean.TRUE)
//			.logoUrl("http://localhost:8080/5")
//			.name("practicename")
//			.build();
//		practices.add(practice1);
//		practices.add(practice2);
//		practices.add(practice3);
//		practices.add(practice4);
//		practices.add(practice5);
//		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
//		return new PageImpl<>(practices, pageable, practices.size());
//	}
}
