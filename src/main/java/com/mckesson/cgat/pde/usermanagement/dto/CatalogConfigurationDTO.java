package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CatalogConfigurationDTO {

	@NotEmpty(message = "At least one catalog price is required")
	private List<CatalogAttributes> catalogAttributes;

	@NotEmpty(message = "At least one step is required")
	private List<StepDTO> steps;

}
