package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import com.mckesson.cgat.pde.usermanagement.model.ConfigFieldRequirement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TherapyConfigRequestDTO {

	@NotNull(message = "Ndc numbers must not be null.")
	@Size(min = 1, message = "Ndc numbers must not be empty.")
	private List<String> ndcNumbers;

	private Boolean isPatientInformationRequired;

	private ConfigFieldRequirement coiIdRequirement;

	private ConfigFieldRequirement mrnRequirement;

	private ConfigFieldRequirement doaRequirement;

	private ConfigFieldRequirement poRequirement;

	private ConfigFieldRequirement pidRequirement;

	private ConfigFieldRequirement dateOfApheresisRequirement;

    private ConfigFieldRequirement shipToPORequirement;

	private ChannelType channelType = ChannelType.BUY_AND_BILL;

}
