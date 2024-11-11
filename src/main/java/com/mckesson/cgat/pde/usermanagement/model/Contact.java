package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@NotNull
	@NotEmpty(message = "Email ID is required and must not be in a empty")
	@Email(message = "Email ID is required and must be in a valid format.")
	private String email;

	@NotEmpty(message = "Phone Number is required and must be in a valid format.")
	@Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
	private String phone;

	@Pattern(regexp = "^\\+?[0-9. ()-]{7,20}$", message = "Fax must be a valid format (e.g., +1-123-456-7890 or (123) 456-7890)")
	private String fax;

}
