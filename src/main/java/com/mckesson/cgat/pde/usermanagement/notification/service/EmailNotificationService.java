package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.dto.ContactUsDTO;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;

public interface EmailNotificationService {

	void sendOnBehalfEmail(EmailNotificationEventType eventType);

	void sendContactUsNotification(ContactUsDTO dto);

}
