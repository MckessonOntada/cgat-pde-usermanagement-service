package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

@Data
public class FieldConfigDTO {

	private String fieldName;

	private String format;

	private FieldPropertiesDTO properties;

}