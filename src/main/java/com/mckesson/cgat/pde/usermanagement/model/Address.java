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
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Size(min = 1, max = 100, message = "Address 1 must be between 1 and 100 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^[-0-9A-Za-z,./ ]+$",
			message = "Address 1 must be alphanumeric with allowed characters: space, hyphen, comma, period, slash.",
			groups = BasicInfo.class)
	@NotEmpty(message = "Address 1 is required.", groups = BasicInfo.class)
	private String address1;

	@Size(max = 100, message = "Address 2 must be at most 100 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^[0-9A-Za-z-,./ ]*$",
			message = "Address 2 must be alphanumeric with allowed characters: space, hyphen, comma, period, slash.",
			groups = BasicInfo.class)
	private String address2;

	@NotEmpty(message = "City is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 50, message = "City must be between 2 and 50 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "City must contain only alphabetic characters and spaces.",
			groups = BasicInfo.class)
	private String city;

	@Size(min = 1, max = 50, message = "State must be between 1 and 50 characters.", groups = BasicInfo.class)
	@NotEmpty(message = "State is required.", groups = BasicInfo.class)
	@Pattern(regexp = "^(?! )[A-Za-z ]*(?<! )$",
			message = "State must contain only alphabetic characters and spaces, and cannot start or end with a space.",
			groups = BasicInfo.class)
	private String state;

	@NotEmpty(message = "ZIP code is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 10, message = "ZIP code must be between 2 and 10 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^\\d{5}(-\\d{4})?$",
			message = "ZIP code must be 5 digits or 9 digits with last 4 digits seperated by a '-' .",
			groups = BasicInfo.class)
	private String zip;

}
