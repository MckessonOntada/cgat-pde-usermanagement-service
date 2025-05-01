package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Name implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "First Name is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^(?![ ])[\\p{L}.-]{1,48}[\\p{L}.-]?$",
			message = "First Name can only contain alphabets, spaces, '.', and '-', and cannot start with space, and cannot end with space'.",
			groups = BasicInfo.class)
	private String firstName;

	@Size(max = 50, message = "Middle Name must not exceed 50 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^(?![ .])[\\p{L}.]{0,50}$",
			message = "Middle Name can only contain alphabets and '.', and cannot start with space or '.'.",
			groups = BasicInfo.class)
	private String middleName;

	@NotEmpty(message = "Last Name is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^\\p{L}+$", message = "Last Name can only contain alphabets with no spaces.",
			groups = BasicInfo.class)
	private String lastName;

}