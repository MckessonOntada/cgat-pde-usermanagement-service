package com.mckesson.cgat.pde.usermanagement.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;

@Data
public class ContactUsDTO {

	@NotBlank(message = "Please enter the First Name.")
	@Size(max = 50, message = "First Name must not exceed 50 characters.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s,\\-]+$",
			message = "Only alphabets, numbers, spaces, commas, and hyphens are allowed.")
	private String firstName;

	@NotBlank(message = "Please enter the Last Name.")
	@Size(max = 50, message = "Last Name must not exceed 50 characters.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s,\\-]+$",
			message = "Only alphabets, numbers, spaces, commas, and hyphens are allowed.")
	private String lastName;

	@NotBlank(message = "Please enter the Title.")
	@Size(max = 50, message = "Title must not exceed 50 characters.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s,\\-]+$",
			message = "Only alphabets, numbers, spaces, commas, and hyphens are allowed.")
	private String title;

	@NotBlank(message = "Please enter the Work Email.")
	@Size(max = 50, message = "Work Email must not exceed 50 characters.")
	@Email(message = "Please enter a valid email address (e.g., name@example.com).")
	private String workEmail;

	@NotBlank(message = "Please enter the Phone Number.")
	@Pattern(regexp = "^[0-9]{10}$",
			message = "Phone Number must be exactly 10 digits. Only numeric values are allowed.")
	private String phoneNumber;

	@NotBlank(message = "Please enter the Company Name.")
	@Size(max = 50, message = "Company Name must not exceed 50 characters.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s,\\-]+$",
			message = "Only alphabets, numbers, spaces, commas, and hyphens are allowed.")
	private String company;

	@Size(max = 2000, message = "Tell us more must not exceed 2000 characters.")
	@Pattern(regexp = "^[a-zA-Z0-9\\s,\\-]*$",
			message = "Only alphabets, numbers, spaces, commas, and hyphens are allowed.")
	private String tellUsMore;

}
