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

	@Size(min = 2, max = 100, groups = BasicInfo.class)
	@Pattern(regexp = "^[-0-9A-Za-z,./ ]+$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.",
			groups = BasicInfo.class)
	@NotEmpty(message = "Address 1 is required.")
	private String address1;

	@Size(max = 100, groups = BasicInfo.class)
	@Pattern(regexp = "^[0-9A-Za-z-,./ ]*$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.",
			groups = BasicInfo.class)
	private String address2;

	@NotEmpty(message = "City is required.")
	@Size(min = 2, max = 50, groups = BasicInfo.class)
	@Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "City must only contain alphabets.", groups = BasicInfo.class)
	private String city;

	@Size(min = 2, max = 50, groups = BasicInfo.class)
	@NotEmpty(message = "State is required.")
	private String state;

	@NotEmpty(message = "ZIP code is required")
	@Size(min = 2, max = 10, message = "Enter a valid Zip Code.", groups = BasicInfo.class)
	@Pattern(regexp = "^[\\d]+$", message = "Must be numeric", groups = BasicInfo.class)
	private String zip;

}
