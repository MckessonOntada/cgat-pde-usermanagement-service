package com.mckesson.cgat.pde.usermanagement.notification.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.InAppNotificationEvent;
import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.notification.config.*;
import com.mckesson.cgat.pde.usermanagement.notification.repository.InAppNotificationEventRepository;
import com.mckesson.cgat.pde.usermanagement.notification.service.InAppNotificationEventService;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InAppNotificationEventServiceImpl implements InAppNotificationEventService {

	private InAppNotificationEventRepository inAppNotificationEventRepository;

	public InAppNotificationEventServiceImpl(InAppNotificationEventRepository inAppNotificationEventRepository) {
		this.inAppNotificationEventRepository = inAppNotificationEventRepository;
	}

	@Override
	public void saveInAppNotification(Long triggerId, InAppNotificationCategory category,
			InAppNotificationEventType eventType) {
		LoggedInUserDetails loggedInUser = UserSecurityUtils.getLoggedInUser();
		InAppNotificationEvent inAppNotificationEvent = InAppNotificationEvent.builder()
			.triggerId(triggerId)
			.notificationCategory(category)
			.notificationEventType(eventType)
			.createdAt(LocalDateTime.now())
			.createdBy(loggedInUser != null ? loggedInUser.getUserName() : "system")
			.build();
		inAppNotificationEventRepository.save(inAppNotificationEvent);
	}

}
