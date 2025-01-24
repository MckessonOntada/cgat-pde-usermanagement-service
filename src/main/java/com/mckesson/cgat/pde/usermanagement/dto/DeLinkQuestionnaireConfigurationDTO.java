package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeLinkQuestionnaireConfigurationDTO {

	@NotNull
	Long questionnaireId;

	String materialNumber;

	String accountNumber;

	@Nullable
	@Valid
	QuestionnaireConfigurationCatalogDTO catalogId;

}
