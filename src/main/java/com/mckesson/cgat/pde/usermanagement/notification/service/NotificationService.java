package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.notification.config.NotificationEventType;
import jakarta.validation.Valid;

public interface NotificationService {

	void saveNotification(Long id, NotificationEventType notificationEventType);

	long saveContactUsMetadata(@Valid ContactUsDTO contactUsDTO);

}
