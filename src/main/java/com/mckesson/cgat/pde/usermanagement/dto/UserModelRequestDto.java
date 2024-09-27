package com.mckesson.cgat.pde.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModelRequestDto {

	@NotNull(message = "First name must not be null")
	@NotEmpty
	private String firstName;

	private String middleName;

	@NotNull(message = "Last name must not be null")
	@NotEmpty
	private String lastName;

	@NotNull(message = "Suffix must not be null")
	@NotEmpty
	private String suffix;

	@NotNull(message = "address_1 must not be null")
	@NotEmpty
	private String address_1;

	private String address_2;

	@NotNull(message = "City must not be null")
	@NotEmpty
	private String city;

	@NotNull(message = "State must not be null")
	@NotEmpty
	private String state;

	@NotNull(message = "Zip must not be null")
	@NotEmpty
	private String zip;

	@Email
	@NotNull(message = "Zip must not be null")
	@NotEmpty
	private String emailId;

	@NotNull(message = "Phone number must not be null")
	@NotEmpty
	private String phoneNumber;
	
	@NotEmpty
	private Boolean isActive = false;

}
