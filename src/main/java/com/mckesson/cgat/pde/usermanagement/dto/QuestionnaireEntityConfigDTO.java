package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class QuestionnaireEntityConfigDTO {

	Map<String,List<String>> tradeNameNDCsMap;

	List<String> accountNumberList;

	@Nullable
	@Valid
	List<CatalogConfigurationAttributes> catalogIdAttributes;

}
