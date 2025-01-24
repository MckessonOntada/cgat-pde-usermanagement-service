package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionnaireConfigurationCatalogDTO {

	@NotBlank(message = "shipTo number should not be blank")
	String shipTo;

	@NotBlank(message = "soldTo number should not be blank")
	String soldTo;

	@NotBlank(message = "material number should not be blank")
	String material;

	String variant;

}
