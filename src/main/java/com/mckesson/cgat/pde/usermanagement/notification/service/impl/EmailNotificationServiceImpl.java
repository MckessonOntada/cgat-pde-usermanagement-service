package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.model.ContactUsMetadata;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.NotificationEvent;
import com.mckesson.cgat.pde.usermanagement.notification.config.NotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.service.NotificationService;
import com.mckesson.cgat.pde.usermanagement.repository.ContactUsMetadataRepository;
import com.mckesson.cgat.pde.usermanagement.repository.NotificationEventRepository;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {

	private final ContactUsMetadataRepository contactUsMetadataRepository;

	private final NotificationEventRepository notificationEventRepository;

	@Value("${contact.us.support.email}")
	private String recipientEmail;

	public EmailNotificationServiceImpl(ContactUsMetadataRepository contactUsMetadataRepository,
			NotificationEventRepository notificationEventRepository) {
		this.contactUsMetadataRepository = contactUsMetadataRepository;
		this.notificationEventRepository = notificationEventRepository;
	}

	@Override
	public void saveNotification(Long triggerId, NotificationEventType notificationEventType) {
		LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();
		NotificationEvent notificationEvent = NotificationEvent.builder()
			.triggerId(triggerId)
			.notificationEventType(notificationEventType)
			.createdAt(LocalDateTime.now())
			.createdBy(loggedInUser != null ? loggedInUser.getUserName() : "system")
			.build();
		notificationEventRepository.save(notificationEvent);
	}

	@Override
	public long saveContactUsMetadata(ContactUsDTO contactUsDTO) {
		ContactUsMetadata contactUsMetadata = new ContactUsMetadata();
		contactUsMetadata.setFormData(contactUsDTO);
		ContactUsMetadata savedContactUSData = contactUsMetadataRepository.save(contactUsMetadata);
		return savedContactUSData.getId();
	}

}
