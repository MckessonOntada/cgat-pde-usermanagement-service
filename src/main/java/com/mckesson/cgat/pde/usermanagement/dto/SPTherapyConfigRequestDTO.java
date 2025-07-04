package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SPTherapyConfigRequestDTO {

	@NotNull(message = "SP Product ids must not be null.")
	@Size(min = 1, message = "SP Product ids must not be empty.")
	private List<String> spProductIds;

	private boolean isCoiIdRequired;

	private boolean isPatientIdentifierRequired;

	private boolean isMrnRequired;

}
