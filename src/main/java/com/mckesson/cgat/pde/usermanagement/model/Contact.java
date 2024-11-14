package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
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
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

  @NotEmpty(message = "Email ID is required")
	@Email(regexp = "^[_a-zA-Z0-9!#$%&'*+/=?``{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Must be valid email format")
	@Size(min = 2, max = 100, message = "Enter a valid Email Address")
	@Column(unique = true)
	private String email;

	@Size(min = 2, max = 5, message = "Must be valid Country code")
	@Pattern(regexp = "^[+0-9]+$", message = "Must be numeric")
	private String countryCode;

  @NotEmpty(message = "Phone Number is required")
	@Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
	@Column(unique = true)
	private String phone;

	@Size(max = 15)
	@Pattern(regexp = "^[\\d ]*$", message = "Must be numeric")
	private String fax;
}
