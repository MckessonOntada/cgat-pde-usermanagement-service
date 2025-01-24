package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireConfigurationDTO {

	@NotNull
	@Size(min = 1)
	List<Long> questionnaireIds;

	List<String> materialNumberList;

	List<String> accountNumberList;

	List<QuestionnaireConfigurationCatalogDTO> catalogIdList;

}
