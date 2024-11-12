package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "Email ID is required")
	@Email(message = "Email ID is required and must be in a valid format.")
	private String email;

	@NotEmpty(message = "Phone Number is required")
	@Pattern(regexp = "\\d{10}", message = "Phone Number must be in a valid format")
	private String phone;

	@Pattern(message = "Fax must be a valid format (e.g., +1-123-456-7890, (123) 456-7890, 123-456-7890)", regexp = "")
	private String fax;

}
