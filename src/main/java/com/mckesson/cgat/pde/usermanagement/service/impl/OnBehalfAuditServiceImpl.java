package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.OnBehalfAudit;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.config.InAppNotificationCategory;
import com.mckesson.cgat.pde.usermanagement.notification.config.InAppNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailNotificationService;
import com.mckesson.cgat.pde.usermanagement.notification.service.InAppNotificationEventService;
import com.mckesson.cgat.pde.usermanagement.repository.OnBehalfAuditRepository;
import com.mckesson.cgat.pde.usermanagement.service.OnBehalfAuidtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OnBehalfAuditServiceImpl implements OnBehalfAuidtService {

	@Autowired
	private OnBehalfAuditRepository auditRepository;

	@Autowired
	private InAppNotificationEventService inAppNotificationEventService;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Override
	@Transactional
	public void logImpersonation(LoggedInUserDetails impersonatedUser, LoggedInUserDetails actualUser) {
		OnBehalfAudit log = OnBehalfAudit.builder()
			.impersonatedUserId(impersonatedUser.getId())
			.userEmail(actualUser.getContact().getEmail())
			.impersonatedUserName(impersonatedUser.getName().getFullName())
			.userId(actualUser.getId())
			.userName(actualUser.getName().getFullName())
			.practiceId(actualUser.getPracticeId())
			.impersonatedUserEmail(impersonatedUser.getContact().getEmail())
			.timestamp(LocalDateTime.now())
			.build();

		OnBehalfAudit savedLog = auditRepository.save(log);
		emailNotificationService.sendOnBehalfEmail(EmailNotificationEventType.ON_BEHALF_LOGIN);
		inAppNotificationEventService.saveInAppNotification(savedLog.getId(), InAppNotificationCategory.ON_BEHALF,
				InAppNotificationEventType.ON_BEHALF_LOGIN);
	}

}