package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyValueDto {
    private String label;
    private String shortLabel;
    private String value;
    private String type;

}
