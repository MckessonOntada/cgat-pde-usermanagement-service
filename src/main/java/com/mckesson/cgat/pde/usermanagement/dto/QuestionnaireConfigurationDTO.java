package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionnaireConfigurationDTO {

	List<Long> questionnaireIds;

	List<String> entityIds;

}
