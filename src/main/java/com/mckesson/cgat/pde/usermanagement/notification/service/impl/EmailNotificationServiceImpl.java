package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.notification.client.RestNotificationClient;
import com.mckesson.cgat.pde.notification.payload.EmailNotification;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConstants;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationStatus;
import com.mckesson.cgat.pde.usermanagement.notification.dto.EmailNotificationDTO;
import com.mckesson.cgat.pde.usermanagement.notification.model.EmailNotificationEvent;
import com.mckesson.cgat.pde.usermanagement.notification.repository.EmailNotificationEventRepository;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailNotificationService;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailTemplateFactory;
import com.mckesson.cgat.pde.usermanagement.notification.utils.EmailNotificationUtil;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {

	private EmailNotificationEventRepository emailNotificationEventRepository;

	private EmailNotificationConfig emailNotificationConfig;

	private PracticeRepository practiceRepository;

	private Validator validator;

	private EmailTemplateFactory emailTemplateFactory;

	private RestNotificationClient restNotificationClient;

	public EmailNotificationServiceImpl(EmailNotificationEventRepository emailNotificationEventRepository,
			EmailNotificationConfig emailNotificationConfig, Validator validator,
			EmailTemplateFactory emailTemplateFactory, PracticeRepository practiceRepository) {
		this.emailNotificationEventRepository = emailNotificationEventRepository;
		this.emailNotificationConfig = emailNotificationConfig;
		this.validator = validator;
		this.emailTemplateFactory = emailTemplateFactory;
		this.practiceRepository = practiceRepository;
		restNotificationClient = new RestNotificationClient(this.emailNotificationConfig.getRestClientUrl());
	}

	@Override
	public void sendOnBehalfEmail(EmailNotificationEventType eventType) {
		LoggedInUserDetails user = UserSecurityUtils.getLoggedInUser();
		try {

			Map<String, String> templateData = buildTemplateData(user, eventType);
			EmailNotification emailNotification = EmailNotificationUtil.createEmailNotificationPayload(templateData,
					user.getUserName(), EmailNotificationConstants.ON_BEHALF_LOGIN_TEMPLATE, user.getId(),
					getTemplateForEvent(eventType), String.valueOf(eventType), String.valueOf(user.getPracticeId()),
					emailNotificationConfig.getHookActionURI());
			sendEmailNotification(emailNotification, user, eventType);
		}
		catch (IllegalArgumentException e) {
			log.error("Failed to process email notification: {}", e);
			savePendingEmailNotification(user, eventType);
		}

	}

	private void saveEmailNotification(LoggedInUserDetails user, EmailNotificationEventType emailNotificationEventType,
			EmailNotification emailNotification, EmailNotificationStatus status) {
		EmailNotificationEvent emailNotificationEvent = EmailNotificationEvent.builder()
			.userId(String.valueOf(user.getId()))
			.emailNotificationEventType(emailNotificationEventType)
			.emailNotificationStatus(status)
			.emailNotification(emailNotification)
			.build();

		emailNotificationEventRepository.save(emailNotificationEvent);

	}

	private void savePendingEmailNotification(LoggedInUserDetails user,
			EmailNotificationEventType emailNotificationEventType) {
		EmailNotificationEvent emailNotificationEvent = EmailNotificationEvent.builder()
			.userId(String.valueOf(user.getId()))
			.emailNotificationEventType(emailNotificationEventType)
			.emailNotificationStatus(EmailNotificationStatus.PENDING)
			.build();

		emailNotificationEventRepository.save(emailNotificationEvent);

	}

	private Map<String, String> buildTemplateData(LoggedInUserDetails user, EmailNotificationEventType eventType) {
		String practiceName = practiceRepository.findNameById(user.getPracticeId()).orElse(null);
		EmailNotificationDTO emailDTO = emailTemplateFactory.getBuilder(eventType)
			.buildTemplate(user, emailNotificationConfig, practiceName);
		EmailNotificationUtil.validateDTO(emailDTO, validator);
		return EmailNotificationUtil.convertObjectToMap(emailDTO);

	}

	private String getTemplateForEvent(EmailNotificationEventType eventType) {
		switch (eventType) {
			case ON_BEHALF_LOGIN:
				return EmailNotificationConstants.ON_BEHALF_LOGIN_TEMPLATE;
			default:
				throw new IllegalArgumentException("Unsupported event type: " + eventType);
		}
	}

	private void sendEmailNotification(EmailNotification emailNotification, LoggedInUserDetails user,
			EmailNotificationEventType emailNotificationEventType) {
		restNotificationClient.sendNotification(emailNotification).thenAcceptAsync(response -> {
			log.info("Notification sent successfully to: {}", user.getName());
			saveEmailNotification(user, emailNotificationEventType, emailNotification, EmailNotificationStatus.SUCCESS);
		}).exceptionally(ex -> {
			log.error("Email Notification failed for sourceId: {}", emailNotification.getSourceId());
			saveEmailNotification(user, emailNotificationEventType, emailNotification, EmailNotificationStatus.FAILED);
			return null;
		});
	}

}
