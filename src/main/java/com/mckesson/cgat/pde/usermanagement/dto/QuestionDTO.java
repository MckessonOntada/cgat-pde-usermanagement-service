package com.mckesson.cgat.pde.usermanagement.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

enum QuestionType {
	RADIO, TEXT
}

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class QuestionDTO {

	@NotBlank(message = "Question can not be blank")
	private String question;

    @NotNull(message = "Question type cannot be null")
	private QuestionType type;

	private Set<String> options;

	private boolean isMandatory;

	private boolean isDocUploadRequired;

}
