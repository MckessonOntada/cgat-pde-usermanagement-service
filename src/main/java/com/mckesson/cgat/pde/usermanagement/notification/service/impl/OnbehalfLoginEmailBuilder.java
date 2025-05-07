package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.dto.EmailNotificationDTO;
import com.mckesson.cgat.pde.usermanagement.notification.dto.OnBehalfEmailDTO;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailTemplateBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OnbehalfLoginEmailBuilder implements EmailTemplateBuilder {

	// Define a formatter for consistent date/time formatting
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
		.ofPattern("MMM dd, yyyy 'at' hh:mm a");

	@Override
	public EmailNotificationDTO buildTemplate(LoggedInUserDetails user, EmailNotificationConfig config,
			String practiceName) {

		String formattedDateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);

		return OnBehalfEmailDTO.builder()
			.receiverName(user.getName().getFullName())
			.supportContact(config.getSupportContact())
			.linkPortal(config.getLinkPDEPortal())
			.organizationName(practiceName)
			.loginDateTime(formattedDateTime)
			.adminName(user.getImpersonatedByUser().getName().getFullName())
			.build();
	}

}