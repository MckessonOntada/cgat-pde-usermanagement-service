package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Contact;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {

	@Size(min = 1, max = 100, message = "Account name must not exceed 100 characters.")
	@Pattern(regexp = "^(?![&\\-])[A-Za-z0-9 &\\-]*(?<![&\\-])$",
			message = "Only letters, numbers, spaces, '&', and '-' are allowed, and the name cannot start or end with '&' or '-'")
	private String accountName;

	@NotEmpty(message = "First Name is required.")
	@Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ.-]+(?: [A-Za-zÀ-ÖØ-öø-ÿ.-]+)*$",
			message = "First Name must only contain alphabets, spaces, '.', and '-' are allowed, "
					+ " no starting/ending space")
	private String firstName;

	@Size(max = 50, message = "Must only contain alphabets.")
	@Pattern(regexp = "^(?! )[A-Za-zÀ-ÖØ-öø-ÿ.]+$",
			message = "Middle Name must contain alphabetic and Dot characters only," + " no starting/ending space")
	private String middleName;

	@NotEmpty(message = "Last Name is required")
	@Size(min = 1, max = 50, message = "Last name must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ](?:[A-Za-zÀ-ÖØ-öø-ÿ]*[A-Za-zÀ-ÖØ-öø-ÿ])?$",
			message = "Last Name must only contain alphabets, no starting/ending space")
	private String lastName;

	@Valid
	private Contact contact;

	@Valid
	private Address address;

}
