package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
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
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Address cannot be null")
	@NotEmpty(message = "Address 1 is required.")
	private String address1;

	private String address2;

	@NotNull(message = "City cannot be null")
	@NotEmpty(message = "City is required.")
	@Pattern(regexp = "^[A-Za-z]+$", message = "City must contain alphabetic characters only")
	private String city;

	@NotNull(message = "state cannot be null")
	@NotEmpty(message = "State is required.")
	private String state;

	@NotBlank(message = "ZIP code is required")
	@Pattern(regexp = "^\\d{5}(-?\\d{4})?$", message = "ZIP code must be 5 digits or 9 digits in extended format (e.g., 12345 or 12345-6789)")
	private String zip;

}
