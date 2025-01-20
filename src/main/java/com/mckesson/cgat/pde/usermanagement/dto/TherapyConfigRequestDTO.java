package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class TherapyConfigRequestDTO {
    private List<String> materialNumbers;
    private boolean patientInfo;
    private boolean coiId;
}
