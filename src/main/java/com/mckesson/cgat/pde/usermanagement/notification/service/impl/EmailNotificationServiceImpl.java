package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.notification.client.RestNotificationClient;
import com.mckesson.cgat.pde.notification.payload.EmailNotification;
import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConstants;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationStatus;
import com.mckesson.cgat.pde.usermanagement.notification.dto.ContactEmailDTO;
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

import java.util.HashMap;
import java.util.Map;

import static com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConstants.CONTACT_US_TEMPLATE;

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
        } catch (IllegalArgumentException e) {
            log.error("Failed to process email notification: {}", e);
            savePendingEmailNotification(user, eventType);
        }

    }

    @Override
    public void sendContactUsNotification(ContactUsDTO dto) {
        try {
            ContactUsEmailBuilder builder = (ContactUsEmailBuilder) emailTemplateFactory
                    .getBuilder(EmailNotificationEventType.CONTACT_US);
            ContactEmailDTO emailDTO = builder.buildTemplate(dto, emailNotificationConfig, dto.getCompany());

            EmailNotificationUtil.validateDTO(emailDTO, validator);

            Map<String, String> templateData = EmailNotificationUtil.convertObjectToMap(emailDTO);

            String recipientEmail = "Coleman.Harris@McKesson.com";
            EmailNotification emailNotification = EmailNotificationUtil.createEmailNotificationPayload(templateData,
                    recipientEmail, EmailNotificationConstants.CONTACT_US_TEMPLATE, dto.getPhoneNumber(),
                    EmailNotificationConstants.CONTACT_US_TEMPLATE, EmailNotificationEventType.CONTACT_US.name(), null,
                    emailNotificationConfig.getHookActionURI());
            restNotificationClient.sendNotification(emailNotification).thenAcceptAsync(response -> {
                log.info("Contact Us notification sent successfully for: {}", dto.getWorkEmail());
                saveContactNotificationEvent(dto, emailNotification, EmailNotificationStatus.SUCCESS);
            }).exceptionally(ex -> {
                log.error("Contact Us Notification failed for email: {}", dto.getWorkEmail(), ex);
                saveContactNotificationEvent(dto, emailNotification, EmailNotificationStatus.FAILED);
                return null;
            });

        } catch (IllegalArgumentException e) {
            log.error("Validation failed for Contact Us email notification: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process Contact Us notification: {}", e.getMessage(), e);
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

    private void saveContactNotificationEvent(ContactUsDTO dto, EmailNotification notification,
                                              EmailNotificationStatus status) {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .userId(dto.getWorkEmail())
                .emailNotificationEventType(EmailNotificationEventType.CONTACT_US)
                .emailNotificationStatus(status)
                .emailNotification(notification)
                .build();
        emailNotificationEventRepository.save(event);
    }

}
