package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.Embeddable;
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
	@NotEmpty(message = "Address cannot be empty")
	private String address1;

	private String address2;

	@NotNull(message = "city cannot be null")
	@NotEmpty(message = "city cannot be empty")
	private String city;

	@NotNull(message = "state cannot be null")
	@NotEmpty(message = "state cannot be empty")
	private String state;

	@NotNull(message = "zip cannot be null")
	@Pattern(regexp = "^\\d{5}", message = "Zip code must be 5 digits")
	private String zip;

}
