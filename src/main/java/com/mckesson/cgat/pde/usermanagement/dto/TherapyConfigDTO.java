package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class TherapyConfigDTO {

	private boolean triageChannel;

	private List<FieldConfigDTO> fields;

}
