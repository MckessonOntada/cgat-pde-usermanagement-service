package com.mckesson.cgat.pde.usermanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.mckesson.cgat.pde.usermanagement.dto.TherapyResponseDto;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;

@WebMvcTest(GlobalTherapyController.class)
@ContextConfiguration(classes = { GlobalTherapyController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class GlobalTherapyControllerTest {

	@MockBean
	private TherapyService therapyService;

	@Autowired
	private MockMvc mockMvc;

	@DisplayName("Test: FindAll Global Therapies")
	@Test
	void findAllTherapiesTest() throws Exception {

		String pageNo = "0";
		String pageSize = "20";
		String sortBy = "name";
		String sortOrder = "asc";
		String query = "";

		Page<TherapyResponseDto> mockResponse = getTherapies();

		when(therapyService.findAllGlobalTherapies(anyString(), any(PageRequest.class))).thenReturn(mockResponse);

		mockMvc
			.perform(get("/therapies").param("pageNo", pageNo)
				.param("pageSize", pageSize)
				.param("sortBy", sortBy)
				.param("sortOrder", sortOrder)
				.param("query", query)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}

	private Page<TherapyResponseDto> getTherapies() {
		Integer pageNo = 0;
		Integer pageSize = 20;
		String sortBy = "name";
		String sortOrder = "asc";
		List<TherapyResponseDto> therapies = new ArrayList<>();
		TherapyResponseDto therapy1 = TherapyResponseDto.builder()
			.materialNumber("12")
			.materialDescription("PCM")
			.ndcNumber("89")
			.strength("650mg")
			.dosage("SOS")
			.build();
		TherapyResponseDto therapy2 = TherapyResponseDto.builder()
			.materialNumber("18")
			.materialDescription("PANTOP")
			.ndcNumber("76")
			.strength("40mg")
			.dosage("BB")
			.build();
		TherapyResponseDto therapy3 = TherapyResponseDto.builder()
			.materialNumber("27")
			.materialDescription("ONDEM")
			.ndcNumber("22")
			.strength("4mg")
			.dosage("SOS")
			.build();

		therapies.add(therapy1);
		therapies.add(therapy2);
		therapies.add(therapy3);
		Sort sort = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		return new PageImpl<>(therapies, pageable, therapies.size());
	}

}
