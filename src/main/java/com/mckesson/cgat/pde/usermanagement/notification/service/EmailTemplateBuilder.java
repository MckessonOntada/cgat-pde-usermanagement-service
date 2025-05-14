package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.dto.EmailNotificationDTO;

public interface EmailTemplateBuilder {

	EmailNotificationDTO buildTemplate(LoggedInUserDetails user, EmailNotificationConfig config, String practiceName);

}
