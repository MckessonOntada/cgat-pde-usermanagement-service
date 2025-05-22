package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CatalogConfigurationDTO {

	@NotEmpty(message = "At least one catalog configuration attributes is required")
	private List<CatalogConfigurationAttributes> catalogConfigurationAttributes;

	@NotEmpty(message = "At least one step is required")
	private List<StepDTO> steps;

	private ChannelType channelType = ChannelType.BUY_AND_BILL;

}
