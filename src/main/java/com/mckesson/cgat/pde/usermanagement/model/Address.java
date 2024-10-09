package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Address  implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "Address1 must not be null")
	@NotEmpty
	private String address1;

	private String address2;

	@NotNull(message = "City must not be null")
	@NotEmpty
	@Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
	private String city;
	
	@NotNull(message = "Zip must not be null")
	@NotEmpty
	@Size()
	@Size(min = 2, max = 10, message = "Zip code must be between 2 and 10 characters")
	private String zip;

	@NotNull(message = "State must not be null")
	@NotEmpty
	@Size(min = 2, max = 50, message = "State name must be between 2 and 50 characters")
	private String state;

}
