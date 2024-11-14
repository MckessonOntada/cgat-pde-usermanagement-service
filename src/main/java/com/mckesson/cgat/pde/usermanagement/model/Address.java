package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
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

	@Size(min = 2, max = 100)
	@Pattern(regexp = "^[-0-9A-Za-z,./ ]+$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.")
	private String address1;

	@Size(max = 100)
	@Pattern(regexp = "^[0-9A-Za-z-,./ ]*$",
			message = "Alphanumeric, along with below chararcters allowed Space, hypen, comma, dot. slash.")
	private String address2;

	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[A-Za-z ]+$", message = "City must only contain alphabets.")
	private String city;

	@Size(min = 2, max = 50)
	private String state;

	@Size(min = 2, max = 10, message = "Enter a valid Zip Code.")
	@Pattern(regexp = "^[\\d]+$", message = "Must be numeric")
	private String zip;

}
