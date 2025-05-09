package com.mckesson.cgat.pde.usermanagement.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
@Configuration
public class EmailNotificationConfig {

	private String restClientUrl;

	private String hookActionURI;

	private String supportContact;

	private String linkPDEPortal;

}
