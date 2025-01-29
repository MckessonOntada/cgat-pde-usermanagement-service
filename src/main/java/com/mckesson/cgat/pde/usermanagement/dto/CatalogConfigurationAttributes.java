package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogConfigurationAttributes {

	@Size(max = 18)
	@NotBlank
	private String sapMaterial;

	@Size(max = 10)
	@NotBlank
	private String soldToCustomer;

	@Size(max = 10)
	@NotBlank
	private String shipToCustomer;

	@Size(max = 30)
	private String variant;

}
