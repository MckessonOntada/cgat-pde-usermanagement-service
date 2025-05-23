package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.notification.config.InAppNotificationCategory;
import com.mckesson.cgat.pde.usermanagement.notification.config.InAppNotificationEventType;

public interface InAppNotificationEventService {

	void saveInAppNotification(Long triggerId, InAppNotificationCategory category,
			InAppNotificationEventType eventType);

}
