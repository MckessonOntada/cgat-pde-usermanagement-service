package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TherapyConfigRequestDTO {

	@NotNull(message = "Ndc numbers must not be null.")
	@Size(min = 1, message = "Ndc numbers must not be empty.")
	private List<String> ndcNumbers;

	private boolean isPatientInformationRequired;

	private boolean isCoiIdRequired;

	private boolean isMRNRequired;

	private boolean isDOARequired;

	private boolean isPORequired;

	private boolean isPIDRequired;

	private ChannelType channelType = ChannelType.BUY_AND_BILL;

}
