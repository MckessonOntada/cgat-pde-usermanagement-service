package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TherapyConfigRequestDTO {

	@NotNull(message = "Material numbers must not be null.")
	@Size(min = 1, message = "Material numbers must not be empty.")
	private List<String> materialNumbers;

	private boolean isPatientInformationRequired;

	private boolean isCoiIdRequired;

}
