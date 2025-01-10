package com.mckesson.cgat.pde.usermanagement.controller;
import com.mckesson.cgat.pde.usermanagement.model.Therapy;
import com.mckesson.cgat.pde.usermanagement.service.TherapyService;
import com.mckesson.jbat.common.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(GlobalTherapyController.class)
@ContextConfiguration(classes = { GlobalTherapyController.class })
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class GlobalTherapyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TherapyService therapyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByMaterialNumber_Success() throws Exception {
        String materialNumber = "12345";
        Therapy mockTherapy = new Therapy();
        mockTherapy.setMaterialNumber(materialNumber);
        when(therapyService.findByMaterialNumber(materialNumber)).thenReturn(mockTherapy);
        mockMvc.perform(get("/therapy/{materialNumber}", materialNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.materialNumber").value(materialNumber));
        verify(therapyService, times(1)).findByMaterialNumber(materialNumber);
    }

    @Test
    public void testFindByMaterialNumber_NotFound() throws Exception {
        String materialNumber = "12345";
        when(therapyService.findByMaterialNumber(materialNumber)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/therapy/{materialNumber}", materialNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(therapyService, times(1)).findByMaterialNumber(materialNumber);
    }
}
