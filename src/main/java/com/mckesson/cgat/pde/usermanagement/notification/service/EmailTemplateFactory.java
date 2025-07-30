package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.service.impl.ContactUsEmailBuilder;
import com.mckesson.cgat.pde.usermanagement.notification.service.impl.OnbehalfLoginEmailBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateFactory {

	private final Map<EmailNotificationEventType, EmailTemplateBuilder> builders = new HashMap<>();

	public EmailTemplateFactory() {
		builders.put(EmailNotificationEventType.ON_BEHALF_LOGIN, new OnbehalfLoginEmailBuilder());
		builders.put(EmailNotificationEventType.CONTACT_US, new ContactUsEmailBuilder());
	}

	public EmailTemplateBuilder getBuilder(EmailNotificationEventType eventType) {
		if (!builders.containsKey(eventType)) {
			throw new IllegalArgumentException("Unsupported email type: " + eventType);
		}
		return builders.get(eventType);
	}

}
