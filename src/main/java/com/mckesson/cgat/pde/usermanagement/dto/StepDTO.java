package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepDTO {
    private int stepNumber;
    private String stepName;
    private String description;
}
