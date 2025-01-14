package com.mckesson.cgat.pde.usermanagement.controller;

import com.mckesson.cgat.pde.usermanagement.dto.PracticeLogoResponseDto;
import com.mckesson.cgat.pde.usermanagement.service.PracticeService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalPracticeController.class)
@ContextConfiguration(classes = { GlobalPracticeController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class GlobalPracticeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PracticeService practiceService;

	@DisplayName("Test : Get Practice Logo")
	@Test
	void getPracticeLogoTest() throws Exception {
		String practiceId = "1";
		PracticeLogoResponseDto practiceLogoResponseDto = PracticeLogoResponseDto.builder()
			.logo(new byte[120])
			.logoExtension(MediaType.IMAGE_JPEG)
			.build();
		when(practiceService.getPracticeLogo(practiceId)).thenReturn(practiceLogoResponseDto);
		mockMvc.perform(get("/practice/{practiceId}/logo", practiceId)).andDo(print()).andExpect(status().isOk());
	}

}
