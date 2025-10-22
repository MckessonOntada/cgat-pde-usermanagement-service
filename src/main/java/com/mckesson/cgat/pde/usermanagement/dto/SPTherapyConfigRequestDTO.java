package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ConfigFieldRequirement;
import com.mckesson.cgat.pde.usermanagement.model.SPTriageChannel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SPTherapyConfigRequestDTO {

	@NotNull(message = "SP Product ids must not be null.")
	@Size(min = 1, message = "SP Product ids must not be empty.")
	private List<String> spProductIds;

	private ConfigFieldRequirement coiIdRequirement;

	private ConfigFieldRequirement mrnRequirement;

	private ConfigFieldRequirement pidRequirement;

	private Boolean isPrescriptionDetailsRequired;

	private SPTriageChannel triageChannel;

	@Min(value = 1, message = "Quantity must be greater than 0.")
	private Long quantity;

	@Min(value = 1, message = "Days supply must be greater than 0.")
	private Long daysSupply;

	@Min(value = 0, message = "Refills must be 0 or more.")
	private Long refills;

	@Size(max = 5000, message = "Instructions must not exceed 5000 characters.")
	private String instructions;

	@Size(max = 5000, message = "Consent policy must not exceed 5000 characters.")
	private String consentPolicy;

}
