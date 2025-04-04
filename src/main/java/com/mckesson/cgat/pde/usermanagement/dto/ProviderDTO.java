package com.mckesson.cgat.pde.usermanagement.dto;

import com.mckesson.cgat.pde.usermanagement.model.Address;
import com.mckesson.cgat.pde.usermanagement.model.Name;
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

	@NotBlank(message = "DEA Number is required")
	@Size(min = 9, max = 9, message = "DEA Number must be exactly 9 characters")
	private String dea;

	@NotBlank(message = "NPI Number is required")
	@Size(min = 10, max = 10, message = "NPI must be exactly 10 digits")
	@Pattern(regexp = "\\d{10}", message = "NPI must contain only numeric digits")
	private String npi;

	@NotEmpty(message = "Phone Number is required")
	@Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
	private String phone;

	@Valid
	private Address address;

	private Boolean isActive;

}