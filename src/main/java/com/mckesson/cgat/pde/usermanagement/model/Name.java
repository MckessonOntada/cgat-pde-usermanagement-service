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
	@Pattern(regexp = "^(?!\\p{Zs})(?!.*\\p{Zs}$)[\\p{L}\\p{Zs}.-]+$",
			message = "First Name can only contain alphabets, spaces, '.', and '-', and cannot start or end with space.",
			groups = BasicInfo.class)
	private String firstName;

	@Size(max = 50, message = "Middle Name must not exceed 50 characters.", groups = BasicInfo.class)
	@Pattern(regexp = "^(|[\\p{L}.\u2026]{0,50})$", message = "Middle Name can only contain alphabets and '.'.",
			groups = BasicInfo.class)
	private String middleName;

	@NotEmpty(message = "Last Name is required.", groups = BasicInfo.class)
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = BasicInfo.class)
	@Pattern(regexp = "^\\p{L}+$", message = "Last Name can only contain alphabets with no spaces.",
			groups = BasicInfo.class)
	private String lastName;

	public String getFullName() {
		if (middleName == null || middleName.trim().isEmpty()) {
			return firstName + " " + lastName;
		}
		return firstName + " " + middleName + " " + lastName;
	}

	@Override
	public String toString() {
		return getFullName();
	}

}