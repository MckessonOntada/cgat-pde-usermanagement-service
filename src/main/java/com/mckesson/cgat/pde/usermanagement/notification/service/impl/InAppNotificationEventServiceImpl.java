package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.InAppNotificationEvent;
import com.mckesson.cgat.pde.usermanagement.notification.config.*;
import com.mckesson.cgat.pde.usermanagement.notification.repository.InAppNotificationEventRepository;
import com.mckesson.cgat.pde.usermanagement.notification.service.InAppNotificationEventService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InAppNotificationEventServiceImpl implements InAppNotificationEventService {

	private InAppNotificationEventRepository inAppNotificationEventRepository;

	public InAppNotificationEventServiceImpl(InAppNotificationEventRepository inAppNotificationEventRepository) {
		this.inAppNotificationEventRepository = inAppNotificationEventRepository;
	}

	@Override
	public void saveInAppNotification(Long triggerId, InAppNotificationCategory category,
			InAppNotificationEventType eventType) {
		InAppNotificationEvent inAppNotificationEvent = InAppNotificationEvent.builder()
			.triggerId(triggerId)
			.notificationCategory(category)
			.notificationEventType(eventType)
			.build();
		inAppNotificationEventRepository.save(inAppNotificationEvent);
	}

}
