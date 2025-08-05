package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.dto.ContactEmailDTO;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailTemplateBuilder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ContactUsEmailBuilder implements EmailTemplateBuilder<ContactUsDTO> {

	@Override
	public ContactEmailDTO buildTemplate(ContactUsDTO dto, EmailNotificationConfig config, String practiceName) {

		return ContactEmailDTO.builder()
			.receiverName(dto.getFirstName() + " " + dto.getLastName())
			.organizationName(dto.getCompany())
			.supportContact(config.getSupportContact())
			.linkPortal(config.getLinkPDEPortal())
			.firstName(dto.getFirstName())
			.lastName(dto.getLastName())
			.title(dto.getTitle())
			.workEmail(dto.getWorkEmail())
			.phoneNumber(dto.getPhoneNumber())
			.company(dto.getCompany())
			.tellUsMore(dto.getTellUsMore())
			.supportEmail(config.getSupportEmail())
			.build();
	}

}
