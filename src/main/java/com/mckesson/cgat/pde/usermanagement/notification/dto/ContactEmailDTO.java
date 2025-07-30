package com.mckesson.cgat.pde.usermanagement.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ContactEmailDTO extends EmailNotificationDTO {

	@NotBlank(message = "Please enter the First Name.")
	private String firstName;

	@NotBlank(message = "Please enter the Last Name.")
	private String lastName;

	@NotBlank(message = "Please enter the Title.")
	private String title;

	@NotBlank(message = "Please enter the Work Email.")
	private String workEmail;

	@NotBlank(message = "Please enter the Phone Number.")
	private String phoneNumber;

	@NotBlank(message = "Please enter the Company Name.")
	private String company;

	private String tellUsMore;

}
