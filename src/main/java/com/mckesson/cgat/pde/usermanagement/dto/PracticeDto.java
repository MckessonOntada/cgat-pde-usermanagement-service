package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeDto {

	private String id;

	@NotNull
	@NotEmpty(message = "Practice name cannot be empty")
	private String practice_name;

	private String practice_logo_url;

	@NotNull
	@NotEmpty(message = "First name cannot be empty")
	private String contact_first_name;

	private String contact_middle_name;

	@NotNull
	@NotEmpty(message = "Last name cannot be empty")
	private String contact_last_name;

	@NotNull
	@NotEmpty(message = "Address line 1 cannot be empty")
	private String address1;

	@NotNull
	@NotEmpty(message = "Address line 2 cannot be empty")
	private String address2;

	@NotNull
	@NotEmpty(message = "City cannot be empty")
	private String city;

	@NotNull
	@NotEmpty(message = "State cannot be empty")
	private String state;

	@NotNull
	@NotEmpty(message = "Zip cannot be empty")
	private String zip;

	@NotNull
	@Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	@NotEmpty(message = "Email cannot be empty")
	private String email;

	@NotNull
	@NotEmpty(message = "Phone Number cannot be empty")
	private String phone_number;

	private String fax;
}
