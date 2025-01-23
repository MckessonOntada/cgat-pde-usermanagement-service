package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

enum QuestionType {

	RADIO, CHECKBOX, SELECT, INPUT, UPLOAD

}

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class QuestionDTO {

	@NotBlank(message = "Question text can not be blank")
	private String questionText;

	@NotNull(message = "Question type cannot be null")
	private QuestionType type;

	private boolean isMandatory;

	private boolean isDocUploadRequired;

	private String additionalData;

}
