package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class QuestionnaireEntityConfigDTO {

	List<Map<String, List<String>>> tradeNameNDCMapList;

	List<String> accountNumberList;

	@Nullable
	@Valid
	List<CatalogConfigurationAttributes> catalogIdAttributes;

}
