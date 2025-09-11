package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.notification.config.NotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.service.impl.ContactUsEmailBuilder;
import com.mckesson.cgat.pde.usermanagement.notification.service.impl.OnbehalfLoginEmailBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateFactory {

	private final Map<NotificationEventType, EmailTemplateBuilder> builders = new HashMap<>();

	public EmailTemplateFactory() {
		builders.put(NotificationEventType.ON_BEHALF_LOGIN, new OnbehalfLoginEmailBuilder());
		builders.put(NotificationEventType.CONTACT_US, new ContactUsEmailBuilder());
	}

	public EmailTemplateBuilder getBuilder(NotificationEventType eventType) {
		if (!builders.containsKey(eventType)) {
			throw new IllegalArgumentException("Unsupported email type: " + eventType);
		}
		return builders.get(eventType);
	}

}
