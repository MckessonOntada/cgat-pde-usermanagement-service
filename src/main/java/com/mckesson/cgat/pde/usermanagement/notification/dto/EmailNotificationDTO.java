package com.mckesson.cgat.pde.usermanagement.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class EmailNotificationDTO {

	@NotNull(message = "Receiver Name is required")
	private String receiverName;

	@NotNull(message = "Organisation Name is required")
	private String organizationName;

	@NotNull(message = "Support Contact is required")
	private String supportContact;

	@NotNull(message = "Link to PDE portal is required")
	private String linkPortal;

}
