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
public class Name implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "First Name is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^[\\p{L}\\p{P}\\p{S}\\p{Zs}\\p{N}]+$",
			message = "First Name allows alphabets, Numbers, special, accented or space characters only.",
			groups = BasicInfo.class)
	private String firstName;

	@Size(max = 50, message = "Must only contain alphabets.", groups = BasicInfo.class)
	@Pattern(regexp = "^(?!\\s+$)[\\p{L}\\p{P}\\p{S}\\p{Zs}\\p{N}]*$",
			message = "Middle Name allows alphabets, Numbers, special, accented or space characters only.",
			groups = BasicInfo.class)
	private String middleName;

	@NotEmpty(message = "Last Name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^[\\p{L}\\p{P}\\p{S}\\p{Zs}\\p{N}]+$",
			message = "Last Name allows alphabets, Numbers, special, accented or space characters only.",
			groups = BasicInfo.class)
	private String lastName;

}
