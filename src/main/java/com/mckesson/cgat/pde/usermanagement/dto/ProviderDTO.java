package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Name;
import com.mckesson.cgat.pde.usermanagement.model.validators.BasicInfo;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProviderDTO {

	private Long practiceId;

	@Valid
	private Name name;

	@NotBlank(message = "DEA Number is required", groups = BasicInfo.class)
	@Size(min = 9, max = 9, message = "DEA Number must be exactly 9 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^[A-Za-z]{2}\\d{7}$",
			message = "DEA Number must start with 2 letters followed by 7 digits (e.g., AB1234567)",
			groups = BasicInfo.class)
	@Column(name = "dea", length = 9, nullable = false)
	private String dea;

	@NotBlank(message = "NPI Number is required", groups = BasicInfo.class)
	@Size(min = 10, max = 10, message = "NPI must be exactly 10 digits", groups = BasicInfo.class)
	@Pattern(regexp = "\\d{10}", message = "NPI must contain only numeric digits", groups = BasicInfo.class)
	@Column(name = "npi", length = 10, nullable = false)
	private String npi;

	@NotEmpty(message = "Phone Number is required", groups = BasicInfo.class)
	@Pattern(regexp = "^[1-9][0-9]{9}$",
			message = "Phone number must be exactly 10 digits, start with 1-9, and contain only numbers",
			groups = BasicInfo.class)
	private String phone;

	@Valid
	private Address address;

}