package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.ChannelType;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireConfigurationDTO {

	@NotNull(message = "QuestionnaireIds should not be null")
	@Size(min = 1, message = "QuestionnaireIds should not be empty")
	List<Long> questionnaireIds;

	List<String> materialNumberList;

	List<String> accountNumberList;

	@Nullable
	@Valid
	List<CatalogConfigurationAttributes> catalogIdAttributes;

	ChannelType channelType = ChannelType.BUY_AND_BILL;

}
