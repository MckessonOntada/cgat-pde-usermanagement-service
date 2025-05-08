package com.mckesson.cgat.pde.usermanagement.service.impl;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.model.OnBehalfAudit;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationEventType;
import com.mckesson.cgat.pde.usermanagement.notification.service.EmailNotificationService;
import com.mckesson.cgat.pde.usermanagement.repository.OnBehalfAuditRepository;
import com.mckesson.cgat.pde.usermanagement.service.OnBehalfAuidtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OnBehalfAuditServiceImpl implements OnBehalfAuidtService {

	@Autowired
	private OnBehalfAuditRepository auditRepository;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Override
	public void logImpersonation(LoggedInUserDetails impersonatedUser, LoggedInUserDetails actualUser) {
		OnBehalfAudit log = OnBehalfAudit.builder()
			.impersonatedUserId(impersonatedUser.getId())
			.userEmail(actualUser.getContact().getEmail())
			.impersonatedUserName(impersonatedUser.getUserName())
			.userId(actualUser.getId())
			.userName(actualUser.getUserName())
			.practiceId(actualUser.getPracticeId())
			.impersonatedUserEmail(impersonatedUser.getContact().getEmail())
			.timestamp(LocalDateTime.now())
			.build();

		auditRepository.save(log);
		emailNotificationService.sendOnBehalfEmail(EmailNotificationEventType.ON_BEHALF_LOGIN);
	}

}