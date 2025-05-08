package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeLinkQuestionnaireConfigurationDTO {

	@NotNull
	private Long questionnaireId;

	private String materialNumber;

	private String accountNumber;

	@Nullable
	@Valid
	private CatalogConfigurationAttributes catalogId;

	ChannelType channelType = ChannelType.BUY_AND_BILL;

}
