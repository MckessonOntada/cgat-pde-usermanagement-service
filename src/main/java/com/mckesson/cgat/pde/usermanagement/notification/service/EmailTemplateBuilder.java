package com.mckesson.cgat.pde.usermanagement.notification.service;

import com.mckesson.cgat.pde.usermanagement.notification.config.EmailNotificationConfig;
import com.mckesson.cgat.pde.usermanagement.notification.dto.EmailNotificationDTO;

public interface EmailTemplateBuilder<T> {

	EmailNotificationDTO buildTemplate(T input, EmailNotificationConfig config, String practiceName);

}
