package com.mckesson.cgat.pde.usermanagement.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class OnBehalfEmailDTO extends EmailNotificationDTO {

	@NotNull(message = "Admin Name is required")
	private String adminName;

	@NotNull(message = "Time of login is required")
	private String loginDateTime;

}
