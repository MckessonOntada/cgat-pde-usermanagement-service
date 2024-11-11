package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "First name cannot be null")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Za-z]+$", message = "First Name must contain alphabetic characters only")
	private String firstName;

	@Size(max = 50, message = "Middle name must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Za-z]+$", message = "Middle Name must contain alphabetic characters only")
	private String middleName;

	@NotEmpty(message = "Last name cannot be empty")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String lastName;

	@NotNull(message = "Suffix must not be null")
	@NotEmpty
	private String suffix;

}
