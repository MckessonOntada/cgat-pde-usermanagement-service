package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.mckesson.cgat.pde.usermanagement.dto.CustomDTOs.TherapyCatalogResponseDTO;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@WebMvcTest(TherapyController.class)
@ContextConfiguration(classes = { TherapyController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TherapyControllerTest {

	@MockBean
	TherapyService therapyService;

	@Autowired
	MockMvc mockMvc;

	@DisplayName("Test : FindAll Therapies")
	@Test
	public void findAllTherapiesTest() throws Exception {

		String query = "";
		String salesOrg = "";
		Pageable paging = PageRequest.of(0, 20, Sort.by("name").ascending());
		String practiceId = "2";
		String userId = "2";
		Page<TherapyCatalogResponseDTO> therapies = getTherapies();

		when(therapyService.findAllTherapies(2L, query, salesOrg, paging)).thenReturn(therapies);

		mockMvc
			.perform(get("/practices/{practiceId}/users/{userId}/therapies", practiceId, userId).param("pageNo", "0")
				.param("pageSize", "20")
				.param("query", "")
				.param("salesOrg", "")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
	}

	private Page<TherapyCatalogResponseDTO> getTherapies() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<TherapyCatalogResponseDTO> therapies = new ArrayList<>();
		TherapyCatalogResponseDTO therapy1 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc1")
			.therapyDescription("tdesc")
			.therapyId("1")
			.udi("udi1")
			.build();
		TherapyCatalogResponseDTO therapy2 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc2")
			.therapyDescription("tdesc")
			.therapyId("2")
			.udi("udi2")
			.build();
		TherapyCatalogResponseDTO therapy3 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc3")
			.therapyDescription("tdesc")
			.therapyId("3")
			.udi("udi3")
			.build();
		TherapyCatalogResponseDTO therapy4 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc4")
			.therapyDescription("tdesc")
			.therapyId("4")
			.udi("udi4")
			.build();
		TherapyCatalogResponseDTO therapy5 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc5")
			.therapyDescription("tdesc")
			.therapyId("5")
			.udi("udi5")
			.build();
		TherapyCatalogResponseDTO therapy6 = TherapyCatalogResponseDTO.builder()
			.ndc("ndc6")
			.therapyDescription("tdesc")
			.therapyId("6")
			.udi("udi6")
			.build();

		therapies.add(therapy1);
		therapies.add(therapy2);
		therapies.add(therapy3);
		therapies.add(therapy4);
		therapies.add(therapy5);
		therapies.add(therapy6);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(therapies, pageable, therapies.size());
	}

}
