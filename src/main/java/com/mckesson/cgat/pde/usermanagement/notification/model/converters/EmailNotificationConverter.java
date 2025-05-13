package com.mckesson.cgat.pde.usermanagement.notification.model.converters;

import com.mckesson.cgat.pde.notification.payload.EmailNotification;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class EmailNotificationConverter implements AttributeConverter<EmailNotification, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(EmailNotification attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		}
		catch (Exception e) {
			throw new RuntimeException("Conversion error", e);
		}
	}

	@Override
	public EmailNotification convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, EmailNotification.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Conversion error", e);
		}
	}

}
