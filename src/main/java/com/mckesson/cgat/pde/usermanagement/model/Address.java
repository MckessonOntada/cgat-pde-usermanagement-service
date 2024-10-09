package com.mckesson.cgat.pde.usermanagement.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
	@NotEmpty(message = "zip cannot be empty")
	private String zip;

}
