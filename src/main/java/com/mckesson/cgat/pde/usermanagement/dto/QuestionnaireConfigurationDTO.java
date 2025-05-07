package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionnaireConfigurationDTO {

	@NotNull(message = "QuestionnaireIds should not be null")
	@Size(min = 1, message = "QuestionnaireIds should not be empty")
	List<Long> questionnaireIds;

	QuestionnaireEntityConfigDTO bnb;

	QuestionnaireEntityConfigDTO sp;

}
