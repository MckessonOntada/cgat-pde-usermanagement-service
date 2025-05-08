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
	@Pattern(regexp = "^(?! )[A-Za-z0-9,./-]+(?: [A-Za-z0-9,./-]+)*(?<! )$",
			message = "Address 1 must be alphanumeric and may include space, hyphen, comma, dot, or slash. It must not start or end with a space.",
			groups = BasicInfo.class)
	@NotEmpty(message = "Address 1 is required.", groups = BasicInfo.class)
	private String address1;

	@Size(max = 100, message = "Address 2 must be at most 100 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^(|(?!\\s)[0-9A-Za-z., \\/-]*(?<!\\s))$",
			message = "Address 2 must be alphanumeric and may include space, hyphen, comma, dot, or slash. It must not start or end with a space.",
			groups = BasicInfo.class)
	private String address2;

	@NotEmpty(message = "City is required.", groups = BasicInfo.class)
	@Size(max = 50, message = "City should not exceed 50 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^(?! )[A-Za-z ]*(?<! )$",
			message = "City name can only contain alphabets and spaces and must not start or end with a space.",
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
	@Pattern(regexp = "^(\\d{5}|\\d{5}-\\d{4})$",
			message = "ZIP code must be in the format 12345 or 12345-6789, with no spaces or invalid characters.",
			groups = BasicInfo.class)
	private String zip;

}
