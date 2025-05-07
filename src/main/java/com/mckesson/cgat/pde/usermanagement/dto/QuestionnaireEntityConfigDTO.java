package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class QuestionnaireEntityConfigDTO {

	List<String> materialNumberList;

	List<String> accountNumberList;

	@Nullable
	@Valid
	List<CatalogConfigurationAttributes> catalogIdAttributes;

}
