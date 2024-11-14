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
public class Name implements Serializable {

	private static final long serialVersionUID = 1L;

	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[A-Za-z]+$", message = "First Name must only contain alphabets.")
	private String firstName;

	@Size(max = 50, message = "Must only contain alphabets.")
	private String middleName;

	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[A-Za-z]+$", message = "Last Name must only contain alphabets.")
	private String lastName;

}
