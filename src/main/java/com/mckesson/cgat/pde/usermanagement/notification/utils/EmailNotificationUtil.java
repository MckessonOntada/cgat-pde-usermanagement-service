package com.mckesson.cgat.pde.usermanagement.notification.utils;

import com.mckesson.cgat.pde.notification.enums.LanguageEnum;
import com.mckesson.cgat.pde.notification.enums.NotificationContentSelection;
import com.mckesson.cgat.pde.notification.payload.EmailNotification;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EmailNotificationUtil {

	public static EmailNotification createEmailNotificationPayload(Map<String, String> metaData, String sentToEmail,
			String createdSource, String sourceId, String templateId, String eventType, String practiceId,
			String hookActionURI) {
		String uniqueSourceId = createUniqueSourceId(sourceId, sentToEmail, eventType);
		log.info("Creating EmailNotification Payload for SourceId:{}", uniqueSourceId);
		return EmailNotification.builder()
			.to(sentToEmail)
			.metaData(metaData)
			.notificationContentSelection(NotificationContentSelection.EMAIL_HTML)
			.hookActionURI(hookActionURI)
			.createdAt(new Date())
			.language(LanguageEnum.en_EN)
			.practiceId(practiceId)
			.source(createdSource)
			.sourceId(uniqueSourceId)
			.templateId(templateId)
			.eventType(eventType)
			.build();
	}

	public static <T> void validateDTO(T object, Validator validator) {
		Set<ConstraintViolation<T>> violations = validator.validate(object);

		if (!violations.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Validation errors: ");
			for (ConstraintViolation<T> violation : violations) {
				errorMessage.append(violation.getPropertyPath())
					.append(" ")
					.append(violation.getMessage())
					.append("; ");
			}
			log.error("Failed to validate template DTO: {}", object);
			throw new IllegalArgumentException(errorMessage.toString());
		}
	}

	public static <T> Map<String, String> convertObjectToMap(T templateDataDTO) {
		Map<String, String> map = new HashMap<>();
		if (templateDataDTO == null) {
			log.error("Template DTO should not be null");
			throw new IllegalArgumentException("Template DTO should not be null");
		}

		Class<?> templateDataDTOClass = templateDataDTO.getClass();
		while (templateDataDTOClass != null) {
			// Loop through all fields, including those from parent classes
			for (Field field : templateDataDTOClass.getDeclaredFields()) {
				field.setAccessible(true);
				try {
					map.put(field.getName(), String.valueOf(field.get(templateDataDTO)));
				}
				catch (IllegalAccessException e) {
					log.error("Unable to process template DTO");
					throw new IllegalArgumentException("Unable to process template DTO");
				}
			}
			// Move to the parent class
			templateDataDTOClass = templateDataDTOClass.getSuperclass();
		}

		log.info("Template DTO map created: {}", map);
		return map;
	}

	private static String createUniqueSourceId(String sourceId, String email, String event) {
		long time = System.currentTimeMillis();
		return new StringBuilder(sourceId).append("_")
			.append(email)
			.append("_")
			.append(event)
			.append("_")
			.append(time)
			.toString();
	}

}
