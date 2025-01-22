package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.Set;

import com.mckesson.cgat.pde.usermanagement.model.QuestionnaireConfigurationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class QuestionnaireDTO {

	private Long id;

	@Size(max = 20, message = "Template name cannot exceed 20 characters.")
	@NotBlank(message = "Template name can not be blank")
	private String name;

	@NotEmpty(message = "At least one question is required")
	private Set<QuestionDTO> questions;

	private Set<QuestionnaireConfigurationType> configuredAt;

}
